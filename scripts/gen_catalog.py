#!/usr/bin/env python3
"""Generate the long-tail Backend API / Platform API surface of the Java SDK
from the checked-in OpenAPI snapshot.

Usage (from sdks/java):

    python3 scripts/gen_catalog.py            # regenerate from scripts/openapi.snapshot.json (a copy of the monorepo's refreshed snapshot)
    python3 scripts/gen_catalog.py --spec F   # or another local spec file
    python3 scripts/gen_catalog.py --report   # also print counts

Reproducible: CI regenerates from the checked-in snapshot and fails on any diff.
To pick up API changes: refresh the snapshot (sdks/rust/scripts/refresh_snapshot.py),
regenerate, review the diff (additions are fine).

What it writes (everything under src/main/java/net/atlasauth/atlas/catalog is
generated; hand-written code is never touched):

  catalog/Catalog.java          client.catalog(): one accessor per namespace
  catalog/<Ns>Api.java          one method per operation, with overloads for the
                                optional query object and an Idempotency-Key
                                (writes the API accepts it on), and a
                                <name>All(..) Iterable for cursor lists
  catalog/models/*.java         request models (mutable, fluent setters) and
                                response models (records); query option classes
  src/test/resources/catalog-ops.tsv   every generated op, for CatalogOpsTest

Which operations: every BAPI + PLATFORM operation of the spec that the
hand-written code (everything outside catalog/) does not already call.

Published signatures are pinned so a regeneration never breaks callers
(scripts/catalog_pins.json):
  method_names              "METHOD /path" -> method name kept (a renamed heuristic
                            or a new overload never moves a published method)
  no_idempotency            ops that shipped without the idempotencyKey overload
  generate_despite_handwritten  ops generated though a hand-written helper shares the route
  skip                      "METHOD /path" -> reason: not a server-SDK call
                            (streaming); mirrored into the drift allowlist
A pin the spec no longer reaches fails the run.

Type mapping: object with properties -> class (requests) / record (responses);
enum/const/format -> String (a new server value never breaks parsing); integer
-> Long; number -> Double; array -> List<T>; map -> Map<String, T>; anyOf/oneOf of
more than one shape and the empty schema -> Object.
"""
import json, os, re, sys, glob, shutil

HERE = os.path.dirname(os.path.abspath(__file__))
JAVA = os.path.dirname(HERE)
SRC = os.path.join(JAVA, 'src/main/java/net/atlasauth/atlas')
OUT = os.path.join(SRC, 'catalog')
MODELS = os.path.join(OUT, 'models')
TSV = os.path.join(JAVA, 'src/test/resources/catalog-ops.tsv')
PKG = 'net.atlasauth.atlas.catalog'
METHODS = ('get', 'post', 'put', 'patch', 'delete')

# a copy lives beside this script so the public mirror of this SDK can regenerate on its own
spec_path = os.path.join(HERE, 'openapi.snapshot.json')
if '--spec' in sys.argv:
    spec_path = sys.argv[sys.argv.index('--spec') + 1]
spec = json.load(open(spec_path))
comps = spec['components']['schemas']
PINS = json.load(open(os.path.join(HERE, 'catalog_pins.json')))
USED_PINS = set()

# ── naming helpers ───────────────────────────────────────────────────────────
JAVA_KW = set('''abstract assert boolean break byte case catch char class const continue default do double else enum
extends final finally float for goto if implements import instanceof int interface long native new package private
protected public return short static strictfp super switch synchronized this throw throws transient try void volatile
while true false null _ var record yield clone finalize getClass hashCode notify notifyAll toString wait equals'''.split())
RESERVED_TYPES = set('''Object String Long Double Boolean List Map Void Integer Number Override Deprecated Record Error
Exception Iterable Math Class Enum Thread Process Runnable Comparable Character Byte Short Float System Package Module
Iterator Set Collection Params Pagination Transport TypeReference JsonProperty JsonIgnoreProperties CursorPage Catalog
Optional Objects Arrays Collections Date Instant Duration URI'''.split())

def words(s):
    s = re.sub(r'([a-z0-9])([A-Z])', r'\1 \2', s)
    return [w for w in re.split(r'[^A-Za-z0-9]+', s) if w]

def pascal(s):
    return ''.join(w[:1].upper() + w[1:] for w in words(s))

def camel(s):
    w = words(s)
    if not w:
        return 'v'
    first = w[0].lower() if w[0].isupper() else w[0][:1].lower() + w[0][1:]
    return first + ''.join(x[:1].upper() + x[1:] for x in w[1:])

def ident(s):
    c = camel(s)
    if c[0].isdigit():
        c = '_' + c
    return c + '_' if c in JAVA_KW else c

def tname(s):
    p = pascal(s) or 'Model'
    if p[0].isdigit():
        p = 'N' + p
    return 'Atlas' + p if p in RESERVED_TYPES else p

def doc(s):
    if not s:
        return ''
    s = re.sub(r'\s+', ' ', str(s)).strip()
    s = s.replace('\\', '&#92;').replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')
    s = s.replace('*/', '*&#47;').replace('@', '&#64;').replace('{', '&#123;').replace('}', '&#125;')
    return s

