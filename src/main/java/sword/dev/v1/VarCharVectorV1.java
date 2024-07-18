package sword.dev.v1;

import sword.dev.type.SwordType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VarCharVectorV1 implements FieldVectorV1 {
    private final SwordType.Utf8 type;
    private List<String> values;
    private String name;
    private boolean nullable;
    private int valueCount;

    public VarCharVectorV1() {
        this("", new SwordType.Utf8());
    }

    public VarCharVectorV1(String name, SwordType.Utf8 type) {
        this.name = name;
        this.type = type;
        this.values = new ArrayList<>();
        this.nullable = true;
        this.valueCount = 0;
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
        values = new ArrayList<>(10); // Initial capacity of 10, can be adjusted
        valueCount = 0;
    }

    @Override
    public int getValueCapacity() {
        return values.size();
    }

    @Override
    public boolean isNull(int index) {
        return index >= valueCount || values.get(index) == null;
    }

    @Override
    public int getValueCount() {
        return valueCount;
    }

    @Override
    public void setValueCount(int valueCount) {
        this.valueCount = valueCount;
        while (values.size() < valueCount) {
            values.add(null);
        }
    }

    @Override
    public void clear() {
        values.clear();
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
    public void copyFrom(int fromIndex, int thisIndex, ValueVectorV1 from) {
        if (from instanceof VarCharVectorV1) {
            VarCharVectorV1 fromVector = (VarCharVectorV1) from;
            set(thisIndex, fromVector.get(fromIndex));
        } else {
            throw new IllegalArgumentException("Cannot copy from " + from.getClass().getSimpleName());
        }
    }

    @Override
    public FieldVectorV1 getNewVector() {
        return new VarCharVectorV1(name, type);
    }

    @Override
    public void transferTo(FieldVectorV1 target) {
        if (target instanceof VarCharVectorV1) {
            VarCharVectorV1 targetVector = (VarCharVectorV1) target;
            targetVector.values = this.values;
            targetVector.valueCount = this.valueCount;
            this.values = new ArrayList<>();
            this.valueCount = 0;
        } else {
            throw new IllegalArgumentException("Cannot transfer to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public void copySubset(int fromIndex, int toIndex, FieldVectorV1 target, int targetIndex) {
        if (target instanceof VarCharVectorV1) {
            VarCharVectorV1 targetVector = (VarCharVectorV1) target;
            for (int i = fromIndex; i < toIndex; i++) {
                targetVector.set(targetIndex + i - fromIndex, this.get(i));
            }
        } else {
            throw new IllegalArgumentException("Cannot copy subset to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public FieldVectorV1 slice(int start, int end) {
        VarCharVectorV1 sliced = new VarCharVectorV1(name + "[" + start + "," + end + "]", type);
        sliced.values = new ArrayList<>(this.values.subList(start, end));
        sliced.valueCount = end - start;
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
        int nullCount = 0;
        for (int i = 0; i < valueCount; i++) {
            if (isNull(i)) {
                nullCount++;
            }
        }
        return nullCount;
    }

    @Override
    public void set(int index, Object value) {
        if (value == null) {
            setNull(index);
        } else if (value instanceof String) {
            set(index, (String) value);
        } else {
            throw new IllegalArgumentException("Value must be a String or null");
        }
    }

    @Override
    public void setSafe(int index, Object value) {
        while (index >= values.size()) {
            values.add(null);
        }
        set(index, value);
    }

    public void set(int index, String value) {
        while (index >= values.size()) {
            values.add(null);
        }
        values.set(index, value);
        valueCount = Math.max(valueCount, index + 1);
    }

    public String get(int index) {
        return values.get(index);
    }

    public void setNull(int index) {
        set(index, null);
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