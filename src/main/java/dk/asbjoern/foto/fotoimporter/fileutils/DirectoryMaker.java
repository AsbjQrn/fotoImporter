package dk.asbjoern.foto.fotoimporter.fileutils;

import dk.asbjoern.foto.fotoimporter.beans.FileWrapper;
import dk.asbjoern.foto.fotoimporter.configuration.YMLConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Component
public class DirectoryMaker implements Directorymaker{


    private YMLConfiguration config;

    public DirectoryMaker(YMLConfiguration config) {
        this.config = config;
    }


    private Path makeIfNotExists(Path path) throws IOException {

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }


//    fileWrapper.getPath() = path
    public Path makeDirectory(Path filesOriginalpath) throws IOException {

        //Hvis SourcePath og filesOriginalpath er lige lange betyder det at filens nye lokation skal være i parentdirectory
        return makeIfNotExists(config.getSourcePath().getNameCount() == filesOriginalpath.getNameCount() ? config.getNoExifPath() : config.getNoExifPath().resolve(filesOriginalpath.subpath(config.getSourcePath().getNameCount(), filesOriginalpath.getNameCount())));

    }

    public Path makeDirectory(LocalDate date) throws IOException {


        Path yearMonthPath = Paths.get(Integer.toString(date.getYear()), date.getMonth().toString());
        Path newLocation = config.getDestinationsPath().resolve(yearMonthPath);

        makeIfNotExists(newLocation);

        return newLocation;


    }
}
