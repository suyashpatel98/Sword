package sword.dev;

import sword.dev.type.SwordType;


public interface FieldVector extends ValueVector {

    /**
     * Get the SwordType of this vector.
     *
     * @return the SwordType of this vector
     */
    SwordType getType();

    /**
     * Get the name of this vector.
     *
     * @return the name of this vector
     */
    String getName();

    /**
     * Set the name of this vector.
     *
     * @param name the name to set for this vector
     */
    void setName(String name);

    /**
     * Get a new empty instance of the same sword.dev.type of vector.
     *
     * @return a new empty instance of the same sword.dev.type of vector
     */
    FieldVector getNewVector();

    /**
     * Transfer the data from this vector to another vector of the same sword.dev.type.
     *
     * @param target the target vector to transfer data to
     */
    void transferTo(FieldVector target);

    /**
     * Copy a subset of the data from this vector to another vector of the same sword.dev.type.
     *
     * @param fromIndex the starting index in this vector to copy from
     * @param toIndex the ending index (exclusive) in this vector to copy from
     * @param target the target vector to copy data to
     * @param targetIndex the starting index in the target vector to copy to
     */
    void copySubset(int fromIndex, int toIndex, FieldVector target, int targetIndex);

    /**
     * Get a slice of this vector.
     *
     * @param start the starting index of the slice
     * @param end the ending index (exclusive) of the slice
     * @return a new sword.dev.FieldVector representing the slice
     */
    FieldVector slice(int start, int end);

    /**
     * Check if this vector is nullable (can contain null values).
     *
     * @return true if this vector can contain null values, false otherwise
     */
    boolean isNullable();

    /**
     * Set whether this vector is nullable.
     *
     * @param nullable true if this vector should be able to contain null values, false otherwise
     */
    void setNullable(boolean nullable);

    /**
     * Get the number of null values in this vector.
     *
     * @return the number of null values in this vector
     */
    int getNullCount();

    /**
     * Set a value at the specified index.
     *
     * @param index the index to set the value at
     * @param value the value to set
     */
    void set(int index, Object value);

    /**
     * Set a value at the specified index, expanding the vector if necessary.
     *
     * @param index the index to set the value at
     * @param value the value to set
     */
    void setSafe(int index, Object value);
}
