package ms.conta.util;

import org.modelmapper.ModelMapper;

public class Transformer {
    
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <T> T transform (Object source, Class<T> destinationClass){
        if(source == null) return null;
        return modelMapper.map(source, destinationClass);
    }

}
