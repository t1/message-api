package net.java.messageapi.adapter;

import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PropertiesMapAdapter extends
        XmlAdapter<PropertiesMapAdapter.MyPropertiesMap, Properties> {

    static class MyPropertiesMap {
        @XmlElement(name = "entry")
        public final List<MyPropertiesMapEntry> entries = new ArrayList<MyPropertiesMapEntry>();
    }

    static class MyPropertiesMapEntry {
        @XmlAttribute
        public final String key;

        @XmlValue
        public final String value;

        // just to satisfy JAXB
        @SuppressWarnings("unused")
        private MyPropertiesMapEntry() {
            this.key = null;
            this.value = null;
        }

        public MyPropertiesMapEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public MyPropertiesMap marshal(Properties in) throws Exception {
        if (in == null)
            return null;
        MyPropertiesMap out = new MyPropertiesMap();
        for (String key : in.stringPropertyNames()) {
            String value = in.getProperty(key);
            out.entries.add(new MyPropertiesMapEntry(key, value));
        }
        return out;
    }

    @Override
    public Properties unmarshal(MyPropertiesMap in) throws Exception {
        if (in == null)
            return null;
        Properties out = new Properties();
        for (MyPropertiesMapEntry entry : in.entries) {
            out.setProperty(entry.key, entry.value);
        }
        return out;
    }
}
