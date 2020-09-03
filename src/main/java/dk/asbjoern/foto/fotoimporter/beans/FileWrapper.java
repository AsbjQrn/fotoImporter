package dk.asbjoern.foto.fotoimporter.beans;

import dk.asbjoern.foto.fotoimporter.hashing.FastAndBadHashCalculator;
import dk.asbjoern.foto.fotoimporter.hashing.SlowAndGoodHashCalculator;

import java.io.File;
import java.nio.file.Path;

public class FileWrapper {

    private final Path path;

    private final Integer firstBytesChecksum;
    private String checksum;

    public FileWrapper(Path path) {
        this.path = path;
        firstBytesChecksum = FastAndBadHashCalculator.firstBytesAsInteger(getFile());
    }

    public File getFile() {
        return path.toFile();
    }

    public Path getPath() {
        return path;
    }

    public Integer getFirstBytesChecksum() {
        return firstBytesChecksum;
    }

    public String getChecksum() {
        if (checksum == null) {
            checksum = SlowAndGoodHashCalculator.checksum(getFile());
        }
        return checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileWrapper fileWrapper = (FileWrapper) o;

        return firstBytesChecksum.equals(fileWrapper.firstBytesChecksum);
    }

    @Override
    public int hashCode() {
        return firstBytesChecksum.hashCode();
    }
}
