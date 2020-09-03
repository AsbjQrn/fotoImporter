package dk.asbjoern.foto.fotoimporter.fileutils;

import dk.asbjoern.foto.fotoimporter.helpers.Loggable;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class FileWriterWithNameCheck implements FileWriter, Loggable {

    private static int renameNumber = 0;

    public void writeFile(Path originalpath, Path newPath) throws Exception {

        Path fileName = originalpath.getFileName();

        while (Files.exists(newPath.resolve(fileName))) {
            fileName = makeNewFileName(fileName);
        }

        Files.copy(originalpath, newPath.resolve(fileName));

    }

    private Path makeNewFileName(Path path) throws Exception {

        String fullFileName = path.toString();
        String fileFirstName;
        String fileType = "";

        if (fullFileName.indexOf('.') > -1) {
            String[] fileNameParts = fullFileName.split("\\.");

            StringBuilder fileFirstNameParts = new StringBuilder();
            for (int i = 0; i < fileNameParts.length - 1; i++) {
                fileFirstNameParts.append(fileNameParts[i]);
            }

            fileFirstName = fileFirstNameParts.toString();
            fileType = fileNameParts[fileNameParts.length - 1];
            return Paths.get(fileFirstName + "(" + ++renameNumber + ")" + "." + fileType);

        } else {
            fileFirstName = fullFileName;
            return Paths.get(fileFirstName + "(" + ++renameNumber + ")" );
        }
    }


}
