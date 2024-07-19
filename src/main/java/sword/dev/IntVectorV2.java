package sword.dev;

import sword.dev.exceptions.OutOfMemoryException;
import sword.dev.type.SwordType;

import java.nio.ByteBuffer;
import java.util.Iterator;
import jdk.incubator.vector.*;
import java.nio.ByteOrder;

public class IntVectorV2 extends BaseValueVectorV2 implements FieldVectorV2 {
    private final SwordType.Int type;
    private SwordBuf dataBuffer;
    private SwordBuf validityBuffer;
    private int valueCount;
    private boolean nullable;

    public IntVectorV2(String name, BufferAllocator allocator) {
        this(name, new SwordType.Int(32), allocator); // Assuming 32-bit integers by default
    }

    public IntVectorV2(String name, SwordType.Int type, BufferAllocator allocator) {
        super(name, allocator);
        this.type = type;
        this.valueCount = 0;
        this.nullable = false;
    }

    @Override
    public SwordType getType() {
        return type;
    }

    @Override
    public void allocateNew() throws OutOfMemoryException {
        super.allocateNew();
        int typeWidth = type.getBitWidth() / 8;
        long size = (long) INITIAL_VALUE_ALLOCATION * typeWidth;
        dataBuffer = allocator.allocate((int)size);
        if (nullable) {
            validityBuffer = allocator.allocate(INITIAL_VALUE_ALLOCATION / 8);
        }
        valueCount = 0;
    }

    @Override
    public boolean allocateNewSafe() {
        try {
            allocateNew();
            return true;
        } catch (OutOfMemoryException e) {
            return false;
        }
    }

    @Override
    public void reAlloc() {
        int typeWidth = type.getBitWidth() / 8;
        long newSize = dataBuffer.capacity() * 2;
        SwordBuf newBuffer = allocator.allocate((int)newSize);
        newBuffer.setBytes(0, dataBuffer, 0, dataBuffer.capacity());
        allocator.free(dataBuffer);
        dataBuffer = newBuffer;

        if (nullable) {
            newSize = validityBuffer.capacity() * 2;
            newBuffer = allocator.allocate((int)newSize);
            newBuffer.setBytes(0, validityBuffer, 0, validityBuffer.capacity());
            allocator.free(validityBuffer);
            validityBuffer = newBuffer;
        }
    }

    @Override
    public int getValueCapacity() {
        return (int) (dataBuffer.capacity() / (type.getBitWidth() / 8));
    }

    @Override
    public void setInitialCapacity(int numRecords) {
        // Implementation depends on how you want to handle initial capacity
    }

    @Override
    public boolean isNull(int index) {
        if (!nullable) return false;
        byte b = validityBuffer.getByte(index / 8);
        int byteIndex = index % 8;
        return (b & (1 << byteIndex)) == 0;
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
        valueCount = 0;
        allocator.free(dataBuffer);
        if (nullable) {
            allocator.free(validityBuffer);
        }
    }

    @Override
    public Object getObject(int index) {
        return get(index);
    }

    @Override
    public void copyFrom(int fromIndex, int thisIndex, ValueVectorV2 from) {
        if (from instanceof IntVectorV2) {
            IntVectorV2 fromVector = (IntVectorV2) from;
            set(thisIndex, fromVector.get(fromIndex));
        } else {
            throw new IllegalArgumentException("Cannot copy from " + from.getClass().getSimpleName());
        }
    }

    @Override
    public void copyFromSafe(int fromIndex, int thisIndex, ValueVectorV2 from) {
        if (thisIndex >= getValueCapacity()) {
            reAlloc();
        }
        copyFrom(fromIndex, thisIndex, from);
    }

    @Override
    public FieldVectorV2 getNewVector() {
        return new IntVectorV2(getName(), type, allocator);
    }