def jdoc(s, indent='    '):
    d = doc(s)
    return f'{indent}/** {d} */\n' if d else ''

# ── schema typing ────────────────────────────────────────────────────────────
def deref(s):
    seen = 0
    while isinstance(s, dict) and '$ref' in s and seen < 20:
        s = comps[s['$ref'].split('/')[-1]]
        seen += 1
    return s

def strip_null(s):
    """Drop null from a type list / anyOf so an optional-nullable collapses to its value type."""
    if not isinstance(s, dict):
        return s
    for k in ('anyOf', 'oneOf'):
        if k in s:
            keep = [x for x in s[k] if not (isinstance(x, dict) and x.get('type') == 'null')]
            if len(keep) == 1:
                merged = dict(keep[0]) if '$ref' not in keep[0] else keep[0]
                return merged
    t = s.get('type')
    if isinstance(t, list):
        keep = [x for x in t if x != 'null']
        if len(keep) == 1:
            s = dict(s)
            s['type'] = keep[0]
    return s

def flatten(s):
    """Object schema -> (properties, required) merging allOf, or None when it is not a plain object.
    A oneOf/anyOf whose every branch is an object becomes one model with the union of the properties
    (all optional): the typed view of a discriminated union."""
    s = strip_null(deref(s))
    if not isinstance(s, dict):
        return None
    props, req = {}, set()
    t = s.get('type')
    objish = t in (None, 'object') or (isinstance(t, list) and 'object' in t)
    if not objish:
        return None
    if 'allOf' in s:
        for part in s['allOf']:
            f = flatten(part)
            if f is None:
                d = strip_null(deref(part))
                if isinstance(d, dict) and d.get('type') == 'object' and not d.get('properties') and not any(k in d for k in ('anyOf', 'oneOf', 'allOf')):
                    continue  # an open object adds no properties
                return None
            props.update(f[0]); req |= f[1]
    for k in ('oneOf', 'anyOf'):
        if k in s:
            branches = [x for x in s[k] if not (isinstance(x, dict) and x.get('type') == 'null')]
            fl = [flatten(x) for x in branches]
            if not fl or any(x is None for x in fl):
                return None
            common = set.intersection(*[x[1] for x in fl])
            for x in fl:
                for name, sub in x[0].items():
                    if name not in props:
                        props[name] = sub
                    elif json.dumps(props[name], sort_keys=True) != json.dumps(sub, sort_keys=True):
                        props[name] = {'oneOf': [props[name], sub]}  # same property, different shapes per branch
            req |= common
    if 'properties' in s:
        props.update(s['properties']); req |= set(s.get('required', []))
    return (props, req) if props else None

class Model:
    def __init__(self, name, props, req, description):
        self.name, self.props, self.required, self.description = name, props, req, description
        self.is_request = False
        self.fields = None  # [(wire, ident, jtype, doc)]

models = {}
NAMES = {tname(n) for n in comps}   # reserved for components so an inline name never steals one
inline_seen = set()

def uniq_inline(base):
    n, i = tname(base), 2
    cand = n
    while cand in NAMES or cand in inline_seen:
        cand = f'{n}{i}'; i += 1
    inline_seen.add(cand)
    return cand

def build_fields(m, req):
    fields, used = [], set()
    for wire, sub in m.props.items():
        base = ident(wire)
        idn, i = base, 2
        while idn in used:
            idn = f'{base}{i}'; i += 1
        used.add(idn)
        d = (deref(sub) or {}).get('description') if isinstance(sub, dict) else None
        if isinstance(sub, dict) and sub.get('description'):
            d = sub['description']
        en = (deref(sub) or {}).get('enum') if isinstance(sub, dict) else None
        if en and len(en) <= 12:
            d = ((d or '') + ' One of: ' + ', '.join(str(e) for e in en if e is not None) + '.').strip()
        t = jtype(sub, m.name + pascal(wire), req)
        if wire in m.required:
            d = ((d or '') + ' Required.').strip()
        fields.append((wire, idn, t, d))
    m.fields = fields

def register(name, props, req_set, description, req):
    m = models.get(name)
    if m is None:
        m = models[name] = Model(name, props, req_set, description)
        m.is_request = req
        build_fields(m, req)
    elif req and not m.is_request:
        m.is_request = True
        build_fields(m, req)   # re-walk so nested models become mutable request classes too
    return m.name

def prim(s):
    """Java type of a primitive-ish schema without registering models, else None."""
    s = strip_null(deref(s))
    if not isinstance(s, dict):
        return None
    if 'const' in s:
        c = s['const']
        return 'Boolean' if isinstance(c, bool) else 'Long' if isinstance(c, int) else 'Double' if isinstance(c, float) else 'String'
    if 'enum' in s and not s.get('type'):
        return 'String'
    return {'string': 'String', 'integer': 'Long', 'number': 'Double', 'boolean': 'Boolean'}.get(s.get('type'))

