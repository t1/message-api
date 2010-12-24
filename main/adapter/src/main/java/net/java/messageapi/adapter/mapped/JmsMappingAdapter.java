package net.java.messageapi.adapter.mapped;

import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.collect.ImmutableMap;

public class JmsMappingAdapter extends XmlAdapter<JmsMappingAdapter.AdaptedMapping, Mapping> {

    public static class AdaptedFieldMapping {
        @XmlAttribute(required = true)
        public final String from;
        @XmlValue
        public final String to;

        // just to satisfy JAXB
        @SuppressWarnings("unused")
        private AdaptedFieldMapping() {
            this(null, null);
        }

        public AdaptedFieldMapping(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }

    public static class AdaptedMapping {
        @XmlAttribute(required = true)
        public String methodName;
        @XmlElement(required = true, name = "map")
        public List<AdaptedFieldMapping> mappings = new ArrayList<AdaptedFieldMapping>();

        private void addFieldMappings(Mapping mapping) {
            if (mapping instanceof MapFieldsMapping) {
                ImmutableMap<String, FieldMapping<?>> map = ((MapFieldsMapping) mapping).map;
                for (Map.Entry<String, FieldMapping<?>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().getAttributeName();
                    mappings.add(new AdaptedFieldMapping(key, value));
                }
            } else if (mapping instanceof MappingDecorator) {
                addFieldMappings(((MappingDecorator) mapping).target);
            }
        }
    }

    @Override
    public AdaptedMapping marshal(Mapping mapping) throws Exception {
        AdaptedMapping result = new AdaptedMapping();
        result.methodName = mapping.getOperationMessageAttibute();
        result.addFieldMappings(mapping);
        return result;
    }

    @Override
    public Mapping unmarshal(AdaptedMapping adapted) throws Exception {
        MappingBuilder builder = new MappingBuilder(adapted.methodName);
        for (AdaptedFieldMapping mapping : adapted.mappings) {
            String property = mapping.from;
            String attributeName = mapping.to;
            builder.mapField(property, attributeName);
        }
        return builder.build();
    }
}
