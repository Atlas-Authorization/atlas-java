package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReaderDomainSetup(
        String object,
        String id,
        String role,
        String host,
        String status,
        String action,
        boolean live,
        String cnameTarget,
        Long lastCheckedAt,
        String lastObservedTarget,
        String failureReason,
        Long certificateExpiresAt,
        String cookieDomain,
        List<DnsInstruction> instructions,
        String note) {
}
