package net.java.messageapi.adapter;

import javax.enterprise.context.spi.CreationalContext;

public class MdbBean extends AbstractBean<Object> {
    @SuppressWarnings("unchecked")
    public MdbBean(Class<?> mdb) {
        super((Class<Object>) mdb);
    }

    @Override
    public Object create(CreationalContext<Object> arg0) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
