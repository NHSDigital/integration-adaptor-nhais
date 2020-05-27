package uk.nhs.digital.nhsconnect.nhais.config;


import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.policy.JmsDefaultDeserializationPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.util.StringUtils;

import javax.jms.ConnectionFactory;


@Configuration
@ConditionalOnMissingBean(ConnectionFactory.class)
public class AmqpConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public JmsConnectionFactory jmsConnectionFactory(AmqpProperties properties) {
        JmsConnectionFactory factory = new JmsConnectionFactory();

        factory.setRemoteURI(properties.getBrokers());

        if (!StringUtils.isEmpty(properties.getUsername())) {
            factory.setUsername(properties.getUsername());
        }

        if (!StringUtils.isEmpty(properties.getPassword())) {
            factory.setPassword(properties.getPassword());
        }

        if (!StringUtils.isEmpty(properties.getClientId())) {
            factory.setClientID(properties.getClientId());
        }

        if (properties.getReceiveLocalOnly() != null) {
            factory.setReceiveLocalOnly(properties.getReceiveLocalOnly());
        }

        if (properties.getReceiveNoWaitLocalOnly() != null) {
            factory.setReceiveNoWaitLocalOnly(properties.getReceiveNoWaitLocalOnly());
        }

        configureDeserializationPolicy(properties, factory);

        return factory;
    }

    private void configureDeserializationPolicy(AmqpProperties properties, JmsConnectionFactory factory) {
        JmsDefaultDeserializationPolicy deserializationPolicy =
                (JmsDefaultDeserializationPolicy) factory.getDeserializationPolicy();

        if (StringUtils.hasLength(properties.getDeserializationPolicy().getWhiteList())) {
            deserializationPolicy.setWhiteList(properties.getDeserializationPolicy().getWhiteList());
        }

        if (StringUtils.hasLength(properties.getDeserializationPolicy().getBlackList())) {
            deserializationPolicy.setBlackList(properties.getDeserializationPolicy().getBlackList());
        }
    }
}