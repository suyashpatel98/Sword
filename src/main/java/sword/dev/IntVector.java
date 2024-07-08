package sword.dev;

import sword.dev.type.SwordType;

import java.util.Iterator;

public class IntVector implements FieldVector {
    private final SwordType.Int type;
    private int[] values;
    private int valueCount;
    private String name;
    private boolean nullable;

    public IntVector() {
        this("", new SwordType.Int(32)); // Assuming 32-bit integers by default
    }

    public IntVector(String name, SwordType.Int type) {
        this.name = name;
        this.type = type;
        this.values = new int[0];
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
        values = new int[10]; // Initial capacity of 10, can be adjusted
        valueCount = 0;
    }

    @Override
    public int getValueCapacity() {
        return values.length;
    }

    @Override
    public boolean isNull(int index) {
        return false; // sword.dev.IntVector doesn't support null values
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
        values = new int[0];
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
        if (from instanceof IntVector) {
            IntVector fromVector = (IntVector) from;
            set(thisIndex, fromVector.get(fromIndex));
        } else {
            throw new IllegalArgumentException("Cannot copy from " + from.getClass().getSimpleName());
        }
    }

    @Override
    public FieldVector getNewVector() {
        return new IntVector(name, type);
    }

    @Override
    public void transferTo(FieldVector target) {
        if (target instanceof IntVector) {
            IntVector targetVector = (IntVector) target;
            targetVector.values = this.values;
            targetVector.valueCount = this.valueCount;
            this.values = new int[0];
            this.valueCount = 0;
        } else {
            throw new IllegalArgumentException("Cannot transfer to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public void copySubset(int fromIndex, int toIndex, FieldVector target, int targetIndex) {
        if (target instanceof IntVector) {
            IntVector targetVector = (IntVector) target;
            int length = toIndex - fromIndex;
            System.arraycopy(this.values, fromIndex, targetVector.values, targetIndex, length);
            targetVector.valueCount = Math.max(targetVector.valueCount, targetIndex + length);
        } else {
            throw new IllegalArgumentException("Cannot copy subset to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public FieldVector slice(int start, int end) {
        IntVector sliced = new IntVector(name + "[" + start + "," + end + "]", type);
        int length = end - start;
        sliced.values = new int[length];
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
        return 0; // sword.dev.IntVector doesn't support null values
    }

    @Override
    public void set(int index, Object value) {
        if (value instanceof Integer) {
            set(index, (int) value);
        } else {
            throw new IllegalArgumentException("Value must be an Integer");
        }
    }

    @Override
    public void setSafe(int index, Object value) {
        if (index >= values.length) {
            int[] newValues = new int[Math.max(index + 1, values.length * 2)];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
        set(index, value);
    }

    public void set(int index, int value) {
        values[index] = value;
        valueCount = Math.max(valueCount, index + 1);
    }

    public int get(int index) {
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
