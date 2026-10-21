package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Your own email provider in development mode: a development instance sends Atlas's sign-in
 * emails without a verified domain.
 *
 * @param active        whether mail goes out through it now; always false on a production instance
 * @param subjectPrefix added to every subject: {@code "[Atlas test] "}
 * @param footer        the line added to the end of every message
 * @param templates     the only emails it sends: Atlas's own sign-in templates
 * @param limits        {@code daily} and {@code per_recipient_hourly}
 * @param note          present for {@code onboarding@resend.dev}: Resend delivers it only to your
 *                      Resend account address
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DevelopmentEmail(
        String object,
        String providerId,
        boolean enabled,
        boolean active,
        String transport,
        String fromAddress,
        String subjectPrefix,
        String footer,
        List<String> templates,
        Map<String, Integer> limits,
        String note,
        String nextStep,
        long updatedAt) {
}
