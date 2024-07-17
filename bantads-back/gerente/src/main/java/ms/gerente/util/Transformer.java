package ms.gerente.util;

import org.modelmapper.ModelMapper;

/*
 * Usage
 * DTO dto = Transformer.transform(obj, DTO.class)
 */


public class Transformer {
    
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <T> T transform (Object source, Class<T> destinationClass){
        return modelMapper.map(source, destinationClass);
    }

}

