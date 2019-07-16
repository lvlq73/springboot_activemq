package com.example.activemq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;

@Configuration
public class ActiveMqTopicConfig {

    @Bean
    public Destination destination(){
        return new ActiveMQTopic("topic1");
    }

    /*@Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory (){
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory(
                        "admin",
                        "admin",
                        "tcp://127.0.0.1:61616");
        return activeMQConnectionFactory;
    }
   *//* @Bean
    public SingleConnectionFactory singleConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory){
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(activeMQConnectionFactory);
        return singleConnectionFactory;
    }*//*

   public JmsTemplate jmsTemplate(ActiveMQConnectionFactory activeMQConnectionFactory){
       JmsTemplate jmsTemplate = new JmsTemplate();
       jmsTemplate.setConnectionFactory(activeMQConnectionFactory);
       jmsTemplate.setDeliveryMode(2);
       jmsTemplate.setSessionTransacted(true);
       jmsTemplate.setSessionAcknowledgeModeName("AUTO_ACKNOWLEDGE");
       return jmsTemplate;
   }*/
}
