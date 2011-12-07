package javax.ejb;

import java.lang.annotation.*;

@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public abstract @interface MessageDriven {

    public abstract String name() default "";

    public abstract Class<?> messageListenerInterface() default Object.class;

    public abstract ActivationConfigProperty[] activationConfig() default {};

    public abstract java.lang.String mappedName() default "";

    public abstract java.lang.String description() default "";

}