package dk.asbjoern.foto.fotoimporter.beans;

import dk.asbjoern.foto.fotoimporter.hashing.FastAndBadHashCalculator;

import java.io.File;

public class Mediafile {

    private final File file;

    private final Integer firstBytesChecksum;

    public Mediafile(File file) {
        this.file = file;
        firstBytesChecksum = FastAndBadHashCalculator.firstBytesAsInteger(file);
    }

    public File getFile() {
        return file;
    }

    public Integer getFirstBytesChecksum() {
        return firstBytesChecksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mediafile mediafile = (Mediafile) o;

        return firstBytesChecksum.equals(mediafile.firstBytesChecksum);
    }

    @Override
    public int hashCode() {
        return firstBytesChecksum.hashCode();
    }
}
