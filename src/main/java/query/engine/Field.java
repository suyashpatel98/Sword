package query.engine;

import sword.dev.type.SwordType;

public class Field {
    private final String name;
    private final SwordType dataType;

    public SwordType getDataType() {
        return dataType;
    }

    public Field(String name, SwordType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }
}