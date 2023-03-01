package org.apache.ranger.services.legend;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.ranger.plugin.service.RangerBaseService;
import org.apache.ranger.plugin.service.ResourceLookupContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangerServiceLegend extends RangerBaseService {
    private static final Logger LOG = LoggerFactory.getLogger(RangerServiceLegend.class);

    @Override
    public Map<String, Object> validateConfig() throws Exception {
        Map<String, Object> ret = new HashMap<String, Object>();
        return ret;
    }

    @Override
    public List<String> lookupResource(ResourceLookupContext context) throws Exception {
        LOG.debug("lookupResource");
        String servicePaths = configs.get("path");
        return Arrays.asList(servicePaths.split(","));
    }


}

