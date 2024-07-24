package ms.gerente.util;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import shared.GenericData;

/*
 * Usage
 * DTO dto = Transformer.transform(obj, DTO.class)
 */


public class Transformer {
    
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <T> T transform (Object source, Class<T> destinationClass){
        return modelMapper.map(source, destinationClass);
    }

    public static <T> List<T> transformList(GenericData<?> list, Class<T> targetClass) {
        if (list == null || list.getList() == null) {
            return List.of();
        }

        return ((List<?>) list.getList()).stream()
            .map(data -> modelMapper.map(data, targetClass))
            .collect(Collectors.toList());
    }

}

