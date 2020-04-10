package dk.asbjoern.foto.fotoimporter.mapping;

import dk.asbjoern.foto.fotoimporter.beans.Mediafile;
import dk.asbjoern.foto.fotoimporter.hashing.SlowAndGoodHashCalculator;
import dk.asbjoern.foto.fotoimporter.helpers.Loggable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public final class FileMapper implements Loggable {


    /**
     * @param path
     * @return
     * @throws IOException Metoden itererer en mappesruktur og finder alle filer. Hver fil får beregnet en checksum baseret på de første x antal bytes (feks 1024).
     *                     Denne metode er meget upræcis og giver relativt mange kollisioner. Men det går til gengæld meget hurtigt. Der kompenseres for kollisionerne senere i programmet.
     */
    public static Map<Integer, List<Mediafile>> makeMapOfAll(Path path) throws IOException {

        Map<Integer, List<Mediafile>> mediefiler = new HashMap<>();

        Files.walk(path)
                .forEach(p -> {

                    File file = p.toFile();
                    if (file.isFile()) {
                        Mediafile mediafile = new Mediafile(file);
                        List<Mediafile> ensfiler = mediefiler.get(mediafile.getFirstBytesChecksum());
                        if (ensfiler == null) {
                            ensfiler = new ArrayList<>();
                            mediefiler.put(mediafile.getFirstBytesChecksum(), ensfiler);
                        }
                        ensfiler.add(mediafile);
                    }
                });

        return mediefiler;
    }


    /**
     * @param mapWithListOfDuplicates
     * @return
     * @throws IOException Metoden renser en map med collisions/duplikater vhja af SHA_256,
     *                     således at der ikke er collision/duplikater ifølge SHA_256
     */
    public static Map<String, Mediafile> makeMapOfOnlyUnique(Map<Integer, List<Mediafile>> mapWithListOfDuplicates) throws IOException {

        Map<String, Mediafile> mapOfOnlyUnique = new HashMap<>();

        Collection<List<Mediafile>> listsOfMediafiles = mapWithListOfDuplicates.values();

        for (List<Mediafile> list : listsOfMediafiles) {
            for (Mediafile mediafile : list) {
                mapOfOnlyUnique.put(SlowAndGoodHashCalculator.checksum(mediafile.getFile()), mediafile);
            }
        }
        return mapOfOnlyUnique;
    }

    /**
     * @param mapOfPossibleImports
     * @param mapOfExistingMedia
     * @return Metoden tager en map med filer der skal importeres, samt en map over existing media. Import mappen er renset for duplikater, det er mapExistingMedia ikke -
     * men her findes filer til import - som ikke er duplikater af gamle/existing filer.
     */
    public static List<Mediafile> makeListOfFilesToImport(Map<String, Mediafile> mapOfPossibleImports, Map<Integer, List<Mediafile>> mapOfExistingMedia) {

        ArrayList<Mediafile> filesToImport = new ArrayList<Mediafile>();

        for (Map.Entry<String, Mediafile> entry : mapOfPossibleImports.entrySet()) {
            Mediafile mediaFileForPossibleImport = entry.getValue();
            if (checkIfFileExists(entry.getKey(), mapOfExistingMedia.get(mediaFileForPossibleImport.getFirstBytesChecksum()))) {
                continue;
            } else {
                filesToImport.add(mediaFileForPossibleImport);
            }
        }
        return filesToImport;
    }

    private static boolean checkIfFileExists(String mediaFileForPossibleImport_SHA_256, List<Mediafile> listOfPossibleExistingDuplicates) {

        if (listOfPossibleExistingDuplicates == null) {
            return false;
        }

        return listOfPossibleExistingDuplicates.stream().anyMatch(m -> SlowAndGoodHashCalculator.checksum(m.getFile()).equals(mediaFileForPossibleImport_SHA_256));

    }
}
