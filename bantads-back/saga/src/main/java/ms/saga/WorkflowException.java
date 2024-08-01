package ms.saga;

public class WorkflowException extends RuntimeException{
    
    public WorkflowException(String message){
        super(message);
    }

}
