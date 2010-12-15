package net.java.messageapi.adapter.mapped;

import com.google.common.collect.ImmutableMap;

class MapFieldsMapping extends MappingDecorator {

    private final ImmutableMap<String, FieldMapping<?>> map;

    public MapFieldsMapping(Mapping target, ImmutableMap<String, FieldMapping<?>> map) {
        super(target);
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