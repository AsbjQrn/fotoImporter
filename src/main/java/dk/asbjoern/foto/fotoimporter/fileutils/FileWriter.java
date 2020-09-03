package dk.asbjoern.foto.fotoimporter.fileutils;

import java.nio.file.Path;

public interface FileWriter {

    void writeFile(Path originalpath, Path newPath) throws Exception;


}
