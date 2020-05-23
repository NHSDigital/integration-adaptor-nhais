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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class AmqpConfig {
    @Value("${nhais.amqp.meshOutboundQueueName}")
    private String queueName;

    @Value("${nhais.amqp.exchange}")
    private String exchange;

    @Value("${nhais.amqp.brokers}")
    private String brokers;

    @Value("${nhais.amqp.username}")
    private String username;

    @Value("${nhais.amqp.password}")
    private String password;

    @Value("${nhais.amqp.maxAttempts}")
    private int maxAttempts;

    @Value("${nhais.amqp.backOffPeriod}")
    private long backOffPeriod;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory ccf = new CachingConnectionFactory();
        ccf.setAddresses(brokers);
        ccf.setShuffleAddresses(true);
        // if username/password are set (even to empty string) then anonymous is not used and breaks local development
        if(!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            ccf.setUsername(username);
            ccf.setPassword(password);
        }
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
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(backOffPeriod);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);
        rabbitTemplate.setRetryTemplate(retryTemplate);
        return rabbitTemplate;
    }
}
