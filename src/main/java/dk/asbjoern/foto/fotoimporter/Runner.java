package dk.asbjoern.foto.fotoimporter;

import dk.asbjoern.foto.fotoimporter.beans.FileWrapper;
import dk.asbjoern.foto.fotoimporter.configuration.YMLConfiguration;
import dk.asbjoern.foto.fotoimporter.exifutils.ExifService;
import dk.asbjoern.foto.fotoimporter.fileutils.Directorymaker;
import dk.asbjoern.foto.fotoimporter.fileutils.FileWriter;
import dk.asbjoern.foto.fotoimporter.helpers.Loggable;
import dk.asbjoern.foto.fotoimporter.fileutils.FindFilesToMerge;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class Runner implements Loggable {

    private final FindFilesToMerge fileMapper;
    private final FileWriter fileWriter;
    private final ExifService exifService;
    private final Directorymaker directorymaker;
    private final YMLConfiguration ymlConfiguration;


    public Runner(FindFilesToMerge fileMapper, FileWriter fileWriter, ExifService exifService, Directorymaker directorymaker, YMLConfiguration ymlConfiguration) {
        this.fileMapper = fileMapper;
        this.fileWriter = fileWriter;
        this.exifService = exifService;
        this.directorymaker = directorymaker;
        this.ymlConfiguration = ymlConfiguration;
    }


    public void run() throws Exception {

        List<FileWrapper> filesToMerge = fileMapper.makeListOfImports(ymlConfiguration.getSourcePath(), ymlConfiguration.getDestinationsPath());

        for (FileWrapper fileWrapper : filesToMerge) {

            Optional<LocalDate> dateRecorded = exifService.readExifDate(exifService.readExif(fileWrapper.getFile()));
            Path newPath;
            if (dateRecorded.isPresent()) {
                newPath = directorymaker.makeDirectory(dateRecorded.get());
            } else {
                newPath = directorymaker.makeDirectory(fileWrapper.getPath().getParent());
            }

            fileWriter.writeFile(fileWrapper.getPath(), newPath);

        }
        ;


    }
}
