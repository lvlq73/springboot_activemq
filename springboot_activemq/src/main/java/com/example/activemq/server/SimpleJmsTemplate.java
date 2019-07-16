package com.example.activemq.server;

import org.springframework.jms.JmsException;
import org.springframework.jms.connection.ConnectionFactoryUtils;
import org.springframework.jms.connection.JmsResourceHolder;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.JmsUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import javax.jms.*;

/**
 * 类描述:自定义JmsTemplate,实现客户手动确认
 *
 * @author fengyong
 * @version 1.0
 * @since 1.0
 * Created by fengyong on 16/8/10 上午10:03.
 */
public class SimpleJmsTemplate extends JmsTemplate {


    private final JmsTemplateResourceFactory transactionalResourceFactory = new JmsTemplateResourceFactory();
    /**
     * 是否开启手动确认标记
     */
    private Boolean autoAcknowledge;

    MessageConsumer consumer = null;
    Session sessionToClose = null;
    Connection conToClose = null;
    boolean startConnection = false;

    /**
     * 接收消息
     * @param session
     * @param consumer
     * @return
     * @throws JMSException
     */
    protected Message doReceive(Session session, MessageConsumer consumer) throws JMSException {
        try {
            this.consumer = consumer;
            // Use transaction timeout (if available).
            long timeout = getReceiveTimeout();
            JmsResourceHolder resourceHolder =
                    (JmsResourceHolder) TransactionSynchronizationManager.getResource(getConnectionFactory());
            if (resourceHolder != null && resourceHolder.hasTimeout()) {
                timeout = Math.min(timeout, resourceHolder.getTimeToLiveInMillis());
            }
            Message message = doReceive(consumer, timeout);
            if (session.getTransacted()) {
                // Commit necessary - but avoid commit call within a JTA transaction.
                // 如果开启了jta事物,那么不会进行提交,jta事物会直接覆盖掉session事物
                if (isSessionLocallyTransacted(session)) {
                    // Transacted session created by this template -> commit.
                    JmsUtils.commitIfNecessary(session);
                }
            }
            //autoAcknowledge如果为真,不进行自动确认
            else if (isClientAcknowledge(session) && !autoAcknowledge) {
                // Manually acknowledge message, if any.
                if (message != null) {
                    message.acknowledge();
                }
            }
            return message;
        }
        finally {
            consumer = null;
        }
    }

    /**
     * 自定义的消息确认,关闭consumer和sesseionToClose是父类本身就要执行的,这里直接拷贝下来,能不改的地方尽量不改
     * 该子类只是为了自定义确认消息
     * @param message
     * @throws JMSException
     */
    public void  msgAckAndcloseSession(Message message) throws JMSException {
        message.acknowledge();
        JmsUtils.closeMessageConsumer(consumer);
        JmsUtils.closeSession(sessionToClose);
        ConnectionFactoryUtils.releaseConnection(conToClose, getConnectionFactory(), startConnection);
    }

    /**
     * 由于上面的doReceive(Session session, MessageConsumer consumer)需要调用这个方法,
     * 而在父类里面这个方法是私有的,所以直接拷贝下来了
     * @param consumer
     * @param timeout
     * @return
     * @throws JMSException
     */
    private Message doReceive(MessageConsumer consumer, long timeout) throws JMSException {
        if (timeout == RECEIVE_TIMEOUT_NO_WAIT) {
            return consumer.receiveNoWait();
        }
        else if (timeout > 0) {
            return consumer.receive(timeout);
        }
        else {
            return consumer.receive();
        }
    }


    /**
     * 该方法是为了防止确认消息前session被关闭,不然确认消息前session关闭会导致异常发生
     * transactionalResourceFactory在父类中是私有且不可修改,因为只有这一个方法用到了transactionalResourceFactory
     * 所以直接将JmsTemplateResourceFactory拷贝下来使用
     * @param action
     * @param startConnection
     * @param <T>
     * @return
     * @throws JmsException
     */
    public <T> T execute(SessionCallback<T> action, boolean startConnection) throws JmsException {
        Assert.notNull(action, "Callback object must not be null");
        this.startConnection = startConnection;

        try {
            Session sessionToUse = ConnectionFactoryUtils.doGetTransactionalSession(
                    getConnectionFactory(), transactionalResourceFactory, startConnection);
            if (sessionToUse == null) {
                conToClose = createConnection();
                sessionToClose = createSession(conToClose);
                if (startConnection) {
                    conToClose.start();
                }
                sessionToUse = sessionToClose;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Executing callback on JMS Session: " + sessionToUse);
            }
            return action.doInJms(sessionToUse);
        }
        catch (JMSException ex) {
            throw convertJmsAccessException(ex);
        }
        finally {
            sessionToClose = null;
            conToClose = null;
            startConnection = false;
        }
    }


    /**
     * Sets new 是否开启手动确认标记.
     *
     * @param autoAcknowledge New value of 是否开启手动确认标记.
     */
    public void setAutoAcknowledge(Boolean autoAcknowledge) {
        this.autoAcknowledge = autoAcknowledge;
    }

    /**
     * Gets 是否开启手动确认标记.
     *
     * @return Value of 是否开启手动确认标记.
     */
    public Boolean getAutoAcknowledge() {
        return autoAcknowledge;
    }


    /**
     * 直接拷贝下来的
     */
    private class JmsTemplateResourceFactory implements ConnectionFactoryUtils.ResourceFactory {

        @Override
        public Connection getConnection(JmsResourceHolder holder) {
            return SimpleJmsTemplate.this.getConnection(holder);
        }

        @Override
        public Session getSession(JmsResourceHolder holder) {
            return SimpleJmsTemplate.this.getSession(holder);
        }

        @Override
        public Connection createConnection() throws JMSException {
            return SimpleJmsTemplate.this.createConnection();
        }

        @Override
        public Session createSession(Connection con) throws JMSException {
            return SimpleJmsTemplate.this.createSession(con);
        }

        @Override
        public boolean isSynchedLocalTransactionAllowed() {
            return SimpleJmsTemplate.this.isSessionTransacted();
        }
    }

}
