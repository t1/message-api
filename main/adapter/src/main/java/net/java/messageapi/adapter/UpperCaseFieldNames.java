package net.java.messageapi.adapter;

class UpperCaseFieldNames extends MappingDecorator {

    public UpperCaseFieldNames(Mapping target) {
        super(target);
    }

    @Override
    public FieldMapping<?> getMappingForField(String fieldName) {
        StringBuilder result = new StringBuilder();
        boolean hadLower = false;
        for (int i = 0; i < fieldName.length(); i++) {
            int character = fieldName.codePointAt(i);
            if (Character.isUpperCase(character)) {
                if (hadLower) {
                    result.append('_');
                    hadLower = false;
                }
                result.appendCodePoint(character);
            } else {
                hadLower = true;
                result.appendCodePoint(Character.toUpperCase(character));
            }
        }
        return FieldMapping.map(result.toString());
    }

    @Override
    public String toString() {
        return super.toString() + "[upper case fields]";
    }
}
