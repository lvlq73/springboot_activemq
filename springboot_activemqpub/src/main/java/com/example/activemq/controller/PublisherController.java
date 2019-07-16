package com.example.activemq.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;

@RestController
@RequestMapping("/test")
public class PublisherController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;


    @RequestMapping("/send")
    public void send(){
        for(int i = 0;i<10;i++){
            String message = "发布的消息"+i;
            logger.info("发送topic=============发送topic："+message);
            jmsTemplate.convertAndSend(destination,message);
        }
    }
}
