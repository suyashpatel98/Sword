package sword.dev;

import sword.dev.exceptions.OutOfMemoryException;
import sword.dev.type.SwordType;

import java.io.Closeable;

public interface ValueVectorV2 extends Closeable, Iterable<Object> {

    /**
     * Get the maximum number of values that can be stored in this vector instance.
     *
     * @return the maximum number of values that can be stored in this vector instance.
     */
    int getValueCapacity();

    /**
     * Release any resources and reset the sword.dev.ValueVector to the initial state.
     */
    void clear();

    /**
     * Get the number of values in the vector.
     *
     * @return number of values in the vector
     */
    int getValueCount();

    /**
     * Set number of values in the vector.
     */
    void setValueCount(int valueCount);

    /**
     * Get object at the specified index.
     *
     * @param index index of object to get
     * @return object at the specified index
     */
    Object getObject(int index);

    /**
     * Check whether an element in the vector is null.
     *
     * @param index index to check for null
     * @return true if element is null
     */
    boolean isNull(int index);

    /**
     * Copy a cell value from a particular index in source vector to a particular position in this vector.
     *
     * @param fromIndex position to copy from in source vector
     * @param thisIndex position to copy to in this vector
     * @param from source vector
     */
    void copyFrom(int fromIndex, int thisIndex, ValueVectorV2 from);

    /**
     * Get the sword.dev.type of the vector.
     *
     * @return the sword.dev.type of the vector
     */
    SwordType getType();

    @Override
    void close();

    /**
     * Get the BufferAllocator associated with this vector.
     *
     * @return the BufferAllocator
     */
    BufferAllocator getAllocator();

    /**
     * Allocate new buffers. ValueVector implements logic to determine how much to allocate.
     *
     * @throws OutOfMemoryException Thrown if no memory can be allocated.
     */
    void allocateNew() throws OutOfMemoryException;

    /**
     * Allocate new buffers. ValueVector implements logic to determine how much to allocate.
     *
     * @return Returns true if allocation was successful.
     */
    boolean allocateNewSafe();

    /**
     * Allocate new buffer with double capacity, and copy data into the new buffer. Replace vector's
     * buffer with new buffer, and release old one.
     */
    void reAlloc();

    /**
     * Set the initial record capacity.
     *
     * @param numRecords the initial record capacity.
     */
    void setInitialCapacity(int numRecords);

    /**
     * Get the number of bytes used by this vector.
     *
     * @return the number of bytes that is used by this vector instance.
     */
    int getBufferSize();

    /**
     * Returns the number of bytes that is used by this vector if it holds the given number of values.
     *
     * @param valueCount the number of values to assume this vector contains
     * @return the buffer size if this vector is holding valueCount values
     */
    int getBufferSizeFor(int valueCount);

    /**
     * Return the underlying buffers associated with this vector.
     *
     * @param clear Whether to clear vector before returning
     * @return The underlying SwordBuf buffers that are used by this vector instance.
     */
    SwordBuf[] getBuffers(boolean clear);

    /**
     * Gets the underlying buffer associated with validity vector.
     *
     * @return buffer
     */
    SwordBuf getValidityBuffer();

    /**
     * Gets the underlying buffer associated with data vector.
     *
     * @return buffer
     */
    SwordBuf getDataBuffer();

    /**
     * Gets the underlying buffer associated with offset vector.
     *
     * @return buffer
     */
    SwordBuf getOffsetBuffer();
}
