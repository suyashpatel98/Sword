package sword.dev;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SwordBuf implements AutoCloseable {
    private ByteBuffer buffer;
    private final BufferAllocator allocator;
    private final int size;
    private boolean closed;
    private int[] intArray;

    public int[] getIntArray() {
        return intArray;
    }

    public SwordBuf(BufferAllocator allocator, int size) {
        this.allocator = allocator;
        this.size = size;
        this.buffer = ByteBuffer.allocateDirect((size + 15) & ~15).order(ByteOrder.LITTLE_ENDIAN);
        this.intArray = new int[size];
        this.closed = false;
    }

    public int capacity() {
        return size;
    }

    public int getInt(long index) {
        checkIndex(index, 4);
        //return buffer.getInt((int) index);
        return intArray[(int)index];
    }

    public void setInt(long index, int value) {
        checkIndex(index, 4);
        //buffer.putInt((int) index, value);
        intArray[(int)index] = value;
    }

    public byte getByte(long index) {
        checkIndex(index, 1);
        return buffer.get((int) index);
    }

    public void setByte(long index, byte value) {
        checkIndex(index, 1);
        buffer.put((int) index, value);
    }

    public void getBytes(long index, byte[] dst, int dstIndex, int length) {
        checkIndex(index, length);
        buffer.position((int) index);
        buffer.get(dst, dstIndex, length);
    }

    public void setBytes(long index, byte[] src, int srcIndex, int length) {
        checkIndex(index, length);
        buffer.position((int) index);
        buffer.put(src, srcIndex, length);
    }

    public void setBytes(long index, SwordBuf srcBuffer, long srcIndex, long length) {
        checkIndex(index, length);
        srcBuffer.checkIndex(srcIndex, length);
        for (long i = 0; i < length; i++) {
            buffer.put((int) (index + i), srcBuffer.getByte(srcIndex + i));
        }
    }

    public SwordBuf slice(long offset, int length) {
        checkIndex(offset, length);
        ByteBuffer slicedBuffer = buffer.slice((int) offset, (int) length);
        SwordBuf slicedBuf = new SwordBuf(allocator, length);
        slicedBuf.buffer = slicedBuffer;
        return slicedBuf;
    }

    public void clear() {
        buffer.clear();
    }

    @Override
    public void close() {
        if (!closed) {
            allocator.free(this);
            closed = true;
        }
    }

    private void checkIndex(long index, long length) {
        if (index < 0 || index + length > size) {
            throw new IndexOutOfBoundsException("Index out of bounds: index=" + index + ", length=" + length + ", capacity=" + size);
        }
    }

    public ByteBuffer nioBuffer() {
        return buffer;
    }
}

