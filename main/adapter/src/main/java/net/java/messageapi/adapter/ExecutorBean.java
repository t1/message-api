package net.java.messageapi.adapter;

import java.util.concurrent.Executor;

import javax.ejb.*;

/**
 * Handy indirection, so you can simply {@link javax.inject.Inject inject} an {@link Executor} in order to have it
 * executed asynchronously.
 * 
 * @see http://stackoverflow.com/questions/13932083/jboss-java-ee-container-and-an-executorservice
 */
@Stateless(name = "Executor")
public class ExecutorBean implements Executor {
    @Override
    @Asynchronous
    public void execute(Runnable command) {
        command.run();
    }
}
