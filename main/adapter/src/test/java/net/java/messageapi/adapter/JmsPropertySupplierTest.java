package net.java.messageapi.adapter;

import static org.mockito.Mockito.*;

import java.util.*;

import javax.jms.Message;

import net.java.messageapi.JmsProperty;
import net.sf.twip.TwiP;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(TwiP.class)
public class JmsPropertySupplierTest {

    private final JmsPropertySupplier supplier = new JmsPropertySupplier();

    @Mock
    Message message;

    static class StringContainer {
        @JmsProperty
        String one = "1";
    }

    @Test
    public void shouldSetStringProperty() throws Exception {
        StringContainer pojo = new StringContainer();

        supplier.addTo(message, pojo);

        verify(message).setStringProperty("one", "1");
    }

    static class BooleanContainer {
        @JmsProperty
        Boolean one = true;
    }

    @Test
    public void shouldSetBooleanProperty() throws Exception {
        BooleanContainer pojo = new BooleanContainer();

        supplier.addTo(message, pojo);

        verify(message).setBooleanProperty("one", true);
    }

    static class PrimitiveBooleanContainer {
        @JmsProperty
        boolean one = true;
    }

    @Test
    public void shouldSetPrimitiveBooleanProperty() throws Exception {
        PrimitiveBooleanContainer pojo = new PrimitiveBooleanContainer();

        supplier.addTo(message, pojo);

        verify(message).setBooleanProperty("one", true);
    }

    static class ByteContainer {
        @JmsProperty
        Byte one = 'b';
    }

    @Test
    public void shouldSetByteProperty() throws Exception {
        ByteContainer pojo = new ByteContainer();

        supplier.addTo(message, pojo);

        verify(message).setByteProperty("one", (byte) 'b');
    }

    static class PrimitiveByteContainer {
        @JmsProperty
        byte one = 'b';
    }

    @Test
    public void shouldSetPrimitiveByteProperty() throws Exception {
        PrimitiveByteContainer pojo = new PrimitiveByteContainer();

        supplier.addTo(message, pojo);

        verify(message).setByteProperty("one", (byte) 'b');
    }

    static class CharacterContainer {
        @JmsProperty
        Character one = 'c';
    }

    @Test
    public void shouldSetCharacterProperty() throws Exception {
        CharacterContainer pojo = new CharacterContainer();

        supplier.addTo(message, pojo);

        verify(message).setStringProperty("one", "c");
    }

    static class PrimitiveCharacterContainer {
        @JmsProperty
        char one = 'c';
    }

    @Test
    public void shouldSetPrimitiveCharacterProperty() throws Exception {
        PrimitiveCharacterContainer pojo = new PrimitiveCharacterContainer();

        supplier.addTo(message, pojo);

        verify(message).setStringProperty("one", "c");
    }

    static class ShortContainer {
        @JmsProperty
        Short one = 123;
    }

    @Test
    public void shouldSetShortProperty() throws Exception {
        ShortContainer pojo = new ShortContainer();

        supplier.addTo(message, pojo);

        verify(message).setShortProperty("one", (short) 123);
    }

    static class PrimitiveShortContainer {
        @JmsProperty
        short one = 123;
    }

    @Test
    public void shouldSetPrimitiveShortProperty() throws Exception {
        PrimitiveShortContainer pojo = new PrimitiveShortContainer();

        supplier.addTo(message, pojo);

        verify(message).setShortProperty("one", (short) 123);
    }

    static class IntegerContainer {
        @JmsProperty
        Integer one = 123;
    }

    @Test
    public void shouldSetIntegerProperty() throws Exception {
        IntegerContainer pojo = new IntegerContainer();

        supplier.addTo(message, pojo);

        verify(message).setIntProperty("one", 123);
    }

    static class PrimitiveIntegerContainer {
        @JmsProperty
        int one = 123;
    }

    @Test
    public void shouldSetPrimitiveIntegerProperty() throws Exception {
        PrimitiveIntegerContainer pojo = new PrimitiveIntegerContainer();

        supplier.addTo(message, pojo);

        verify(message).setIntProperty("one", 123);
    }

