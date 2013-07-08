package net.java.messageapi.adapter;

import java.util.Map;

class MapFieldsMapping extends MappingDecorator {

    final Map<String, FieldMapping<?>> map;

    public MapFieldsMapping(Mapping target, Map<String, FieldMapping<?>> map) {
        super(target);
        if (map == null)
            throw new NullPointerException("the map must not be null");
        this.map = map;
    }

    @Override
    public FieldMapping<?> getMappingForField(String fieldName) {
        final FieldMapping<?> value = map.get(fieldName);
        return (value == null) ? super.getMappingForField(fieldName) : value;
    }

    @Override
    public String toString() {
        return super.toString() + "[fields=" + map + "]";
    }
}
