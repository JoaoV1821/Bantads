package ms.saga.insercaodegerente;

import java.util.List;

import ms.saga.workflow.Workflow;
import ms.saga.workflow.WorkflowStep;

public class InsercaoGerenteWorkflow implements Workflow{
 
    private final List<WorkflowStep> steps;

    public InsercaoGerenteWorkflow(List<WorkflowStep> steps){
        this.steps = steps;
    }

    @Override
    public List<WorkflowStep> getSteps(){
        return this.steps;
    }


}

