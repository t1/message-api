package net.java.messageapi.adapter;

import java.util.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PropertiesMapAdapter extends XmlAdapter<PropertiesMapAdapter.MyMap, Properties> {

    public static class MyMap {
        public final List<MyMapEntry> entries = new ArrayList<MyMapEntry>();
    }

    public static class MyMapEntry {
        @XmlAttribute
        public final String key;

        @XmlValue
        public final String value;

        // just to satisfy JAXB
        @SuppressWarnings("unused")
        private MyMapEntry() {
            this.key = null;
            this.value = null;
        }

        public MyMapEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public MyMap marshal(Properties in) throws Exception {
        if (in == null)
            return null;
        MyMap out = new MyMap();
        for (String key : in.stringPropertyNames()) {
            String value = in.getProperty(key);
            out.entries.add(new MyMapEntry(key, value));
        }
        return out;
    }

    @Override
    public Properties unmarshal(MyMap in) throws Exception {
        if (in == null)
            return null;
        Properties out = new Properties();
        for (MyMapEntry entry : in.entries) {
            out.setProperty(entry.key, entry.value);
        }
        return out;
    }
}
