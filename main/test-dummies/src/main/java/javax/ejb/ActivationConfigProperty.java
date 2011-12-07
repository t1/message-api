package javax.ejb;

import java.lang.annotation.*;

@Target(value = {})
@Retention(value = RetentionPolicy.RUNTIME)
public abstract @interface ActivationConfigProperty {

    public abstract String propertyName();

    public abstract String propertyValue();

}