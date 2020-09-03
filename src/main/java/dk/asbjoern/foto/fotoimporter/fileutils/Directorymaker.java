package dk.asbjoern.foto.fotoimporter.fileutils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

public interface Directorymaker {

    Path makeDirectory(Path filesOriginalpath) throws IOException;

    Path makeDirectory(LocalDate date) throws IOException;

}
