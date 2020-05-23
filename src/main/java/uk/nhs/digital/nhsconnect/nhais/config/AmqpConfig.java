package uk.nhs.digital.nhsconnect.nhais.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    @Value("${nhais.amqp.meshOutboundQueueName}")
    String queueName;

    @Value("${nhais.amqp.exchange}")
    String exchange;

    @Value("${nhais.amqp.brokers}")
    String brokers;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory ccf = new CachingConnectionFactory();
        ccf.setAddresses(brokers);
        return ccf;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        // use same routing key as queue name as convention for direct exchanges
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
