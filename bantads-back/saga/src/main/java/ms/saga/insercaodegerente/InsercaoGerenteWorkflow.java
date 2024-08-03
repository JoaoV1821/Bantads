package ms.saga.insercaodegerente;

import java.util.List;

import ms.saga.Workflow;
import ms.saga.WorkflowStep;

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

