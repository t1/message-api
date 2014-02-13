package net.java.messageapi.adapter;

import javax.jms.*;

public interface JmsHeaderSupplier {
    void addTo(Message message, Class<?> api, Object pojo) throws JMSException;
}