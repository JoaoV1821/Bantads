package ms.saga.alteracaodeperfil;

import java.util.List;

import ms.saga.Workflow;
import ms.saga.WorkflowStep;

public class UpdateProfileWorkflow implements Workflow{
    
    private final List<WorkflowStep> steps;

    public UpdateProfileWorkflow(List<WorkflowStep> steps){
        this.steps = steps;
    }

    @Override
    public List<WorkflowStep> getSteps(){
        return this.steps;
    }

}
