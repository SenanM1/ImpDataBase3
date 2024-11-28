package exercise3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import exercise3.impl.NSMPage;
import exercise3.impl.PaxPage;
import exercise3.lib.Page;
import exercise3.lib.FixedSizeConverter;

public class PaxMain {

    public static void main(String[] args) throws IOException {
        NSMPage<IntRecord> nsmPage = new NSMPage<>(65, new IntRecordConverter());
        PaxPage<IntRecord> paxPage = new PaxPage<>(65, new IntRecordConverter(), new int[]{4, 4, 4});

        testPage(nsmPage);
        // TODO:
        // testPage(paxPage)
    }

    static class IntRecord {
        int i1;
        int i2;
        int i3;

        public IntRecord() {
        }

        public IntRecord(int i1, int i2, int i3) {
            this.i1 = i1;
            this.i2 = i2;
            this.i3 = i3;
        }

        @Override
        public String toString() {
            return "[" + i1 + ", " + i2 + ", " + i3 + "]";
        }
    }

    static class IntRecordConverter extends FixedSizeConverter<IntRecord> {
        final static int INT_RECORD_SIZE = Integer.BYTES * 3;

        public IntRecordConverter() {
            super(INT_RECORD_SIZE);
        }

        @Override
        public IntRecord read(DataInput dataInput, IntRecord object) throws IOException {
            if (object == null) {
                object = new IntRecord();
            }
            object.i1 = dataInput.readInt();
            object.i2 = dataInput.readInt();
            object.i3 = dataInput.readInt();
            return object;
        }

        @Override
        public void write(DataOutput dataOutput, IntRecord rec) throws IOException {
            dataOutput.writeInt(rec.i1);
            dataOutput.writeInt(rec.i2);
            dataOutput.writeInt(rec.i3);
        }

        @Override
        public void writeColumn(DataOutput dataOutput, IntRecord rec, int i) throws IOException {
            switch (i) {
                case 0:
                    dataOutput.writeInt(rec.i1);
                    break;
                case 1:
                    dataOutput.writeInt(rec.i2);
                    break;
                case 2:
                    dataOutput.writeInt(rec.i3);
                    break;
                default:
                    throw new RuntimeException("Invalid column index: " + i);
            }
        }

        @Override
        public IntRecord readColumn(DataInput dataInput, IntRecord object, int i) throws IOException {
            if (object == null) {
                object = new IntRecord();
            }
            switch (i) {
                case 0:
                    object.i1 = dataInput.readInt();
                    break;
                case 1:
                    object.i2 = dataInput.readInt();
                    break;
                case 2:
                    object.i3 = dataInput.readInt();
                    break;
                default:
                    throw new RuntimeException("Invalid column index: " + i);
            }
            return object;
        }
    }


    private static void testPage(Page<IntRecord> page) throws IOException {
        IntRecord r1 = new IntRecord(1, 1, 1);
        IntRecord r2 = new IntRecord(2, 2, 2);
        IntRecord r3 = new IntRecord(3, 3, 3);
        IntRecord r4 = new IntRecord(4, 4, 4);
        IntRecord r5 = new IntRecord(5, 5, 5);
        IntRecord r6 = new IntRecord(6, 6, 6);

        System.out.println("Free space: " + page.getFreeSpace());
        page.store(r1);
        System.out.println("Free space: " + page.getFreeSpace());
        page.store(r2);
        System.out.println("Free space: " + page.getFreeSpace());

        short i3 = page.store(r3);
        System.out.println("Free space: " + page.getFreeSpace());
        page.store(r4);
        System.out.println("Free space: " + page.getFreeSpace());
        page.store(r5);
        System.out.println("Free space: " + page.getFreeSpace());

        page.print();

        page.delete(i3);
        System.out.println("Free space: " + page.getFreeSpace());

        page.print();

        page.store(r6);
        System.out.println("Free space: " + page.getFreeSpace());

        page.print();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        page.write(dos);
        page.read(new DataInputStream(new ByteArrayInputStream(bos.toByteArray())));

        System.out.println("Free space (after serialization): " + page.getFreeSpace());

        page.print();
    }
}
