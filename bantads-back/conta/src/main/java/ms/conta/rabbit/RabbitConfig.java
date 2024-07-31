package ms.conta.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    @Bean Queue queue(){
        return new Queue("conta", false);
    }

    @Bean Exchange exchange(){
        return new DirectExchange("exchange");
    }

    @Bean Binding binding(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue)
        .to(exchange)
        .with("conta")
        .noargs();
    }

    @Bean Queue queueResponse(){
        return new Queue("conta.response", false);
    }

    @Bean Exchange exchangeResponse(){
        return new DirectExchange("exchange.response");
    }

    @Bean Binding bindingResponse(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue)
        .to(exchange)
        .with("conta.response")
        .noargs();
    }

    @Bean Queue queueQuery(){
        return new Queue("query", false);
    }

    @Bean Exchange exchangeQuery(){
        return new DirectExchange("exchange.query");
    }

    @Bean Binding bindingQuery(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue)
        .to(exchange)
        .with("query")
        .noargs();
    }

    @Bean SimpleMessageConverter simpleMessageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setBeanClassLoader(getClass().getClassLoader());
        return converter;
    }


}
