package dk.asbjoern.foto.fotoimporter.hashing;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class HashCalculator {


    public void givenFile_generatingChecksum_thenVerifying()
            throws NoSuchAlgorithmException, IOException {
        String filename = "src/test/resources/test_md5.txt";
        String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(filename)));
        byte[] digest = md.digest();
        String myChecksum = DataTypeConverter
                .printHexBinary(digest).toUpperCase();

        assertThat(myChecksum.equals(checksum)).isTrue();
    }

}