def jtype(s, hint, req):
    if s is None or s == {}:
        return 'Object'
    if isinstance(s, dict) and '$ref' in s:
        name = s['$ref'].split('/')[-1]
        target = comps[name]
        f = flatten(target)
        if f is not None and len(f[0]) < 250:
            return register(tname(name), f[0], f[1], target.get('description'), req)
        return jtype(target, name, req)
    s = strip_null(s)
    if '$ref' in s:
        return jtype(s, hint, req)
    if 'const' in s:
        c = s['const']
        return 'Boolean' if isinstance(c, bool) else 'Long' if isinstance(c, int) else 'Double' if isinstance(c, float) else 'String'
    if any(k in s for k in ('allOf', 'properties', 'anyOf', 'oneOf')):
        f = flatten(s)
        if f is not None and len(f[0]) < 250:
            return register(uniq_inline(hint), f[0], f[1], s.get('description'), req)
        if 'allOf' in s or 'properties' in s:
            return 'Map<String, Object>'
        kinds = {prim(x) for x in s.get('oneOf', s.get('anyOf', [])) if not (isinstance(x, dict) and x.get('type') == 'null')}
        return kinds.pop() if len(kinds) == 1 and None not in kinds else 'Object'
    t = s.get('type')
    if isinstance(t, list):
        return 'Object'
    if t == 'string':
        return 'byte[]' if s.get('format') == 'binary' and False else 'String'
    if t == 'integer':
        return 'Long'
    if t == 'number':
        return 'Double'
    if t == 'boolean':
        return 'Boolean'
    if t == 'array':
        return f"List<{jtype(s.get('items'), hint + 'Item', req)}>"
    if t == 'object' or 'additionalProperties' in s:
        ap = s.get('additionalProperties')
        if isinstance(ap, dict) and ap:
            return f"Map<String, {jtype(ap, hint + 'Value', req)}>"
        return 'Map<String, Object>'
    if 'enum' in s:
        return 'String'
    return 'Object'

# ── which operations ─────────────────────────────────────────────────────────
def canon(p):
    return re.sub(r'\{[^}]*\}|\*', '{}', p).rstrip('/')

all_ops = {}
for p, item in spec['paths'].items():
    for m in METHODS:
        o = item.get(m)
        if o and o.get('tags', [''])[0] in ('BAPI', 'PLATFORM'):
            all_ops[(m.upper(), p)] = o

def handwritten_ops():
    """(METHOD, canonical path) pairs the hand-written Java already calls, plus paths it mentions at all."""
    pairs, paths = set(), set()
    lit = r'"(/v1[^"]*)"(?:\s*\+\s*(?:"[^"]*"|[\w.]+(?:\([^()]*(?:\([^()]*\)[^()]*)*\))?))*'
    for f in glob.glob(os.path.join(SRC, '**/*.java'), recursive=True):
        if os.sep + 'catalog' + os.sep in f:
            continue
        text = open(f).read()
        for m in re.finditer(r'(?:request|requestBytes|requestWithHeaders|requestSigned)\(\s*"(GET|POST|PUT|PATCH|DELETE)"\s*,\s*(' + lit + ')', text):
            pairs.add((m.group(1), canon(expr_path(m.group(2)))))
        for m in re.finditer(r'\b(get|post|put|patch|delete|postWithQuery|getRaw|getBytes)\(\s*(' + lit + ')', text):
            verb = {'postWithQuery': 'POST', 'getRaw': 'GET', 'getBytes': 'GET'}.get(m.group(1), m.group(1).upper())
            pairs.add((verb, canon(expr_path(m.group(2)))))
        for m in re.finditer(lit, text):
            paths.add(canon(expr_path(m.group(0))))
    return pairs, paths

def expr_path(expr):
    """A Java path expression ("/v1/x/" + enc(id) + "/y") -> /v1/x/{}/y."""
    out = ''
    for part in re.finditer(r'"([^"]*)"|(\w[\w.]*(?:\([^()]*(?:\([^()]*\)[^()]*)*\))?)', expr):
        out += part.group(1) if part.group(1) is not None else '{}'
    return out.split('?')[0]

pairs, hw_paths = handwritten_ops()
by_canon = {}
for (m, p) in all_ops:
    by_canon.setdefault(canon(p), []).append(m)
for k in PINS['generate_despite_handwritten']:
    if k not in {f'{m} {p}' for (m, p) in all_ops}:
        sys.exit(f'pin generate_despite_handwritten: {k} is not in the spec any more')
    USED_PINS.add(('gdh', k))

def op_fields(o):
    """Property/parameter names reachable from an operation (same walk as scripts/sdk-field-drift.mjs)."""
    out = set()
    def walk(sch, seen, depth):
        if not isinstance(sch, dict) or depth > 4:
            return
        if '$ref' in sch:
            if sch['$ref'] in seen:
                return
            seen.add(sch['$ref'])
            return walk(comps.get(sch['$ref'].split('/')[-1]), seen, depth)
        for k in ('allOf', 'oneOf', 'anyOf'):
            for x in sch.get(k, []):
                walk(x, seen, depth + 1)
        walk(sch.get('items'), seen, depth + 1)
        for k, v in (sch.get('properties') or {}).items():
            out.add(k)
            walk(v, seen, depth + 1)
    for prm in o.get('parameters', []):
        if prm.get('in') == 'query':
            out.add(prm['name'])
    walk(((o.get('requestBody') or {}).get('content', {}).get('application/json') or {}).get('schema'), set(), 0)
    for code in ('200', '201', '202'):
        walk((((o.get('responses') or {}).get(code) or {}).get('content', {}).get('application/json') or {}).get('schema'), set(), 0)
    return {f for f in out if re.match(r'^[A-Za-z_]\w*$', f)}

