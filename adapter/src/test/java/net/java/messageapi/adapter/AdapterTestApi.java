/**
 * 
 */
package net.java.messageapi.adapter;

import net.java.messageapi.MessageApi;

//TODO Find out why Eclipse builds the SimpleMethod classes for the test code (Maven does not)
@MessageApi
public interface AdapterTestApi {
    void adapterMethod1();

    void adapterMethod2(int i);

    void adapterMethod3(int i, String s);
}