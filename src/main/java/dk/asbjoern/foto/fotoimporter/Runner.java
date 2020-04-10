package dk.asbjoern.foto.fotoimporter;

import dk.asbjoern.foto.fotoimporter.beans.Mediafile;
import dk.asbjoern.foto.fotoimporter.helpers.Loggable;
import dk.asbjoern.foto.fotoimporter.mapping.FileMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class Runner implements Loggable {

    private final FileMapper fileMapper;

    public Runner(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public void run() throws IOException {

        Path receivingLibrary = Paths.get("/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/");
        Path possibleImports = Paths.get("files");

        long startMakeMapOfAll = System.currentTimeMillis();
        Map<Integer, List<Mediafile>> mapOfReceivingLibrary = fileMapper.makeMapOfAll(receivingLibrary);
        long slutMakeMapOfAll = System.currentTimeMillis();

        long startMakeMapOfOnlyUnique = System.currentTimeMillis();
        Map<String, Mediafile> uniquePossibleImports = fileMapper.makeMapOfOnlyUnique(fileMapper.makeMapOfAll(possibleImports));
        long slutMakeMapOfOnlyUnique = System.currentTimeMillis();

        long startmakeListOfFilesToImport = System.currentTimeMillis();
        List<Mediafile> finalImports = fileMapper.makeListOfFilesToImport(uniquePossibleImports, mapOfReceivingLibrary);
        long slutmakeListOfFilesToImport = System.currentTimeMillis();



        logger().info(String.format("Size of receivingLibrary: %s", mapOfReceivingLibrary.size() ));
        logger().info(String.format("Size of possibleImports: %s", uniquePossibleImports.size() ));
        logger().info(String.format("Size of finalImports: %s", finalImports.size() ));
        logger().info(String.format("Time spent makeMapOfAll : %s", slutMakeMapOfAll - startMakeMapOfAll ));
        logger().info(String.format("Time spent MakeMapOfOnlyUnique : %s", slutMakeMapOfOnlyUnique - startMakeMapOfOnlyUnique ));
        logger().info(String.format("Time spent makeListOfFilesToImport : %s", slutmakeListOfFilesToImport - startmakeListOfFilesToImport ));



    }


}