def hand_idents():
    ids = set()
    for f in glob.glob(os.path.join(SRC, '**/*.java'), recursive=True):
        if os.sep + 'catalog' + os.sep in f:
            continue
        for m in re.finditer(r'[A-Za-z][A-Za-z0-9_]*', open(f).read()):
            ids.add(m.group(0).lower().replace('_', ''))
    return ids
HAND_IDS = hand_idents()

selected = []
skipped = {}
gap_ops = 0
gap_keys = set()
hidden = 0
for (m, p), o in sorted(all_ops.items(), key=lambda kv: (kv[0][1], kv[0][0])):
    key = f'{m} {p}'
    hand = (m, canon(p)) in pairs  # METHOD + path: a verb sharing a path with a hand-written op is still generated
    if hand and key not in PINS['generate_despite_handwritten']:
        # A hand-written op whose request/response carries fields the hand-written models lack is also
        # generated, so those fields are typed (the hand-written method keeps its published signature).
        if any(f.lower().replace('_', '') not in HAND_IDS for f in op_fields(o)):
            gap_ops += 1
            gap_keys.add(key)
        else:
            continue
    if key in PINS['skip']:
        skipped[key] = PINS['skip'][key]; USED_PINS.add(('skip', key))
        continue
    selected.append((m, p, o))
    if canon(p) in hw_paths:
        hidden += 1  # the path-only drift gate counted this as present

def loose_pairs():
    """A second, looser scan (any path literal and any verb in one hand-written method) to catch what the strict scan misses."""
    out = set()
    lit = r'"(/v1[^"]*)"(?:\s*\+\s*(?:"[^"]*"|[\w.]+(?:\([^()]*(?:\([^()]*\)[^()]*)*\))?))*'
    for f in glob.glob(os.path.join(SRC, '**/*.java'), recursive=True):
        if os.sep + 'catalog' + os.sep in f:
            continue
        for chunk in re.split(r'\n    (?=(?:public|private|protected|static)\b)', open(f).read()):
            verbs = {v.upper() for v in re.findall(r'"(GET|POST|PUT|PATCH|DELETE)"', chunk)}
            verbs |= {{'postwithquery': 'POST', 'getraw': 'GET', 'getbytes': 'GET'}.get(v.lower(), v.upper()) for v in re.findall(r'\b(get|post|put|patch|delete|postWithQuery|getRaw|getBytes)\(', chunk)}
            for m in re.finditer(lit, chunk):
                for v in verbs:
                    out.add((v, canon(expr_path(m.group(0)))))
    return out

# A generated op that the hand-written source also calls would be a silent duplicate: stop instead. (Hand-written ops whose
# models lack fields are generated on purpose and are exempt; the strict scan decided them.)
_loose = loose_pairs()
for (m, p, o) in selected:
    key = f'{m} {p}'
    if (m, canon(p)) in _loose and key not in PINS['generate_despite_handwritten'] and key not in gap_keys:
        sys.exit(f'{key} is generated but hand-written source appears to call it too: pin it in generate_despite_handwritten if that is intended, or fix the scan')

# ── namespaces and method names ──────────────────────────────────────────────
def is_param(seg):
    return seg.startswith('{')

def singular(w):
    if w.endswith('ies'):
        return w[:-3] + 'y'
    if w.endswith('s') and not w.endswith(('ss', 'us', 'is')):
        return w[:-1]
    return w

def plural(w):
    return w.endswith('s') and not w.endswith(('ss', 'us', 'is'))

def split_path(p):
    segs = p.strip('/').split('/')[1:]  # drop v1
    if segs and segs[0] == 'platform' and len(segs) > 1:
        return 'platform_' + segs[1], segs[2:]
    return segs[0], segs[1:]

def candidates(method, rest, is_list):
    statics = [s for s in rest if not is_param(s)]
    ends_param = bool(rest) and is_param(rest[-1])
    k0 = 0 if not statics else 1
    def ctx(k, sing):
        ws = statics[-k:] if k else []
        if ws and sing:
            ws = ws[:-1] + [singular(ws[-1])]
        return ''.join(pascal(w) for w in ws)
    out = []
    if method == 'GET':
        verb = 'get' if ends_param or not is_list else 'list'
        for k in range(k0, len(statics) + 1):
            out.append(verb + ctx(k, ends_param))
    elif method == 'POST':
        if not rest:
            out.append('create')
        elif ends_param:
            for k in range(k0, len(statics) + 1):
                out.append('update' + ctx(k, True))
        elif plural(statics[-1]):
            for k in range(1, len(statics) + 1):
                out.append('create' + ctx(k, True))
        else:
            for k in range(1, len(statics) + 1):
                c = ctx(k, False)
                out.append(c[:1].lower() + c[1:])
    else:
        verb = {'PUT': 'set', 'PATCH': 'update', 'DELETE': 'delete'}[method]
        for k in range(k0, len(statics) + 1):
            out.append(verb + ctx(k, ends_param))
    return out

