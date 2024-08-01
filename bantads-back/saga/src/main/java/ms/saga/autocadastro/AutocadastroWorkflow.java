package ms.saga.autocadastro;

import java.util.List;

import ms.saga.Workflow;
import ms.saga.WorkflowStep;

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
