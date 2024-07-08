package sword.dev;

import sword.dev.type.SwordType;

import java.io.Closeable;

public interface ValueVector extends Closeable, Iterable<Object> {

    /**
     * Allocate new buffers for the vector.
     */
    void allocateNew();

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
    void copyFrom(int fromIndex, int thisIndex, ValueVector from);

    /**
     * Get the sword.dev.type of the vector.
     *
     * @return the sword.dev.type of the vector
     */
    SwordType getType();

    @Override
    void close();
}
