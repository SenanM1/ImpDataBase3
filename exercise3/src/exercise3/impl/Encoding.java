package exercise3.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Encoding {
    /**
     * Compresses the passed values using Differential Encoding.
     */
    public static int[] encodeDiff(int[] numbers) {
        // TODO
        if (numbers == null || numbers.length == 0) {
            return new int[0];  // Return an empty array if the input is empty
        }

        // Initialize the result array to hold the encoded values
        int[] encoded = new int[numbers.length];

        // The first value is the same as the input
        encoded[0] = numbers[0];

        // Loop through the rest of the numbers and store the difference with the previous number
        for (int i = 1; i < numbers.length; i++) {
            encoded[i] = numbers[i] - numbers[i - 1];
        }

        return encoded;
    }

    /**
     * Decompresses values previously compressed via Differential Encoding.
     */
    public static int[] decodeDiff(int[] numbers) {
        // TODO
        if (numbers == null || numbers.length == 0) {
            return new int[0];  // Return an empty array if the input is empty
        }

        int[] decoded = new int[numbers.length];
        decoded[0] = numbers[0];  // The first value is the same

        // Loop through and reconstruct the original values using the differences
        for (int i = 1; i < numbers.length; i++) {
            decoded[i] = decoded[i - 1] + numbers[i];
        }

        return decoded;
    }

    /**
     * Compresses the passed values using Variable Byte Encoding.
     */
    public static byte[] encodeVB(int[] numbers) {
        // TODO
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int num : numbers) {
            while (num > 0x7F) {  // While there are more than 7 bits
                byte b = (byte) ((num & 0x7F) | 0x80);  // Get the lower 7 bits and set the MSB
                outputStream.write(b & 0xFF);  // Mask to treat byte as unsigned
                num >>= 7;  // Right shift by 7 bits to get the next chunk
            }
            outputStream.write(num & 0x7F);  // Write the last byte (MSB is 0)
        }
        return outputStream.toByteArray();  // Return the byte array
    }

    /**
     * Decompresses values previously compressed via Variable Byte Encoding.
     */
    public static int[] decodeVB(byte[] vbs) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(vbs);
        ArrayList<Integer> decodedList = new ArrayList<>();

        int num = 0;
        int shift = 0;

        int b;
        while ((b = inputStream.read()) != -1) {
            num |= (b & 0x7F) << shift;  // Get the lower 7 bits and place them in the correct position
            shift += 7;

            if ((b & 0x80) == 0) {  // If MSB is 0, we've reached the last byte of the current number
                decodedList.add(num);
                num = 0;
                shift = 0;
            }
        }

        // Convert the list to an array
        return decodedList.stream().mapToInt(i -> i).toArray();

    }

    public static void main(String[] args) {
        int[] seq = {1, 7, 56, 134, 256, 268, 384, 472, 512, 648};

        // TODO

        // Differential Encoding
        int[] encoded = encodeDiff(seq);
        System.out.println("Encoded using Differential Encoding: ");
        for (int num : encoded) {
            System.out.print(num + " ");
        }
        System.out.println();

        // Differential Decoding
        int[] decoded = decodeDiff(encoded);
        System.out.println("Decoded using Differential Decoding: ");
        for (int num : decoded) {
            System.out.print(num + " ");
        }
        System.out.println();

        // Test Variable Byte Encoding and Decoding
        byte[] vbEncoded = encodeVB(seq);
        System.out.println("Encoded using Variable Byte Encoding: ");
        for (byte b : vbEncoded) {
            // Print the byte as unsigned
            System.out.print((b & 0xFF) + " ");
        }
        System.out.println();

        // Variable Byte Decoding
        int[] vbDecoded = decodeVB(vbEncoded);
        System.out.println("Decoded using Variable Byte Decoding: ");
        for (int num : vbDecoded) {
            System.out.print(num + " ");
        }
        System.out.println();   
    }
}
