package dk.asbjoern.foto.fotoimporter.beans;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;


public class MediaWrapper extends FileWrapper {

    private Optional<LocalDate> dateTaken = Optional.ofNullable(null);


    public MediaWrapper(Path path) {
        super(path);
    }


}
