package org.finos.legend.engine.plan.execution.authorization.ranger;

import org.finos.legend.engine.shared.core.identity.Identity;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class RangerMiddleTierAuthorizerTest {
    private static final Logger LOG = LoggerFactory.getLogger(RangerMiddleTierAuthorizerTest.class);

    @Test
    public void policyCheck() throws Exception{
        RangerMiddleTierAuthorizer sut = new RangerMiddleTierAuthorizer();
        sut.evaluate(new Identity("ferngi"), null, null);
    }
}