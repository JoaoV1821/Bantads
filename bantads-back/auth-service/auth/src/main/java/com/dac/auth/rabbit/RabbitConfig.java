package com.dac.auth.rabbit;

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
        return new Queue("auth", false);
    }

    @Bean Exchange exchange(){
        return new DirectExchange("exchange");
    }

    @Bean Binding binding(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue)
        .to(exchange)
        .with("auth")
        .noargs();
    }

    @Bean Queue queueResponse(){
        return new Queue("auth.response", false);
    }

    @Bean Exchange exchangeResponse(){
        return new DirectExchange("exchange.response");
    }

    @Bean Binding bindingResponse(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue)
        .to(exchange)
        .with("auth.response")
        .noargs();
    }

    @Bean SimpleMessageConverter simpleMessageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setBeanClassLoader(getClass().getClassLoader());
        return converter;
    }


}
