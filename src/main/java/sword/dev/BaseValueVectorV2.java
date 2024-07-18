package sword.dev;

import sword.dev.exceptions.OutOfMemoryException;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;


public abstract class BaseValueVectorV2 implements ValueVectorV2 {

    protected static final int INITIAL_VALUE_ALLOCATION = 100000000; // Simplified initial allocation

    protected final BufferAllocator allocator;
    protected String name;

    protected BaseValueVectorV2(String name, BufferAllocator allocator) {
        this.allocator = Objects.requireNonNull(allocator, "allocator cannot be null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + ", valueCount=" + getValueCount() + "]";
    }

    @Override
    public void clear() {
        // To be implemented by subclasses
    }

    @Override
    public void close() {
        clear();
    }

    @Override
    public Iterator<Object> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public BufferAllocator getAllocator() {
        return allocator;
    }

    protected void compareTypes(BaseValueVectorV2 target, String caller) {
        if (!this.getType().equals(target.getType())) {
            throw new UnsupportedOperationException(caller + " should have vectors of exact same type");
        }
    }

    protected void releaseBuffer(SwordBuf buffer) {
        if (buffer != null) {
            buffer.close();
            allocator.free(buffer);
        }
    }

    protected static int getValidityBufferSizeFromCount(final int valueCount) {
        return (valueCount + 7) / 8;
    }

    protected long computeCombinedBufferSize(int valueCount, int typeWidth) {
        long validityBufferSize = getValidityBufferSizeFromCount(valueCount);
        long dataBufferSize = (long) valueCount * typeWidth;
        return validityBufferSize + dataBufferSize;
    }

    protected static class DataAndValidityBuffers {
        private SwordBuf dataBuf;
        private SwordBuf validityBuf;

        DataAndValidityBuffers(SwordBuf dataBuf, SwordBuf validityBuf) {
            this.dataBuf = dataBuf;
            this.validityBuf = validityBuf;
        }

        SwordBuf getDataBuf() {
            return dataBuf;
        }

        SwordBuf getValidityBuf() {
            return validityBuf;
        }
    }

    protected DataAndValidityBuffers allocFixedDataAndValidityBufs(int valueCount, int typeWidth) {
        long bufferSize = computeCombinedBufferSize(valueCount, typeWidth);
        SwordBuf combinedBuffer = allocator.allocate((int)bufferSize);

        int validityBufferSize = getValidityBufferSizeFromCount(valueCount);
        int dataBufferSize = valueCount * typeWidth;

        SwordBuf validityBuf = combinedBuffer.slice(0, validityBufferSize);
        SwordBuf dataBuf = combinedBuffer.slice(validityBufferSize, dataBufferSize);

        return new DataAndValidityBuffers(dataBuf, validityBuf);
    }

    @Override
    public void allocateNew() throws OutOfMemoryException {
        // To be implemented by subclasses
    }

    @Override
    public boolean allocateNewSafe() {
        // To be implemented by subclasses
        return false;
    }

    @Override
    public void reAlloc() {
        // To be implemented by subclasses
    }

    @Override
    public void setInitialCapacity(int numRecords) {
        // To be implemented by subclasses
    }

    @Override
    public int getBufferSize() {
        // To be implemented by subclasses
        return 0;
    }

    @Override
    public int getBufferSizeFor(int valueCount) {
        // To be implemented by subclasses
        return 0;
    }

    @Override
    public SwordBuf[] getBuffers(boolean clear) {
        // To be implemented by subclasses
        return new SwordBuf[0];
    }

    @Override
    public SwordBuf getValidityBuffer() {
        // To be implemented by subclasses
        return null;
    }

    @Override
    public SwordBuf getDataBuffer() {
        // To be implemented by subclasses
        return null;
    }

    @Override
    public SwordBuf getOffsetBuffer() {
        // To be implemented by subclasses
        return null;
    }
}