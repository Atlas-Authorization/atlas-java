package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * The hosted-page / email appearance bag ({@code authConfig.branding}). Every
 * field is optional and authored by a customer. Its wire fields are camelCase, so
 * each multi-word field carries an explicit {@code @JsonProperty} to opt out of
 * the mapper's snake_case default.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandingSettings {
    @JsonProperty("applicationName")
    public String applicationName;
    @JsonProperty("logoUrl")
    public String logoUrl;
    @JsonProperty("colorPrimary")
    public String colorPrimary;
    @JsonProperty("colorBackground")
    public String colorBackground;
    @JsonProperty("colorText")
    public String colorText;
    /** Shown in the email footer; falls back to the application name. */
    @JsonProperty("supportEmail")
    public String supportEmail;

    @JsonProperty("colorAccent")
    public String colorAccent;
    @JsonProperty("colorCard")
    public String colorCard;
    @JsonProperty("colorBorder")
    public String colorBorder;
    /** A CSS length like {@code 12px} or {@code 0.75rem}. */
    @JsonProperty("borderRadius")
    public String borderRadius;
    /** {@code light} | {@code dark} | {@code auto}. */
    public String theme;
    /** A curated stack name or {@code custom}. */
    @JsonProperty("fontFamily")
    public String fontFamily;
    /** When {@code fontFamily === 'custom'}, an https URL to a @font-face stylesheet. */
    @JsonProperty("fontUrl")
    public String fontUrl;
    /** {@code centered} | {@code split} | {@code wall}. */
    public String layout;
    public BrandingBackground background;
    /** Provider display order + which social providers to hide. */
    public Providers providers;
    public Copy copy;
    @JsonProperty("socialButtons")
    public SocialButtons socialButtons;
    /** How a verification code is entered — segmented {@code boxes} (default) or {@code single}. */
    @JsonProperty("codeInput")
    public String codeInput;
    /**
     * Per-audience hosted-page branding keyed by audience hint ({@code [a-z0-9_-]{1,32}}, at most
     * 20). A sign-in started with {@code audience_hint=<hint>} uses that entry. An update merges per
     * hint; a null entry deletes that hint.
     */
    @JsonProperty("audiences")
    public Map<String, BrandingAudience> audiences;
    public Card card;
    public Typography typography;
    @JsonProperty("buttonShape")
    public String buttonShape;
    @JsonProperty("cardBorder")
    public String cardBorder;
    @JsonProperty("inputSize")
    public String inputSize;
    public Interaction interaction;
    @JsonProperty("backgroundPattern")
    public String backgroundPattern;
    @JsonProperty("cardStyle")
    public String cardStyle;
    @JsonProperty("textScale")
    public String textScale;
    @JsonProperty("socialPlacement")
    public String socialPlacement;
    /** A permutation of {@code ['logo','heading','social','form']}. */
    @JsonProperty("sectionOrder")
    public List<String> sectionOrder;
    public Legal legal;
    @JsonProperty("signUpFields")
    public List<SignUpField> signUpFields;
    /** Raw CSS, injected after sanitization. */
    @JsonProperty("customCss")
    public String customCss;
    /** Sanitized HTML slot above the card. */
    @JsonProperty("customHtmlHeader")
    public String customHtmlHeader;
    /** Sanitized HTML slot below the card. */
    @JsonProperty("customHtmlFooter")
    public String customHtmlFooter;

    public BrandingSettings() {
    }

    public BrandingSettings applicationName(String v) { this.applicationName = v; return this; }
    public BrandingSettings logoUrl(String v) { this.logoUrl = v; return this; }
    public BrandingSettings colorPrimary(String v) { this.colorPrimary = v; return this; }
    public BrandingSettings colorBackground(String v) { this.colorBackground = v; return this; }
    public BrandingSettings colorText(String v) { this.colorText = v; return this; }
    public BrandingSettings supportEmail(String v) { this.supportEmail = v; return this; }
    public BrandingSettings colorAccent(String v) { this.colorAccent = v; return this; }
    public BrandingSettings colorCard(String v) { this.colorCard = v; return this; }
    public BrandingSettings colorBorder(String v) { this.colorBorder = v; return this; }
    public BrandingSettings borderRadius(String v) { this.borderRadius = v; return this; }
    public BrandingSettings theme(String v) { this.theme = v; return this; }
    public BrandingSettings fontFamily(String v) { this.fontFamily = v; return this; }
    public BrandingSettings fontUrl(String v) { this.fontUrl = v; return this; }
    public BrandingSettings layout(String v) { this.layout = v; return this; }
    public BrandingSettings background(BrandingBackground v) { this.background = v; return this; }
    public BrandingSettings providers(Providers v) { this.providers = v; return this; }
    public BrandingSettings copy(Copy v) { this.copy = v; return this; }
    public BrandingSettings socialButtons(SocialButtons v) { this.socialButtons = v; return this; }
    public BrandingSettings codeInput(String v) { this.codeInput = v; return this; }
    public BrandingSettings card(Card v) { this.card = v; return this; }
    public BrandingSettings typography(Typography v) { this.typography = v; return this; }
    public BrandingSettings buttonShape(String v) { this.buttonShape = v; return this; }
    public BrandingSettings cardBorder(String v) { this.cardBorder = v; return this; }
    public BrandingSettings inputSize(String v) { this.inputSize = v; return this; }
    public BrandingSettings interaction(Interaction v) { this.interaction = v; return this; }
    public BrandingSettings backgroundPattern(String v) { this.backgroundPattern = v; return this; }
    public BrandingSettings cardStyle(String v) { this.cardStyle = v; return this; }
    public BrandingSettings textScale(String v) { this.textScale = v; return this; }
    public BrandingSettings socialPlacement(String v) { this.socialPlacement = v; return this; }
    public BrandingSettings sectionOrder(List<String> v) { this.sectionOrder = v; return this; }
    public BrandingSettings legal(Legal v) { this.legal = v; return this; }
    public BrandingSettings signUpFields(List<SignUpField> v) { this.signUpFields = v; return this; }
    public BrandingSettings customCss(String v) { this.customCss = v; return this; }
    public BrandingSettings customHtmlHeader(String v) { this.customHtmlHeader = v; return this; }
    public BrandingSettings customHtmlFooter(String v) { this.customHtmlFooter = v; return this; }

    // ---- nested inline shapes ---------------------------------------------

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Providers {
        public List<String> order;
        public List<String> hidden;

        public Providers() {
        }

        public Providers order(List<String> v) { this.order = v; return this; }
        public Providers hidden(List<String> v) { this.hidden = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Copy {
        public String headline;
        public String subheadline;
        public String footer;

        public Copy() {
        }

        public Copy headline(String v) { this.headline = v; return this; }
        public Copy subheadline(String v) { this.subheadline = v; return this; }
        public Copy footer(String v) { this.footer = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class SocialButtons {
        public String variant;
        public String size;
        public String layout;
        public Map<String, String> labels;
        public String search;
        /** Per-provider colour overrides, keyed by provider id. */
        public Map<String, ProviderStyle> styles;

        public SocialButtons() {
        }

        public SocialButtons variant(String v) { this.variant = v; return this; }
        public SocialButtons size(String v) { this.size = v; return this; }
        public SocialButtons layout(String v) { this.layout = v; return this; }
        public SocialButtons labels(Map<String, String> v) { this.labels = v; return this; }
        public SocialButtons search(String v) { this.search = v; return this; }
        public SocialButtons styles(Map<String, ProviderStyle> v) { this.styles = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ProviderStyle {
        public String bg;
        public String fg;

        public ProviderStyle() {
        }

        public ProviderStyle bg(String v) { this.bg = v; return this; }
        public ProviderStyle fg(String v) { this.fg = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Card {
        public String width;
        public String align;
        @JsonProperty("logoSize")
        public String logoSize;

        public Card() {
        }

        public Card width(String v) { this.width = v; return this; }
        public Card align(String v) { this.align = v; return this; }
        public Card logoSize(String v) { this.logoSize = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Typography {
        @JsonProperty("headingSize")
        public String headingSize;
        @JsonProperty("headingWeight")
        public String headingWeight;

        public Typography() {
        }

        public Typography headingSize(String v) { this.headingSize = v; return this; }
        public Typography headingWeight(String v) { this.headingWeight = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Interaction {
        @JsonProperty("buttonStyle")
        public String buttonStyle;
        @JsonProperty("buttonEffect")
        public String buttonEffect;
        public String shadow;
        public String density;
        @JsonProperty("inputStyle")
        public String inputStyle;
        public Boolean animations;
        @JsonProperty("motionSpeed")
        public String motionSpeed;
        @JsonProperty("motionEasing")
        public String motionEasing;
        public String loading;

        public Interaction() {
        }

        public Interaction buttonStyle(String v) { this.buttonStyle = v; return this; }
        public Interaction buttonEffect(String v) { this.buttonEffect = v; return this; }
        public Interaction shadow(String v) { this.shadow = v; return this; }
        public Interaction density(String v) { this.density = v; return this; }
        public Interaction inputStyle(String v) { this.inputStyle = v; return this; }
        public Interaction animations(Boolean v) { this.animations = v; return this; }
        public Interaction motionSpeed(String v) { this.motionSpeed = v; return this; }
        public Interaction motionEasing(String v) { this.motionEasing = v; return this; }
        public Interaction loading(String v) { this.loading = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Legal {
        @JsonProperty("termsUrl")
        public String termsUrl;
        @JsonProperty("privacyUrl")
        public String privacyUrl;
        public Boolean required;

        public Legal() {
        }

        public Legal termsUrl(String v) { this.termsUrl = v; return this; }
        public Legal privacyUrl(String v) { this.privacyUrl = v; return this; }
        public Legal required(Boolean v) { this.required = v; return this; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class SignUpField {
        public String key;
        public String label;
        public String type;
        public Boolean required;
        public String placeholder;
        public List<String> options;

        public SignUpField() {
        }

        public SignUpField key(String v) { this.key = v; return this; }
        public SignUpField label(String v) { this.label = v; return this; }
        public SignUpField type(String v) { this.type = v; return this; }
        public SignUpField required(Boolean v) { this.required = v; return this; }
        public SignUpField placeholder(String v) { this.placeholder = v; return this; }
        public SignUpField options(List<String> v) { this.options = v; return this; }
    }
}
