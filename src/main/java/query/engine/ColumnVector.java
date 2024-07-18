package query.engine;

import sword.dev.type.SwordType;

public interface ColumnVector {
    SwordType getType();
    Object getValue(int i);
    int size();
}
