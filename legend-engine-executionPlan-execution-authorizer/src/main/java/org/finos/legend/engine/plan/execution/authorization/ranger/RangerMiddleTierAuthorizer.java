package org.finos.legend.engine.plan.execution.authorization.ranger;

import org.apache.ranger.plugin.audit.RangerDefaultAuditHandler;
import org.apache.ranger.plugin.policyengine.*;
import org.apache.ranger.plugin.service.RangerBasePlugin;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.finos.legend.engine.plan.execution.authorization.ExecutionAuthorization;
import org.finos.legend.engine.plan.execution.authorization.PlanExecutionAuthorizer;
import org.finos.legend.engine.plan.execution.authorization.PlanExecutionAuthorizerInput;
import org.finos.legend.engine.plan.execution.authorization.PlanExecutionAuthorizerOutput;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.ExecutionPlan;
import org.finos.legend.engine.shared.core.identity.Identity;

import java.util.Collections;

public class RangerMiddleTierAuthorizer implements PlanExecutionAuthorizer {

    private final org.apache.ranger.plugin.service.RangerBasePlugin plugin;

    public RangerMiddleTierAuthorizer()
    {
        plugin = new RangerBasePlugin("legend", "legend");
        plugin.setResultProcessor(new RangerDefaultAuditHandler());
        plugin.init();
    }

    @Override
    public PlanExecutionAuthorizerOutput evaluate(Identity identity, ExecutionPlan executionPlan, PlanExecutionAuthorizerInput authorizationInput) throws Exception {
        RangerAccessResourceImpl resource = new RangerAccessResourceImpl();
        //`resource.setValue("path", "/v1/legend/test/service");
        resource.setValue("service-guid", authorizationInput.getContextParams().get("legend.serviceUniqueId"));
        String accessType = "SERVICE_EXECUTION".equals(authorizationInput.getContextParams().get("legend.usageContext")) ? "execute_service" : "explore_data";
        RangerAccessRequest request = new RangerAccessRequestImpl(resource, accessType, identity.getName(), Collections.emptySet(), Collections.emptySet());
        RangerAccessResult result = plugin.isAccessAllowed(request);
        System.out.println("Result " + result);
        ExecutionAuthorization executionAuthorization = result != null && result.getIsAllowed() ?
                ExecutionAuthorization.authorize("", "", Maps.immutable.empty(), Maps.immutable.empty(),"",  Lists.immutable.empty()) :
                ExecutionAuthorization.deny("", "", Maps.immutable.empty(), Maps.immutable.empty(), "", Lists.immutable.empty());
        return new PlanExecutionAuthorizerOutput("Ranger", "", authorizationInput, executionPlan, Lists.immutable.of(executionAuthorization));
    }

    @Override
    public boolean isMiddleTierPlan(ExecutionPlan executionPlan) {
        return true;
    }

    public static void main(String args[])
    {
       RangerMiddleTierAuthorizer middleTierAuthorizer = new RangerMiddleTierAuthorizer();
       middleTierAuthorizer.isMiddleTierPlan(null);
        try {
            Thread.sleep(6000);
            middleTierAuthorizer.evaluate(new Identity("admin"), null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

