package shared;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenericData<T> implements Serializable{

    private T dto;
    private List<T> list;

}
