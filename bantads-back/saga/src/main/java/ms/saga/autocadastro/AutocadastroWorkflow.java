package ms.saga.autocadastro;

import java.util.List;

import ms.saga.workflow.Workflow;
import ms.saga.workflow.WorkflowStep;

public class AutocadastroWorkflow implements Workflow{
    
    private final List<WorkflowStep> steps;

    public AutocadastroWorkflow(List<WorkflowStep> steps){
        this.steps = steps;
    }

    @Override
    public List<WorkflowStep> getSteps(){
        return this.steps;
    }


}
