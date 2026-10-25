package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The provisioned accounts of an app. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdProvisionedAccountList(
        String object,
        List<AtlasIdProvisionedAccount> data,
        /** The account-wide cap (default 25). */ int limit,
        /** Test accounts the account holds across all its apps. */ int total) {
}