    static class LongContainer {
        @JmsProperty
        Long one = 123L;
    }

    @Test
    public void shouldSetLongProperty() throws Exception {
        LongContainer pojo = new LongContainer();

        supplier.addTo(message, pojo);

        verify(message).setLongProperty("one", 123L);
    }

    static class PrimitiveLongContainer {
        @JmsProperty
        long one = 123L;
    }

    @Test
    public void shouldSetPrimitiveLongProperty() throws Exception {
        PrimitiveLongContainer pojo = new PrimitiveLongContainer();

        supplier.addTo(message, pojo);

        verify(message).setLongProperty("one", 123L);
    }

    static class FloatContainer {
        @JmsProperty
        Float one = 12.3f;
    }

    @Test
    public void shouldSetFloatProperty() throws Exception {
        FloatContainer pojo = new FloatContainer();

        supplier.addTo(message, pojo);

        verify(message).setFloatProperty("one", 12.3f);
    }

    static class PrimitiveFloatContainer {
        @JmsProperty
        float one = 12.3f;
    }

    @Test
    public void shouldSetPrimitiveFloatProperty() throws Exception {
        PrimitiveFloatContainer pojo = new PrimitiveFloatContainer();

        supplier.addTo(message, pojo);

        verify(message).setFloatProperty("one", 12.3f);
    }

    static class DoubleContainer {
        @JmsProperty
        Double one = 12.3;
    }

    @Test
    public void shouldSetDoubleProperty() throws Exception {
        DoubleContainer pojo = new DoubleContainer();

        supplier.addTo(message, pojo);

        verify(message).setDoubleProperty("one", 12.3);
    }

    static class PrimitiveDoubleContainer {
        @JmsProperty
        double one = 12.3;
    }

    @Test
    public void shouldSetPrimitiveDoubleProperty() throws Exception {
        PrimitiveDoubleContainer pojo = new PrimitiveDoubleContainer();

        supplier.addTo(message, pojo);

        verify(message).setDoubleProperty("one", 12.3);
    }

    static class ListContainer {
        @JmsProperty
        List<String> one = Arrays.asList("111", "222", "333");
    }

    @Test
    public void shouldSetListProperty() throws Exception {
        ListContainer pojo = new ListContainer();

        supplier.addTo(message, pojo);

        verify(message).setStringProperty("one[0]", "111");
        verify(message).setStringProperty("one[1]", "222");
        verify(message).setStringProperty("one[2]", "333");
    }

    static class ArrayContainer {
        @JmsProperty
        String[] one = { "111", "222", "333" };
    }

    @Test
    public void shouldSetArrayProperty() throws Exception {
        ArrayContainer pojo = new ArrayContainer();

        supplier.addTo(message, pojo);

        verify(message).setStringProperty("one[0]", "111");
        verify(message).setStringProperty("one[1]", "222");
        verify(message).setStringProperty("one[2]", "333");
    }

    static class SetContainer {
        @JmsProperty
        Set<String> one = new TreeSet<String>(Arrays.asList("111", "222", "333"));
    }

    @Test
    public void shouldSetSetProperty() throws Exception {
        SetContainer pojo = new SetContainer();

        supplier.addTo(message, pojo);

        verify(message).setStringProperty("one[0]", "111");
        verify(message).setStringProperty("one[1]", "222");
        verify(message).setStringProperty("one[2]", "333");
    }

    static class MapContainer {
        @JmsProperty
        Map<String, String> one = new HashMap<String, String>();
        {
            one.put("aaa", "111");
            one.put("bbb", "222");
            one.put("ccc", "333");
        }
    }

    @Test
    public void shouldSetMapProperty() throws Exception {
        MapContainer pojo = new MapContainer();

        supplier.addTo(message, pojo);

        verify(message).setStringProperty("one[aaa]", "111");
        verify(message).setStringProperty("one[bbb]", "222");
        verify(message).setStringProperty("one[ccc]", "333");
    }
}
