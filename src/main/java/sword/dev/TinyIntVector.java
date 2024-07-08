package sword.dev;

import sword.dev.type.SwordType;

import java.util.Iterator;

public class TinyIntVector implements FieldVector {
    private final SwordType.TinyInt type;
    private byte[] values;
    private int valueCount;
    private String name;
    private boolean nullable;

    public TinyIntVector() {
        this("", new SwordType.TinyInt());
    }

    public TinyIntVector(String name, SwordType.TinyInt type) {
        this.name = name;
        this.type = type;
        this.values = new byte[0];
        this.valueCount = 0;
        this.nullable = false;
    }

    @Override
    public SwordType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void allocateNew() {
        values = new byte[10]; // Initial capacity of 10, can be adjusted
        valueCount = 0;
    }

    @Override
    public int getValueCapacity() {
        return values.length;
    }

    @Override
    public boolean isNull(int index) {
        return false; // sword.dev.TinyIntVector doesn't support null values
    }

    @Override
    public int getValueCount() {
        return valueCount;
    }

    @Override
    public void setValueCount(int valueCount) {
        this.valueCount = valueCount;
    }

    @Override
    public void clear() {
        values = new byte[0];
        valueCount = 0;
    }

    @Override
    public void close() {
        clear();
    }

    @Override
    public Object getObject(int index) {
        return get(index);
    }

    @Override
    public void copyFrom(int fromIndex, int thisIndex, ValueVector from) {
        if (from instanceof TinyIntVector) {
            TinyIntVector fromVector = (TinyIntVector) from;
            set(thisIndex, fromVector.get(fromIndex));
        } else {
            throw new IllegalArgumentException("Cannot copy from " + from.getClass().getSimpleName());
        }
    }

    @Override
    public FieldVector getNewVector() {
        return new TinyIntVector(name, type);
    }

    @Override
    public void transferTo(FieldVector target) {
        if (target instanceof TinyIntVector) {
            TinyIntVector targetVector = (TinyIntVector) target;
            targetVector.values = this.values;
            targetVector.valueCount = this.valueCount;
            this.values = new byte[0];
            this.valueCount = 0;
        } else {
            throw new IllegalArgumentException("Cannot transfer to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public void copySubset(int fromIndex, int toIndex, FieldVector target, int targetIndex) {
        if (target instanceof TinyIntVector) {
            TinyIntVector targetVector = (TinyIntVector) target;
            int length = toIndex - fromIndex;
            System.arraycopy(this.values, fromIndex, targetVector.values, targetIndex, length);
            targetVector.valueCount = Math.max(targetVector.valueCount, targetIndex + length);
        } else {
            throw new IllegalArgumentException("Cannot copy subset to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public FieldVector slice(int start, int end) {
        TinyIntVector sliced = new TinyIntVector(name + "[" + start + "," + end + "]", type);
        int length = end - start;
        sliced.values = new byte[length];
        System.arraycopy(this.values, start, sliced.values, 0, length);
        sliced.valueCount = length;
        return sliced;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public int getNullCount() {
        return 0; // sword.dev.TinyIntVector doesn't support null values
    }

    @Override
    public void set(int index, Object value) {
        if (value instanceof Byte) {
            set(index, (byte) value);
        } else {
            throw new IllegalArgumentException("Value must be a Byte");
        }
    }

    @Override
    public void setSafe(int index, Object value) {
        if (index >= values.length) {
            byte[] newValues = new byte[Math.max(index + 1, values.length * 2)];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
        set(index, value);
    }

    public void set(int index, byte value) {
        values[index] = value;
        valueCount = Math.max(valueCount, index + 1);
    }

    public byte get(int index) {
        return values[index];
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < valueCount;
            }

            @Override
            public Object next() {
                return get(index++);
            }
        };
    }
}
