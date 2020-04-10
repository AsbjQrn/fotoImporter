package dk.asbjoern.foto.fotoimporter.hashing;

import org.springframework.stereotype.Component;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class SlowAndGoodHashCalculator {

    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String checksum(File file) {

        byte[] buffer = new byte[8192];
        int count;
        byte[] hash = null;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {

            while ((count = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
//            bis.close();

            hash = digest.digest();
        } catch (IOException e) {
            System.exit(1);

        }
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
