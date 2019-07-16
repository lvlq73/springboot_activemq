package com.example.activemq.config;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Destination;

@Configuration
public class ActiveMqTopicConfig {

    @Bean
    public Destination destination(){
        return new ActiveMQTopic("topic1");
    }
   /* @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory (){
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory(
                        "admin",
                        "admin",
                        "tcp://127.0.0.1:61616");
        return activeMQConnectionFactory;
    }
  *//*  @Bean
    public SingleConnectionFactory singleConnectionFactory(ActiveMQConnectionFactory activeMQConnectionFactory){
        SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
        singleConnectionFactory.setTargetConnectionFactory(activeMQConnectionFactory);
        return singleConnectionFactory;
    }*//*

    @Bean
    public SimpleJmsTemplate simpleJmsTemplate( ActiveMQConnectionFactory activeMQConnectionFactory){
        SimpleJmsTemplate jmsTemplate=new SimpleJmsTemplate();
        jmsTemplate.setConnectionFactory(activeMQConnectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.setSessionTransacted(false);
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        jmsTemplate.setReceiveTimeout(1000);
        jmsTemplate.setAutoAcknowledge(true);
        return jmsTemplate;
    }*/
}