    @Override
    public void transferTo(FieldVectorV2 target) {
        if (target instanceof IntVectorV2) {
            IntVectorV2 targetVector = (IntVectorV2) target;
            targetVector.dataBuffer = this.dataBuffer;
            targetVector.validityBuffer = this.validityBuffer;
            targetVector.valueCount = this.valueCount;
            this.dataBuffer = null;
            this.validityBuffer = null;
            this.valueCount = 0;
        } else {
            throw new IllegalArgumentException("Cannot transfer to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public void copySubset(int fromIndex, int toIndex, FieldVectorV2 target, int targetIndex) {
        if (target instanceof IntVectorV2) {
            IntVectorV2 targetVector = (IntVectorV2) target;
            int length = toIndex - fromIndex;
            targetVector.dataBuffer.setBytes(targetIndex * 4, this.dataBuffer, fromIndex * 4, length * 4);
            if (nullable) {
                // Copy validity buffer
                int fromByteIndex = fromIndex / 8;
                int toByteIndex = targetIndex / 8;
                int lengthInBytes = (length + 7) / 8;
                targetVector.validityBuffer.setBytes(toByteIndex, this.validityBuffer, fromByteIndex, lengthInBytes);
            }
            targetVector.valueCount = Math.max(targetVector.valueCount, targetIndex + length);
        } else {
            throw new IllegalArgumentException("Cannot copy subset to " + target.getClass().getSimpleName());
        }
    }

    @Override
    public FieldVectorV2 slice(int start, int end) {
        IntVectorV2 sliced = new IntVectorV2(getName() + "[" + start + "," + end + "]", type, allocator);
        int length = end - start;
        sliced.dataBuffer = dataBuffer.slice(start * 4, length * 4);
        if (nullable) {
            int startByte = start / 8;
            int endByte = (end + 7) / 8;
            sliced.validityBuffer = validityBuffer.slice(startByte, endByte - startByte);
        }
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
        if (nullable && validityBuffer == null) {
            validityBuffer = allocator.allocate(getValueCapacity() / 8);
        }
    }

    @Override
    public int getNullCount() {
        if (!nullable) return 0;
        int nullCount = 0;
        for (int i = 0; i < valueCount; i++) {
            if (isNull(i)) nullCount++;
        }
        return nullCount;
    }

    @Override
    public void set(int index, Object value) {
        if (value == null) {
            setNull(index);
        } else if (value instanceof Integer) {
            set(index, (int) value);
        } else {
            throw new IllegalArgumentException("Value must be an Integer or null");
        }
    }

    @Override
    public void setSafe(int index, Object value) {
        while (index >= getValueCapacity()) {
            reAlloc();
        }
        set(index, value);
    }

    public void setSimd(int index, int[] values) {
        VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
        int vectorLength = SPECIES.length();
        int loopBound = SPECIES.loopBound(values.length);

        for (int i = 0; i < loopBound; i += vectorLength) {
            IntVector.fromArray(SPECIES, values, i)
                    .intoArray(dataBuffer.getIntArray(), index + i);
        }

        // Handle remaining elements
        for (int i = loopBound; i < values.length; i++) {
            set(index + i, values[i]);
        }

        valueCount = Math.max(valueCount, index + values.length);
    }


    /*
    public void setSimd(int index, int[] values) {
        int vectorLength = IntVector.SPECIES_256.length();
        int loopCount = values.length / vectorLength;
        int loopBound = loopCount * vectorLength;

        for (int i = 0; i < loopBound; i += vectorLength) {
            IntVector.SPECIES_256.fromArray(values, i)
                    .intoByteBuffer(dataBuffer.nioBuffer(), (index + i) * 4, ByteOrder.LITTLE_ENDIAN);
        }

        // Handle remaining elements
        for (int i = loopBound; i < values.length; i++) {
            set(index + i, values[i]);
        }

        if (nullable) {
            for (int i = 0; i < values.length; i++) {
                BitVectorHelper.setBit(validityBuffer, index + i);
            }
        }

        valueCount = Math.max(valueCount, index + values.length);
    }
     */

    public void getSimd(int index, int[] values) {
        VectorSpecies<Integer> SPECIES = IntVector.SPECIES_256;
        int vectorLength = SPECIES.length();
        int loopCount = values.length / vectorLength;
        int loopBound = loopCount * vectorLength;

        for (int i = 0; i < loopBound; i += vectorLength) {
            IntVector intVector = IntVector.fromByteBuffer(SPECIES, dataBuffer.nioBuffer(), (index + i) * 4, ByteOrder.LITTLE_ENDIAN);
            intVector.intoArray(values, i);
        }

        // Handle remaining elements
        for (int i = loopBound; i < values.length; i++) {
            values[i] = get(index + i);
        }
    }



    public void set(int index, int value) {
        dataBuffer.setInt(index * 4, value);
        if (nullable) {
            BitVectorHelper.setBit(validityBuffer, index);
        }
        valueCount = Math.max(valueCount, index + 1);
    }

    public int get(int index) {
        return dataBuffer.getInt(index * 4);
    }

    public void setNull(int index) {
        if (!nullable) throw new UnsupportedOperationException("This vector is not nullable");
        BitVectorHelper.unsetBit(validityBuffer, index);
        valueCount = Math.max(valueCount, index + 1);
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
                return isNull(index) ? null : get(index++);
            }
        };
    }

    @Override
    public int getBufferSize() {
        return dataBuffer.capacity() + (nullable ? validityBuffer.capacity() : 0);
    }

    @Override
    public int getBufferSizeFor(int valueCount) {
        return valueCount * 4 + (nullable ? (valueCount + 7) / 8 : 0);
    }

    @Override
    public SwordBuf[] getBuffers(boolean clear) {
        SwordBuf[] buffers = nullable ? new SwordBuf[2] : new SwordBuf[1];
        buffers[0] = dataBuffer;
        if (nullable) {
            buffers[1] = validityBuffer;
        }
        if (clear) {
            clear();
        }
        return buffers;
    }

    @Override
    public SwordBuf getValidityBuffer() {
        return validityBuffer;
    }

    @Override
    public SwordBuf getDataBuffer() {
        return dataBuffer;
    }

    @Override
    public SwordBuf getOffsetBuffer() {
        return null; // IntVector doesn't use an offset buffer
    }
}
