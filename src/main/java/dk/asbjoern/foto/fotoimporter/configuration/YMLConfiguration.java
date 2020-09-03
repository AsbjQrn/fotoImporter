package dk.asbjoern.foto.fotoimporter.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class YMLConfiguration {


    private Path sourcePath;
    private Path destinationsPath;
    private Path noExifPath;


    @Autowired
    public YMLConfiguration(@Value("${sourceLib}") final String sourceLib, @Value("${destinationLib}") final String destinationLib
            , @Value("${noExifFolderName}") final String noExifFolderName) {

        this.sourcePath = Paths.get(sourceLib);
        this.destinationsPath = Paths.get(destinationLib);
        this.noExifPath = destinationsPath.resolve(Paths.get(noExifFolderName));
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public Path getDestinationsPath() {
        return destinationsPath;
    }

    public Path getNoExifPath() {
        return noExifPath;
    }
}
