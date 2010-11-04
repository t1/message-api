/**
 * 
 */
package net.java.messageapi.adapter;

public class AsynchronousMock implements Runnable {
    private final Runnable callback;

    public AsynchronousMock(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.run();
            }
        }).start();
    }
}