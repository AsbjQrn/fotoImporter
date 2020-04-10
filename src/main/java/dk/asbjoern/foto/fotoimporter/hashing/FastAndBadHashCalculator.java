package dk.asbjoern.foto.fotoimporter.hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

public class FastAndBadHashCalculator {


    private final static int NUMBER_OF_BYTES = 1024;


    public static long checksum(File file) {
        long checksum = 0;

        try {
            CheckedInputStream cis = null;

            cis = new CheckedInputStream(new FileInputStream(file), new Adler32());

            byte[] buffer = new byte[NUMBER_OF_BYTES];
            while (cis.read(buffer) >= 0) {
                return cis.getChecksum().getValue();
            }
            checksum = cis.getChecksum().getValue();


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return checksum;
    }

    public static String firstBytesAsString(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return Arrays.toString(fis.readNBytes(NUMBER_OF_BYTES));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }


    public static Integer firstBytesAsInteger(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return Arrays.hashCode(fis.readNBytes(NUMBER_OF_BYTES));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }


    public static int firstBytesAsHashcode_Int(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return Arrays.hashCode(fis.readNBytes(NUMBER_OF_BYTES));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return 0;
    }

    public static byte[] firstBytes(File file) {

        try {
            FileInputStream fis = new FileInputStream(file);
            return fis.readNBytes(NUMBER_OF_BYTES);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;

    }

}
