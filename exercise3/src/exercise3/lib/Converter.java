package exercise3.lib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Converter<T> {
    /**
     * Reads the state (the attributes) for the specified object from the
     * specified data input and returns the restored object. The state of the
     * specified object before the call of <code>read</code> will be lost. When
     * <code>object&nbsp;==&nbsp;null</code> a new object should be created and
     * restored.<br />
     * The <code>read</code> method must read the values in the same sequence
     * and with the same types as were written by <code>write</code>.
     *
     * @param dataInput the stream to read data from in order to restore the
     *                  object.
     * @param object    the object to be restored. When
     *                  <code>object&nbsp;==&nbsp;null</code> a new object should be
     *                  created and restored.
     * @return the restored object.
     * @throws IOException if I/O errors occur.
     */
    T read(DataInput dataInput, T object) throws IOException;

    /**
     * Creates a new object by reading the state (the attributes) from the
     * specified data input and returns the restored object. The
     * <code>read</code> method must read the values in the same sequence and
     * with the same types as were written by <code>write</code>.
     *
     * @param dataInput the stream to read data from in order to restore the
     *                  object.
     * @return the restored object.
     * @throws IOException if I/O errors occur.
     */
    default T read(DataInput dataInput) throws IOException {
        return read(dataInput, null);
    }

    /**
     * Writes the state (the attributes) of the specified object to the
     * specified data output. This method should serialize the state of this
     * object without calling another <code>write</code> method in order to
     * prevent recursions.
     *
     * @param dataOutput the stream to write the state (the attributes) of the
     *                   object to.
     * @param object     the object whose state (attributes) should be written to
     *                   the data output.
     * @throws IOException includes any I/O exceptions that may occur.
     */
    void write(DataOutput dataOutput, T object) throws IOException;

    /**
     * Write the attribute at columnIndex of the object into dataOutput.
     */
    void writeColumn(DataOutput dataOutput, T object, int columnIndex) throws IOException;

    /**
     * Read attribute number columnIndex from the dataInput and store it in the given object.
     * If object is null, a new object will be created.
     */
    T readColumn(DataInput dataInput, T object, int columnIndex) throws IOException;

    default T readColumn(DataInput dataInput, int columnIndex) throws IOException {
        return readColumn(dataInput, null, columnIndex);
    }
}