def success(o):
    for code, r in sorted(o.get('responses', {}).items()):
        if code.startswith('2'):
            return code, r
    return None, {}

def resp_is_list(o):
    _, r = success(o)
    sch = ((r.get('content') or {}).get('application/json') or {}).get('schema')
    f = flatten(sch) if sch else None
    return bool(f and 'data' in f[0] and isinstance(strip_null(deref(f[0]['data'])), dict) and strip_null(deref(f[0]['data'])).get('type') == 'array')

class Op: pass

ops = []
used_names = {}      # ns -> set(method names)
type_used = set()
for m, p, o in selected:   # reserve published names first: a new op visited earlier must not take one
    if f'{m} {p}' in PINS['method_names']:
        used_names.setdefault(split_path(p)[0], set()).add(PINS['method_names'][f'{m} {p}'])
for m, p, o in selected:
    ns, rest = split_path(p)
    op = Op()
    op.method, op.path, op.raw, op.ns = m, p, o, ns
    key = f'{m} {p}'
    names = used_names.setdefault(ns, set())
    pin = PINS['method_names'].get(key)
    if pin:
        USED_PINS.add(('mn', key)); name = pin
    else:
        name = None
        for c in candidates(m, rest, resp_is_list(o)):
            if c not in names and c not in JAVA_KW:
                name = c; break
        if name is None:
            base = candidates(m, rest, resp_is_list(o))[-1]
            plist = [pascal(s[1:-1]) for s in rest if is_param(s)]
            for extra in ['By' + ''.join(plist)] + [f'V{i}' for i in range(2, 50)]:
                if base + extra not in names:
                    name = base + extra; break
    names.add(name)
    op.name = ident(name)
    op.type_base = tname(pascal(ns) + pascal(name))
    ops.append(op)

# ── per-op shapes ────────────────────────────────────────────────────────────
for op in ops:
    o, m, p = op.raw, op.method, op.path
    tpl_params = re.findall(r'\{([^}]+)\}', p)
    used = set()
    op.path_params = []
    for n in tpl_params:
        idn = ident(n)
        if idn in ('body', 'query', 'idempotencyKey', 'data', 'contentType'):
            idn += 'Param'
        while idn in used:
            idn += '_'
        used.add(idn)
        op.path_params.append((n, idn))
    q = []
    op.metadata = False
    op.idem = False
    for prm in o.get('parameters', []):
        if '$ref' in prm:
            if prm['$ref'].endswith('/MetadataFilter'):
                op.metadata = True
            continue
        if prm['in'] == 'query':
            q.append(prm)
        elif prm['in'] == 'header' and prm['name'].lower() == 'idempotency-key':
            op.idem = f'{m} {p}' not in PINS['no_idempotency']
            if f'{m} {p}' in PINS['no_idempotency']:
                USED_PINS.add(('ni', f'{m} {p}'))
    op.query = []   # (wire, ident, type, required, desc)
    qused = set()
    for prm in q:
        idn = ident(prm['name'])
        while idn in qused:
            idn += '_'
        qused.add(idn)
        t = jtype(prm.get('schema'), op.type_base + pascal(prm['name']), False)
        d = prm.get('description') or ''
        en = (prm.get('schema') or {}).get('enum')
        if en and len(en) <= 12:
            d = (d + ' One of: ' + ', '.join(str(e) for e in en) + '.').strip()
        op.query.append((prm['name'], idn, t, bool(prm.get('required')), d))
    if op.metadata:
        op.query.append(('metadata', 'metadata', 'Map<String, String>', False, 'Filter by metadata key/value pairs (sent as metadata[key]=value).'))
    op.required_q = [x for x in op.query if x[3]]
    op.qtype = op.type_base + 'Query' if op.query else None
    # request body
    op.body = None        # java type
    op.binary = False
    rb = o.get('requestBody')
    if rb:
        content = rb.get('content', {})
        js = (content.get('application/json') or {}).get('schema')
        if js is not None:
            op.body = jtype(js, op.type_base + 'Body', True)
        elif content:
            op.binary = True
            op.body = 'byte[]'
    # response
    code, r = success(o)
    cont = (r.get('content') or {})
    op.ret = 'void'
    op.raw_text = False
    op.stream = False
    op.ret_model = None
    if 'application/json' in cont:
        op.ret = jtype((cont['application/json'] or {}).get('schema'), op.type_base + 'Response', False)
        op.ret_model = op.ret
    elif 'application/gzip' in cont:
        op.ret = 'byte[]'
    elif 'application/x-ndjson' in cont:
        op.ret = 'String'; op.raw_text = True
    elif 'text/event-stream' in cont:
        op.stream = True
    op.summary = o.get('summary') or ''
    op.scope = o.get('x-required-scope') or o.get('x-scope')
    op.deprecated = bool(o.get('deprecated') or o.get('x-deprecated-since'))

