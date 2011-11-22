package net.java.messageapi.adapter;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

public class InjectionTargetWrapper<T> implements InjectionTarget<T> {

    private final InjectionTarget<T> target;

    public InjectionTargetWrapper(InjectionTarget<T> target) {
        this.target = target;
    }

    @Override
    public void dispose(T bean) {
        target.dispose(bean);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return target.getInjectionPoints();
    }

    @Override
    public T produce(CreationalContext<T> context) {
        return target.produce(context);
    }

    @Override
    public void inject(T bean, CreationalContext<T> context) {
        target.inject(bean, context);
    }

    @Override
    public void postConstruct(T bean) {
        target.postConstruct(bean);
    }

    @Override
    public void preDestroy(T bean) {
        target.preDestroy(bean);
    }
}
