package ms.gerente.rabbit;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ms.gerente.util.Transformer;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import shared.GenericData;
import shared.Message;

/*
 * Usage
 * 1.Mensagem simples que não espera resposta: sendMessage, ou convertAndSend no próprio método
 * 
 * 2.Requisição-Resposta: para requisições que esperam respostas, sendRequest irá armazenar o pedido
 * no Cache, assim que receber a resposta irá emiti-lo e removê-lo do Cache
 */


@Component
public class Producer {
    
    @Autowired RabbitTemplate rabbitTemplate;

    private final Cache<String, MonoSink<GenericData<?>>> monoSinkCache;

    public Producer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        this.monoSinkCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    }

    //Simple RabbitMQ docs implementation
    public void sendMessage(Message<?> message) { 
        rabbitTemplate.convertAndSend(message.getTarget(), message);
    } 

    //Async non-blocking request-response
    public Mono<GenericData<?>> sendRequest(Message<?> request){
        System.out.println("Producer::sendRequest");
        String correlationId = request.getId();

        Mono<GenericData<?>> responseMono = Mono.<GenericData<?>>create(sink -> {
            monoSinkCache.put(correlationId, sink);
            rabbitTemplate.convertAndSend("exchange", request.getTarget(), request, msg -> {
                msg.getMessageProperties().setCorrelationId(correlationId);
                msg.getMessageProperties().setReplyTo(request.getReplyTo());
                return msg;
            });
            })
            .timeout(Duration.ofSeconds(10))
            .onErrorResume(throwable -> {
                monoSinkCache.invalidate(correlationId);
                return Mono.justOrEmpty(null);
            });
        return responseMono;
    }

    //Handling response from sendRequest
    @RabbitListener(queues = "gerente.response")
    public void handleResponse(Message<?> message){
        String correlationId = message.getId();
        MonoSink<GenericData<?>> sink = monoSinkCache.getIfPresent(correlationId);
        if(sink != null){
            GenericData<?> response = convertDataFromMsg(message);
            sink.success(response);
            monoSinkCache.invalidate(correlationId);
        }
        
    }

    private GenericData<?> convertDataFromMsg(Message<?> message){
        return Transformer.transform(message.getData(), GenericData.class);
    }

}