# streaming ops are not a server-SDK call: stay out of the generated surface, listed in the report
ops = [op for op in ops if not (op.stream and skipped.setdefault(f'{op.method} {op.path}', 'a server-sent event stream: needs a streaming client, not a request/response call'))]

# pagination: GET + starting_after + {data: [..], next_cursor}
def page_info(op):
    if op.method != 'GET' or not any(q[0] == 'starting_after' for q in op.query) or not op.ret_model:
        return None
    mdl = models.get(op.ret_model)
    if not mdl:
        return None
    fields = {w: (i, t) for (w, i, t, d) in mdl.fields}
    if 'data' not in fields or not fields['data'][1].startswith('List<'):
        return None
    item = fields['data'][1][5:-1]
    idf = None
    if 'next_cursor' not in fields:
        # the cursor is the last item's id (the spec: "the last item's id")
        im = models.get(item)
        if 'has_more' not in fields or im is None or not any(w == 'id' and t == 'String' for (w, i, t, d) in im.fields):
            return None
        idf = im
    return dict(model=mdl, data=fields['data'][0], next=fields['next_cursor'][0] if 'next_cursor' in fields else None,
                more=fields['has_more'][0] if 'has_more' in fields else None, item=item, item_model=idf)

# ── emit ─────────────────────────────────────────────────────────────────────
HDR = '// Code generated by scripts/gen_catalog.py from the OpenAPI snapshot. DO NOT EDIT.\n'

def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(text)

shutil.rmtree(OUT, ignore_errors=True)

def emit_model(m):
    imp = ['import com.fasterxml.jackson.annotation.JsonIgnoreProperties;', 'import com.fasterxml.jackson.annotation.JsonProperty;', '',
           'import java.util.List;', 'import java.util.Map;', '']
    s = f'{HDR}package {PKG}.models;\n\n' + '\n'.join(imp)
    d = doc(m.description) or f'The {m.name} object.'
    s += f'/** {d} */\n@JsonIgnoreProperties(ignoreUnknown = true)\n'
    if m.is_request:
        s = s.replace('import com.fasterxml.jackson.annotation.JsonIgnoreProperties;', 'import com.fasterxml.jackson.annotation.JsonAnyGetter;\nimport com.fasterxml.jackson.annotation.JsonIgnore;\nimport com.fasterxml.jackson.annotation.JsonIgnoreProperties;', 1)
        s = s.replace('import java.util.List;', 'import java.util.LinkedHashMap;\nimport java.util.List;', 1)
        s += f'public final class {m.name} {{\n'
        for (w, i, t, fd) in m.fields:
            s += jdoc(fd) + f'    @JsonProperty("{w}")\n    public {t} {i};\n'
        s += '    /** Wire names sent as an explicit JSON null (see the clear methods); unset fields are omitted. */\n    @JsonIgnore\n    private final java.util.Set<String> clearedFields = new java.util.LinkedHashSet<>();\n'
        s += f'\n    public {m.name}() {{\n    }}\n\n'
        for (w, i, t, fd) in m.fields:
            s += f'    public {m.name} {i}({t} v) {{ this.{i} = v; clearedFields.remove("{w}"); return this; }}\n'
            if w not in m.required:
                cn = 'clear' + i[:1].upper() + i[1:].rstrip('_')
                s += f'    /** Send {{@code "{w}": null}} to clear the stored value (a PATCH); leaving it unset omits the field. */\n    public {m.name} {cn}() {{ this.{i} = null; clearedFields.add("{w}"); return this; }}\n'
        s += '\n    @JsonAnyGetter\n    Map<String, Object> clearedAsNull() {\n        Map<String, Object> m = new LinkedHashMap<>();\n        for (String f : clearedFields) {\n            m.put(f, null);\n        }\n        return m;\n    }\n}\n'
    else:
        comp = ',\n'.join(f'        @JsonProperty("{w}") {t} {i}' for (w, i, t, fd) in m.fields)
        s += f'public record {m.name}(\n{comp}) {{\n}}\n'
    write(os.path.join(MODELS, m.name + '.java'), s)

