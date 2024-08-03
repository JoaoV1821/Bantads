package ms.saga.remocaodegerente;

import java.util.List;

import ms.saga.workflow.Workflow;
import ms.saga.workflow.WorkflowStep;

public class RemocaoGerenteWorkflow implements Workflow{
 
    private final List<WorkflowStep> steps;

    public RemocaoGerenteWorkflow(List<WorkflowStep> steps){
        this.steps = steps;
    }

    @Override
    public List<WorkflowStep> getSteps(){
        return this.steps;
    }


}

