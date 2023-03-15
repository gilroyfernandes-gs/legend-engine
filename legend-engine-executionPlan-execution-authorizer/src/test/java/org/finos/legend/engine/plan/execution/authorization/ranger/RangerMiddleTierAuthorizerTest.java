package org.finos.legend.engine.plan.execution.authorization.ranger;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.engine.plan.execution.authorization.PlanExecutionAuthorizerInput;
import org.finos.legend.engine.plan.execution.authorization.PlanExecutionAuthorizerOutput;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.SingleExecutionPlan;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RangerMiddleTierAuthorizerTest {
    private static final Logger LOG = LoggerFactory.getLogger(RangerMiddleTierAuthorizerTest.class);

    @Test
    public void policyCheckSuccess() throws Exception{
        RangerMiddleTierAuthorizer sut = new RangerMiddleTierAuthorizer();

        String user = "user1";
        String serviceGUID = "service1GUID@intergration1/service1/";
        testRequest(sut, user, serviceGUID, true);
    }
    @Test
    @Ignore
    public void policyFailsOnInvalidUser() throws Exception{
        RangerMiddleTierAuthorizer sut = new RangerMiddleTierAuthorizer();

        String user = "dummyUser";
        String serviceGUID = "service1GUID@intergration1/service1/";
        testRequest(sut, user, serviceGUID, false);
    }
    @Test
    public void policyFailsOnAuthorizedService() throws Exception{
        RangerMiddleTierAuthorizer sut = new RangerMiddleTierAuthorizer();

        String user = "ferngi";
        String serviceGUID = "GUIDNOtInPolicy@intergration1/service1/";
        testRequest(sut, user, serviceGUID, false);
    }

    // user ferngi is entitled for all services except serviceGUID
    @Test
    public void testExcludePolicy() throws Exception{
        RangerMiddleTierAuthorizer sut = new RangerMiddleTierAuthorizer();

        String user = "ferngi";
        String serviceGUID = "service2GUID@/integration2/compl/getComplOrderRawCount";
        testRequest(sut, user, serviceGUID, false);
        String unknownUser  = "unknownUser";
            testRequest(sut, unknownUser, serviceGUID, false);
        String undefinedServiceGUID = "serviceNotDefined@/integration2/compl/getComplOrderRawCount";
            testRequest(sut, user, undefinedServiceGUID, false);
            testRequest(sut, unknownUser, undefinedServiceGUID, true);
    }

    @Test
    public void testRowFilters() throws Exception{
        RangerMiddleTierAuthorizer sut = new RangerMiddleTierAuthorizer();

        String user = "admin";
        String database = "memsql_secdic";
        String port = "9001";
        String host =  "dev.memsql.url.com";
        String undefinedServiceGUID = "serviceNotDefined@/integration2/compl/getComplOrderRawCount";
        ImmutableMap<String, String> resourceContext = new UnifiedMap<>(
                Tuples.pair("legend.servicePath", "/api/foobar"),
                Tuples.pair( "legend.database", database),
                Tuples.pair("legend.host", host),
                Tuples.pair("legend.port", port),
                Tuples.pair("legend.usageContext", "EXPLORE_DATA")).toImmutable();

        SingleExecutionPlan plan = new SingleExecutionPlan();
        plan.kerberos = user;
        plan.authDependent = false;

        PlanExecutionAuthorizerInput authorizerInput = new PlanExecutionAuthorizerInput.ExecutionAuthorizationInputBuilder(
                PlanExecutionAuthorizerInput.ExecutionMode.SERVICE_EXECUTION)
                .withResourceContext(resourceContext)
                .build();
        Optional<String> result = sut.getRowFilter(new Identity(user), plan, authorizerInput);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals("col in USD, GBP", result.get());

        result = sut.getRowFilter(new Identity("unAuthorizedUser"), plan, authorizerInput);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isPresent());
    }
    private static void testRequest(RangerMiddleTierAuthorizer sut, String user, String serviceGUID, Boolean expected) throws Exception {
        ImmutableMap<String, String> resourceContext = Maps.immutable.of("legend.servicePath", "/api/foobar", "legend.serviceUniqueId", serviceGUID,
                "legend.usageContext", "SERVICE_EXECUTION");
        testRequest(sut, user, expected, resourceContext);
    }

    private static void testRequest(RangerMiddleTierAuthorizer sut, String user, Boolean expected, ImmutableMap<String, String> resourceContext) throws Exception {
        SingleExecutionPlan plan = new SingleExecutionPlan();
        plan.kerberos = user;
        plan.authDependent = false;

        PlanExecutionAuthorizerInput authorizerInput = new PlanExecutionAuthorizerInput.ExecutionAuthorizationInputBuilder(
                PlanExecutionAuthorizerInput.ExecutionMode.SERVICE_EXECUTION)
                 .withResourceContext(resourceContext)
                     .build();
        PlanExecutionAuthorizerOutput result =  sut.evaluate(new Identity(user), plan, authorizerInput);
        org.junit.Assert.assertEquals(String.format("Expected authorization to be %b but got result %s reason [%s] for request %s", expected, result.isAuthorized(), result.getSummary(), authorizerInput), expected, result.isAuthorized());
    }
}