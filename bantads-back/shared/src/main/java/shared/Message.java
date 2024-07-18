package shared;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message<T> implements Serializable{

    private String id; //UUID  
    private String request; //METHOD
    private GenericData<T> data; //DTO, OBJECT, LIST
    private String target; //TARGET QUEUE
    private String replyTo; //RESPONSE QUEUE

}