def emit_query(op):
    n = op.qtype
    s = f'{HDR}package {PKG}.models;\n\nimport net.atlasauth.atlas.Params;\n\nimport java.util.List;\nimport java.util.Map;\n\n'
    s += f'/** Query parameters of {{@code {op.method} {op.path}}}. */\npublic final class {n} {{\n'
    for (w, i, t, req, d) in op.query:
        s += jdoc(((d or '') + (' Required.' if req else '')).strip()) + f'    public {t} {i};\n'
    if op.required_q:
        args = ', '.join(f'{t} {i}' for (w, i, t, r, d) in op.required_q)
        s += f'\n    /** Every required parameter. */\n    public {n}({args}) {{\n'
        for (w, i, t, r, d) in op.required_q:
            s += f'        this.{i} = {i};\n'
        s += '    }\n'
    else:
        s += f'\n    public {n}() {{\n    }}\n'
    s += '\n'
    for (w, i, t, req, d) in op.query:
        s += f'    public {n} {i}({t} v) {{ this.{i} = v; return this; }}\n'
    # copy: used by the *All iterators so the caller's object is never mutated
    s += f'\n    /** A shallow copy. */\n    public {n} copy() {{\n        {n} c = new {n}({", ".join("this." + i for (w, i, t, r, d) in op.required_q)});\n'
    for (w, i, t, req, d) in op.query:
        if not req:
            s += f'        c.{i} = this.{i};\n'
    s += '        return c;\n    }\n'
    s += '\n    /** The query string entries (null values dropped). */\n    public Map<String, Object> toMap() {\n        Params p = Params.of();\n'
    for (w, i, t, req, d) in op.query:
        if w == 'metadata':
            continue
        s += f'        p.set("{w}", {i});\n'
    s += '        Map<String, Object> m = p.map();\n'
    if op.metadata:
        s += '        Params.putMetadata(m, metadata);\n'
    s += '        return m;\n    }\n}\n'
    write(os.path.join(MODELS, n + '.java'), s)

def path_expr(op):
    parts = re.split(r'(\{[^}]+\})', op.path)
    pm = dict(op.path_params)
    out = []
    for part in parts:
        if not part:
            continue
        out.append(f'enc({pm[part[1:-1]]})' if part.startswith('{') else json.dumps(part))
    return ' + '.join(out)

def emit_ops(op):
    """Method source text (all overloads) for one op, plus its test row."""
    pp = ', '.join(f'String {i}' for (n, i) in op.path_params)
    pn = ', '.join(i for (n, i) in op.path_params)
    body = op.body
    ret = op.ret
    dep = '    @Deprecated\n' if op.deprecated else ''
    head = f'{{@code {op.method} {op.path}}}'
    if op.summary:
        head += ' - ' + doc(op.summary)
    if op.scope:
        head += f' Scope: {{@code {doc(op.scope)}}}.'
    pe = path_expr(op)
    tref = f'new TypeReference<{ret if ret != "void" else "Object"}>() {{\n        }}'
    def sig(name, params):
        return f'    public {ret} {name}({", ".join(x for x in params if x)})'
    def call(query_expr, body_expr, idem_expr):
        if op.binary:
            return (f't.requestBytes("{op.method}", {pe}, {query_expr}, {body_expr}, contentType, {tref})')
        raw = 'true' if op.raw_text else 'false'
        return f't.request("{op.method}", {pe}, {query_expr}, {body_expr}, {idem_expr}, {raw}, {tref})'
    def stmt(c):
        return f'        {"" if ret == "void" else "return "}{c};\n'
    bparam = []
    if body:
        bparam = [f'{body} body'] + (['String contentType'] if op.binary else [])
    qparam = [f'{op.qtype} query'] if op.qtype else []
    qexpr = 'query == null ? null : query.toMap()' if op.qtype else 'null'
    bexpr = 'body' if body else 'null'
    variants = []
    P = [pp] if pp else []
    full = P + bparam + qparam
    # shorthand without the query when every query parameter is optional
    if op.qtype and not op.required_q:
        short = P + bparam
        variants.append((short, call('null', bexpr, 'null'), None))
    variants.append((full, call(qexpr, bexpr, 'null'), None))
    if op.idem and not op.binary:
        variants.append((full + ['String idempotencyKey'], call(qexpr, bexpr, 'idempotencyKey'), 'As above, with an {@code Idempotency-Key} so a retry replays the first response.'))
    s = ''
    first = True
    for params, c, extra in variants:
        j = (f'    /** {head} */\n' if first else f'    /** {extra or "As above, with every optional parameter."} */\n')
        first = False
        s += j + dep + sig(op.name, params) + ' {\n' + stmt(c) + '    }\n\n'
    pg = page_info(op)
    if pg:
        qn = op.qtype
        mdl = pg['model']
        acc = (lambda f: f'r.{f}()') if not mdl.is_request else (lambda f: f'r.{f}')
        more = f'Boolean.TRUE.equals({acc(pg["more"])})' if pg['more'] else f'{acc(pg["next"])} != null'
        if pg['next']:
            nxt = acc(pg['next'])
        else:
            last = f'{acc(pg["data"])}.get({acc(pg["data"])}.size() - 1)'
            idv = f'{last}.id()' if not pg['item_model'].is_request else f'{last}.id'
            nxt = f'({acc(pg["data"])} == null || {acc(pg["data"])}.isEmpty() ? null : {idv})'
        pa = ', '.join(x for x in (P + [f'{qn} query']) if x)
        base_q = f'query == null ? new {qn}() : query.copy()' if not op.required_q else 'query.copy()'
        s += (f'    /** Every item across every page of {{@code {op.method} {op.path}}}, fetched lazily (the cursor is followed for you). */\n'
              f'    public Iterable<{pg["item"]}> {op.name}All({pa}) {{\n'
              f'        final {qn} base = {base_q};\n'
              f'        return Pagination.iterate(cursor -> {{\n'
              f'            {ret} r = {op.name}({(pn + ", ") if pn else ""}base.copy().startingAfter(cursor));\n'
              f'            return new CursorPage<>({acc(pg["data"])}, {more}, {nxt});\n'
              f'        }});\n    }}\n\n')
        if not op.required_q:
            pb = ', '.join(x for x in P)
            s += (f'    /** As above, with no query options. */\n    public Iterable<{pg["item"]}> {op.name}All({pb}) {{\n'
                  f'        return {op.name}All({(pn + ", ") if pn else ""}null);\n    }}\n\n')
    return s

