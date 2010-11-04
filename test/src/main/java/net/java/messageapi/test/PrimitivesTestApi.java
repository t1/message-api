package net.java.messageapi.test;

import net.java.messageapi.MessageApi;

@MessageApi
public interface PrimitivesTestApi {

    public void booleanCall(boolean b);

    public void byteCall(byte b);

    public void charCall(char c);

    public void shortCall(short s);

    public void intCall(int i);

    public void longCall(long l);

    public void floatCall(float f);

    public void doubleCall(double d);

    public void twoDoublesCall(double d, double e);
}
