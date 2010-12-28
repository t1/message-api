package net.java.messageapi.adapter.mapped;

import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.collect.ImmutableMap;

/**
 * TODO map converters
 * <p>
 * TODO use visitor instead of instanceof
 */
public class JmsMappingAdapter extends XmlAdapter<JmsMappingAdapter.AdaptedMapping, Mapping> {

    public static class AdaptedMapping {
        @XmlAttribute
        public String methodName;
        @XmlAttribute
        public Boolean upperCase;
        @XmlElement(name = "mapField")
        public List<AdaptedFieldMapping> fieldMappings = new ArrayList<AdaptedFieldMapping>();
        @XmlElement(name = "mapOperation")
        public List<AdaptedOperationMapping> operationMappings = new ArrayList<AdaptedOperationMapping>();

        private void addMappings(Mapping mapping) {
            if (mapping instanceof MapFieldsMapping) {
                ImmutableMap<String, FieldMapping<?>> map = ((MapFieldsMapping) mapping).map;
                for (Map.Entry<String, FieldMapping<?>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().getAttributeName();
                    fieldMappings.add(new AdaptedFieldMapping(key, value));
                }
            }
            if (mapping instanceof MapOperationsMapping) {
                ImmutableMap<String, String> map = ((MapOperationsMapping) mapping).map;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    operationMappings.add(new AdaptedOperationMapping(key, value));
                }
            }
            if (mapping instanceof UpperCaseFieldNames) {
                upperCase = true;
            }
            if (mapping instanceof MappingDecorator) {
                addMappings(((MappingDecorator) mapping).target);
            }
        }
    }

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

    public static class AdaptedOperationMapping {
        @XmlAttribute(required = true)
        public final String from;
        @XmlValue
        public final String to;

        // just to satisfy JAXB
        @SuppressWarnings("unused")
        private AdaptedOperationMapping() {
            this(null, null);
        }

        public AdaptedOperationMapping(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }

    @Override
    public AdaptedMapping marshal(Mapping mapping) throws Exception {
        AdaptedMapping result = new AdaptedMapping();
        result.methodName = mapping.getOperationMessageAttibute();
        result.addMappings(mapping);
        return result;
    }

    @Override
    public Mapping unmarshal(AdaptedMapping adapted) throws Exception {
        MappingBuilder builder = new MappingBuilder(adapted.methodName);

        for (AdaptedFieldMapping mapping : adapted.fieldMappings) {
            String property = mapping.from;
            String attribute = mapping.to;
            builder.mapField(property, attribute);
        }

        for (AdaptedOperationMapping mapping : adapted.operationMappings) {
            String method = mapping.from;
            String operation = mapping.to;
            builder.mapOperation(method, operation);
        }

        if (adapted.upperCase == Boolean.TRUE) {
            builder.upperCaseFields();
        }

        return builder.build();
    }
}
