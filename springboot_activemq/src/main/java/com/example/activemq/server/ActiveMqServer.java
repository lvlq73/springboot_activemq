package com.example.activemq.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Session;
import javax.jms.TextMessage;

@Component
public class ActiveMqServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JmsTemplate jmsTemplate;

    private int i=0;

    @JmsListener(destination = "topic1")
    public void receiveTopic(TextMessage message, Session session) {
      /*  System.out.println("监听topic=============监听topic");
        System.out.println(message);*/
        i++;
        try {
            if (null != message) {
                logger.info("监听topic=============监听topic："+message.getText());

             /*   if(i==4){
                    throw new RuntimeException("Exception");
                }*/
                //message.acknowledge();
            }
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }
}
