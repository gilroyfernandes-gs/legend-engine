package org.finos.legend.engine.plan.execution.authorization.ranger;

import org.apache.ranger.admin.client.AbstractRangerAdminClient;
import org.apache.ranger.plugin.util.ServicePolicies;
import org.apache.ranger.plugin.util.ServiceTags;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

public class RangerAdminClientImpl extends AbstractRangerAdminClient {
    private final static String cacheFilename = "legend-policies.json";

    public ServicePolicies getServicePoliciesIfUpdated(long lastKnownVersion, long lastActivationTimeInMillis) throws Exception {

        String basedir = System.getProperty("basedir");
        if (basedir == null) {
            basedir = new File(".").getCanonicalPath();
        }

        java.nio.file.Path cachePath = FileSystems.getDefault().getPath(basedir, "/src/test/resources/" + cacheFilename);
        byte[] cacheBytes = Files.readAllBytes(cachePath);

        return gson.fromJson(new String(cacheBytes), ServicePolicies.class);
    }

    public ServiceTags getServiceTagsIfUpdated(long lastKnownVersion, long lastActivationTimeInMillis) throws Exception {
        return null;

    }

    public List<String> getTagTypes(String tagTypePattern) throws Exception {
        return null;
    }
}