by_ns = {}
for op in ops:
    by_ns.setdefault(op.ns, []).append(op)

rows = []
for ns in sorted(by_ns):
    cls = tname(pascal(ns)) + 'Api'
    s = (f'{HDR}package {PKG};\n\nimport com.fasterxml.jackson.core.type.TypeReference;\n'
         f'import net.atlasauth.atlas.Pagination;\nimport net.atlasauth.atlas.http.Transport;\n'
         f'import net.atlasauth.atlas.models.CursorPage;\nimport {PKG}.models.*;\n\nimport java.util.List;\nimport java.util.Map;\n\n'
         f'import static net.atlasauth.atlas.http.Transport.enc;\n\n'
         f'/** The {{@code /v1/{doc(ns.replace("platform_", "platform/"))}}} operations that have no hand-written method. */\n'
         f'public final class {cls} {{\n\n    private final Transport t;\n\n    public {cls}(Transport t) {{\n        this.t = t;\n    }}\n\n')
    for op in by_ns[ns]:
        s += emit_ops(op)
        full_types = [f'java.lang.String'] * len(op.path_params)
        if op.body:
            full_types.append('byte[]' if op.binary else (f'{PKG}.models.{op.body}' if op.body in models else op.body.replace('<', '&lt;')))
            if op.binary:
                full_types.append('java.lang.String')
        if op.qtype:
            full_types.append(f'{PKG}.models.{op.qtype}')
        rows.append('\t'.join([ns, cls, op.name, op.method, op.path, ','.join(full_types), 'byte[]' if op.binary else '']))
    s = s.rstrip('\n') + '\n}\n'
    write(os.path.join(OUT, cls + '.java'), s)

# Catalog accessors: platform namespaces use the platform-key transport
cat = (f'{HDR}package {PKG};\n\nimport net.atlasauth.atlas.http.Transport;\n\n'
       '/**\n * Every Backend API and Platform API operation that has no hand-written method, one accessor per\n'
       ' * namespace. Reach it as {@code atlas.catalog().<namespace>()}.\n */\npublic final class Catalog {\n\n'
       '    private final Transport t;\n    private final Transport platform;\n\n'
       '    public Catalog(Transport t, Transport platform) {\n        this.t = t;\n        this.platform = platform;\n    }\n\n')
for ns in sorted(by_ns):
    cls = tname(pascal(ns)) + 'Api'
    tr = 'platform' if ns.startswith('platform_') else 't'
    cat += f'    /** The {{@code /v1/{doc(ns.replace("platform_", "platform/"))}}} operations. */\n    public {cls} {ident(ns)}() {{\n        return new {cls}({tr});\n    }}\n\n'
cat = cat.rstrip('\n') + '\n}\n'
write(os.path.join(OUT, 'Catalog.java'), cat)

for op in ops:
    if op.qtype:
        emit_query(op)
for m in sorted(models.values(), key=lambda x: x.name):
    emit_model(m)
os.makedirs(os.path.dirname(TSV), exist_ok=True)
open(TSV, 'w').write('\n'.join(rows) + '\n')

# pins must all be reachable
for key in PINS['method_names']:
    if ('mn', key) not in USED_PINS:
        sys.exit(f'pin method_names: {key} no longer matches a generated operation')
for key in PINS['no_idempotency']:
    if ('ni', key) not in USED_PINS:
        sys.exit(f'pin no_idempotency: {key} no longer matches a generated write with an Idempotency-Key')
for key in PINS['skip']:
    if ('skip', key) not in USED_PINS:
        sys.exit(f'pin skip: {key} no longer matches an operation')

names_all = [m.name for m in models.values()] + [o.qtype for o in ops if o.qtype] + [tname(pascal(n)) + 'Api' for n in by_ns]
low = {}
for n in names_all:
    if low.setdefault(n.lower(), n) != n or names_all.count(n) > 1:
        sys.exit(f'type name collision: {n} vs {low[n.lower()]}')
for k, v in sorted(skipped.items()):
    print('skipped', k, '-', v)
if '--report' in sys.argv or True:
    print(f'{hidden} operations were hidden from the path-only drift gate (a hand-written op shares the path, the verb differs)')
    print(f'{len(ops)} operations ({gap_ops} of them re-typed to carry fields the hand-written models lack) in {len(by_ns)} namespaces, {len(models)} models, {sum(1 for o in ops if o.qtype)} query classes, '
          f'{sum(1 for o in ops if page_info(o))} paginated, {len(skipped)} skipped')
