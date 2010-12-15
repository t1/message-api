package net.java.messageapi.adapter;

import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MapAdapter extends XmlAdapter<MapAdapter.MyMap, Map<String, String>> {

    static class MyMap {
        @XmlElement(name = "entry")
        public final List<MyMapEntry> entries = new ArrayList<MyMapEntry>();
    }

    static class MyMapEntry {
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
    public MyMap marshal(Map<String, String> in) throws Exception {
        if (in == null)
            return null;
        MyMap out = new MyMap();
        for (String key : in.keySet()) {
            String value = in.get(key);
            out.entries.add(new MyMapEntry(key, value));
        }
        return out;
    }

    @Override
    public Map<String, String> unmarshal(MyMap in) throws Exception {
        if (in == null)
            return null;
        Map<String, String> out = new HashMap<String, String>();
        for (MyMapEntry entry : in.entries) {
            out.put(entry.key, entry.value);
        }
        return out;
    }
}
