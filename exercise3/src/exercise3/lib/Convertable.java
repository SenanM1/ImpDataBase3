package exercise3.lib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Convertable {
    /**
     * Reads the state (the attributes) for an object of this class from
     * the specified data input and restores the calling object. The state
     * of the object before calling <tt>read</tt> will be lost.<br>
     * The <tt>read</tt> method must read the values in the same sequence
     * and with the same types as were written by <tt>write</tt>.
     *
     * @param dataInput the stream to read data from in order to restore
     *                  the object.
     * @throws IOException if I/O errors occur.
     */
    void read(DataInput dataInput) throws IOException;

    /**
     * Writes the state (the attributes) of the calling object to the
     * specified data output. This method should serialize the state of
     * this object without calling another <tt>write</tt> method in order
     * to prevent recursions.
     *
     * @param dataOutput the stream to write the state (the attributes) of
     *                   the object to.
     * @throws IOException includes any I/O exceptions that may occur.
     */
    void write(DataOutput dataOutput) throws IOException;
}
