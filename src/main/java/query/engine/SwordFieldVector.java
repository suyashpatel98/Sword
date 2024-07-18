package query.engine;


import sword.dev.FieldVectorV2;
import sword.dev.IntVectorV2;
import sword.dev.type.SwordType;

public class SwordFieldVector implements ColumnVector {

    private final FieldVectorV2 field;

    public SwordFieldVector(FieldVectorV2 field) {
        this.field = field;
    }

    public FieldVectorV2 getField() {
        return field;
    }

    @Override
    public SwordType getType() {
        if (field instanceof IntVectorV2) {
            return SwordTypes.Int32Type;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Object getValue(int i) {
        if (field.isNull(i)) {
            return null;
        }
        if (field instanceof IntVectorV2) {
            IntVectorV2 intVector = (IntVectorV2) field;
            return intVector.get(i);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public int size() {
        return field.getValueCount();
    }
}

