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
        public final List<AdaptedFieldMapping<?>> fieldMappings = new ArrayList<AdaptedFieldMapping<?>>();
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
                    fieldMappings.add(AdaptedFieldMapping.of(key, fieldMapping));
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

            for (AdaptedOperationMapping mapping : operationMappings) {
                String method = mapping.from;
                String operation = mapping.to;
                builder.mapOperation(method, operation);
            }

            for (AdaptedFieldMapping<?> adaptedMapping : fieldMappings) {
                String property = adaptedMapping.from;
                builder.mapField(property, adaptedMapping.toFieldMapping());
            }

            if (upperCase == Boolean.TRUE) {
                builder.upperCaseFields();
            }

            return builder.build();
        }
    }

    public static class AdaptedFieldMapping<T> {

        public static class Default<T> {
            @XmlAnyElement(lax = true)
            public final T value;

            // satisfy JAXB
            @SuppressWarnings("unused")
            private Default() {
                this(null);
            }

            public Default(T value) {
                this.value = value;
            }
        }

        public static <T> AdaptedFieldMapping<T> of(String key, FieldMapping<T> fieldMapping) {
            return new AdaptedFieldMapping<T>(key, fieldMapping);
        }

        @XmlAttribute(required = true)
        public final String from;
        @XmlAttribute(required = true)
        public final String to;
        @XmlElementRef
        public final Converter<T> converter;
        @XmlElement(name = "default")
        public final Default<T> defaultValue;

        // just to satisfy JAXB
        @SuppressWarnings("unused")
        private AdaptedFieldMapping() {
            this.from = null;
            this.to = null;
            this.converter = null;
            this.defaultValue = null;
        }

        public AdaptedFieldMapping(String from, FieldMapping<T> fieldMapping) {
            this.from = from;
            this.to = fieldMapping.getAttributeName();
            this.converter = fieldMapping.getConverter();
            this.defaultValue = (!fieldMapping.hasDefaultValue()) ? null : //
                    new Default<T>(fieldMapping.getDefaultValue());
        }

        public FieldMapping<T> toFieldMapping() {
            if (defaultValue == null)
                return FieldMapping.map(to, converter);
            return FieldMapping.mapWithDefault(to, converter, defaultValue.value);
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
