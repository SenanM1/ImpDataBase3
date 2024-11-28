package exercise3.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import exercise3.lib.FixedSizeConverter;
import exercise3.lib.Page;
import exercise3.lib.Block;

/**
 * Page implementation storing fixed size records
 * row by row.
 */
public class NSMPage<T> implements Page<T> {
    /**
     * The available space for the actual records
     */
    private final int dataSize;

    /**
     * Converter used
     */
    private final FixedSizeConverter<T> converter;

    /**
     * Block storing serialized records
     */
    private final Block data;

    /**
     * Meta-data, indicating whether a slot is
     * used or not
     */
    private final boolean[] slotMask;

    /**
     * The remaining size for storing data
     */
    private int sizeRemaining;

    /**
     * @param size      the total size of this page (in bytes)
     * @param converter the converter to use for record serialization
     */
    public NSMPage(int size, FixedSizeConverter<T> converter) {
        this.converter = converter;
        // Calculate the maximum number of records we can store
        int numRecords = (size / converter.getSerializedSize());
        if (numRecords * converter.getSerializedSize() + numRecords > size)
            numRecords--;
        // All slots empty initially
        this.slotMask = new boolean[numRecords];
        Arrays.fill(slotMask, false);
        // Calculate sizes
        this.dataSize = size - slotMask.length;
        this.data = new Block(dataSize);
        this.sizeRemaining = numRecords * converter.getSerializedSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(DataInput dataInput) throws IOException {
        sizeRemaining = slotMask.length * getRecordSize();

        // Read free space mask
        int numRecords = 0;
        for (int i = 0; i < slotMask.length; i++) {
            slotMask[i] = dataInput.readBoolean();
            numRecords += slotMask[i] ? 1 : 0;
        }
        sizeRemaining -= (numRecords * getRecordSize());
        // Read data
        dataInput.readFully(data.array, 0, dataSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        for (boolean b : slotMask) dataOutput.writeBoolean(b);
        dataOutput.write(data.array, data.offset, data.size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFreeSpace() {
        return sizeRemaining;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRecordSize() {
        return converter.getSerializedSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short store(T element) {
        // Get free slot
        short id = nextFreeId();
        int offset = id * getRecordSize();
        try {
            // Save record, update mask and remaining size
            converter.write(data.dataOutputStream(offset, offset + getRecordSize()), element);
            sizeRemaining -= getRecordSize();
            slotMask[id] = true;
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(short id) {
        // Do not physically delete this record (too expensive)
        if (slotMask[id]) {
            sizeRemaining += getRecordSize();
            slotMask[id] = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(short id) {
        // Lookup if id exists
        if (slotMask[id]) {
            try {
                int offset = id * getRecordSize();
                return converter.read(data.dataInputStream(offset, offset + getRecordSize()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    /**
     * Retrieves the next free slot to use (First Fit)
     *
     * @return the slot to use for a new record
     */
    private short nextFreeId() {
        for (short id = 0; id < slotMask.length; id++) {
            if (!slotMask[id])
                return id;
        }
        throw new RuntimeException("No free ids available");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Short> ids() {
        return new Iterator<Short>() {
            short idx = 0;
            short next = computeNext();

            short computeNext() {
                while (idx < slotMask.length && !slotMask[idx])
                    idx++;
                return idx++;
            }

            @Override
            public boolean hasNext() {
                return next < slotMask.length;
            }

            @Override
            public Short next() {
                short res = next;
                next = computeNext();
                return res;
            }
        };
    }
}
