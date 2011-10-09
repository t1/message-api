package net.java.messageapi.adapter;

import net.java.messageapi.MessageApi;

@MessageApi
public interface AdapterTestApi {
    void adapterMethod1();

    void adapterMethod2(int i);

    void adapterMethod3(int i, String s);
}