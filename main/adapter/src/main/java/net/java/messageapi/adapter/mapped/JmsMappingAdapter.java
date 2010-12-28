package net.java.messageapi.adapter.mapped;

import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.java.messageapi.converter.Converter;

public class JmsMappingAdapter extends XmlAdapter<JmsMappingAdapter.AdaptedMapping, Mapping> {

    public static class AdaptedMapping {
        @XmlAttribute
        public final String methodName;
        @XmlAttribute
        public Boolean upperCase;
        @XmlElement(name = "mapField")
        public final List<AdaptedFieldMapping> fieldMappings = new ArrayList<AdaptedFieldMapping>();
        @XmlElement(name = "mapOperation")
        public final List<AdaptedOperationMapping> operationMappings = new ArrayList<AdaptedOperationMapping>();

        // just to satisfy JAXB
        @SuppressWarnings("unused")
        private AdaptedMapping() {
            this.methodName = null;
        }

        public AdaptedMapping(Mapping mapping) {
            this.methodName = mapping.getOperationMessageAttibute();
            addMappings(mapping);
        }

        /** TODO use visitor instead of instanceof */
        private void addMappings(Mapping mapping) {
            if (mapping instanceof MapFieldsMapping) {
                Map<String, FieldMapping<?>> map = ((MapFieldsMapping) mapping).map;
                for (Map.Entry<String, FieldMapping<?>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    FieldMapping<?> fieldMapping = entry.getValue();
                    fieldMappings.add(new AdaptedFieldMapping(key, fieldMapping));
                }
            }
            if (mapping instanceof MapOperationsMapping) {
                Map<String, String> map = ((MapOperationsMapping) mapping).map;
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

        public Mapping toMapping() {
            MappingBuilder builder = new MappingBuilder(methodName);

            for (AdaptedFieldMapping mapping : fieldMappings) {
                String property = mapping.from;
                String attributeName = mapping.to;
                Converter<?> converter = mapping.converter;
                builder.mapField(property, FieldMapping.map(attributeName, converter));
            }

            for (AdaptedOperationMapping mapping : operationMappings) {
                String method = mapping.from;
                String operation = mapping.to;
                builder.mapOperation(method, operation);
            }

            if (upperCase == Boolean.TRUE) {
                builder.upperCaseFields();
            }

            return builder.build();
        }
    }

    public static class AdaptedFieldMapping {
        @XmlAttribute(required = true)
        public final String from;
        @XmlAttribute(required = true)
        public final String to;
        @XmlElementRef
        public final Converter<?> converter;

        // TODO map default

        // just to satisfy JAXB
        @SuppressWarnings("unused")
        private AdaptedFieldMapping() {
            this.from = null;
            this.to = null;
            this.converter = null;
        }

        public AdaptedFieldMapping(String from, FieldMapping<?> fieldMapping) {
            this.from = from;
            this.to = fieldMapping.getAttributeName();
            this.converter = fieldMapping.getConverter();
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
        try {
            return new AdaptedMapping(mapping);
        } catch (Exception e) {
            e.printStackTrace(); // why is this exception not passed up?
            throw e;
        }
    }

    @Override
    public Mapping unmarshal(AdaptedMapping adapted) throws Exception {
        try {
            return adapted.toMapping();
        } catch (Exception e) {
            e.printStackTrace(); // why is this exception not passed up?
            throw e;
        }
    }
}
