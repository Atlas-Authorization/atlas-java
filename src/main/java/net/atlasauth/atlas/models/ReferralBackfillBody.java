package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/instance/referrals/backfill}. Unset fields are omitted. */
public final class ReferralBackfillBody {
    /** ISO 8601 date or date-time: users created at or after it. Required. */
    public String since;
    /** customerRef (external_id or user id) to referral code, up to 10,000. Wins over codeMetadataPath. */
    public Map<String, String> codes;
    /** Where each user keeps the code they arrived with, e.g. {@code public_metadata.referral_code}. */
    public String codeMetadataPath;

    public ReferralBackfillBody() {
    }

    public ReferralBackfillBody since(String v) { this.since = v; return this; }
    public ReferralBackfillBody codes(Map<String, String> v) { this.codes = v; return this; }
    public ReferralBackfillBody codeMetadataPath(String v) { this.codeMetadataPath = v; return this; }
}
