package net.java.messageapi.adapter;

import javax.jms.JMSException;
import javax.jms.Message;

public interface JmsHeaderSupplier {
    void addTo(Message message, Object pojo) throws JMSException;
}