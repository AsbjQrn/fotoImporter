package dk.asbjoern.foto.fotoimporter.fileutils;

import dk.asbjoern.foto.fotoimporter.beans.FileWrapper;
import dk.asbjoern.foto.fotoimporter.hashing.SlowAndGoodHashCalculator;
import dk.asbjoern.foto.fotoimporter.helpers.Loggable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public final class FindFilesToMerge implements Loggable {


    public List<FileWrapper> makeListOfImports(Path possibleImports, Path receivingLibrary) throws IOException {
        logger().info("Building mapOfLists_ReceivingLibrary");
        long startMakeMapOfAll = System.currentTimeMillis();
        Map<Integer, List<FileWrapper>> mapOfListsReceivingLibrary = makeMapOfAll(receivingLibrary);
        long slutMakeMapOfAll = System.currentTimeMillis();

        logger().info("Building mapOfLists_PossibleImports");
        long startMapOfAllPossibleImports = System.currentTimeMillis();
        Map<Integer, List<FileWrapper>> mapOfListsPossibleImports = makeMapOfAll(possibleImports);
        long slutMapOfAllPossibleImports = System.currentTimeMillis();

        logger().info("Cleaning cleanMapWithListsOfDuplicates  (imports)");
        long startCleanMapWithListsOfDuplicates = System.currentTimeMillis();
        List<FileWrapper> listOfPossibleImports = cleanMapWithListsOfDuplicates(mapOfListsPossibleImports);
        long slutCleanMapWithListsOfDuplicates = System.currentTimeMillis();

        logger().info("Building list of final imports  ");
        long startFindFilesToImport = System.currentTimeMillis();
        List<FileWrapper> listOfFilesToImport = findFilesToImport(listOfPossibleImports, mapOfListsReceivingLibrary);
        long slutFindFilesToImport = System.currentTimeMillis();

        logger().info(String.format("Size of receivingLibrary: %s", mapOfListsReceivingLibrary.size()));
        logger().info(String.format("Size of mapOfPossibleImports: %s", mapOfListsPossibleImports.size()));
        logger().info(String.format("Size of listOfPossibleImports: %s", listOfPossibleImports.size()));
        logger().info(String.format("Size of listOfFilesToImport: %s", listOfFilesToImport.size()));

        logger().info(String.format("Time spent makeMapOfAll : %s", slutMakeMapOfAll - startMakeMapOfAll));
        logger().info(String.format("Time spent mapOfAllPossibleImports : %s", slutMapOfAllPossibleImports - startMapOfAllPossibleImports));
        logger().info(String.format("Time spent cleanMapWithListsOfDuplicates : %s", slutCleanMapWithListsOfDuplicates - startCleanMapWithListsOfDuplicates));
        logger().info(String.format("Time spent findFilesToImport : %s", slutFindFilesToImport - startFindFilesToImport));
        logger().info(String.format("Number of hashsumcalculations: %s", SlowAndGoodHashCalculator.getChecksum_calculations()));

        return listOfFilesToImport;
    }

    /**
     * @param path
     * @return
     * @throws IOException Metoden itererer en mappesruktur og finder alle filer. Hver fil får beregnet en checksum baseret på de første x antal bytes (feks 1024).
     *                     Denne metode er meget upræcis og giver relativt mange kollisioner.
     *                     Men det går til gengæld meget hurtigt. Der kompenseres for kollisionerne senere i programmet.
     */
    private Map<Integer, List<FileWrapper>> makeMapOfAll(Path path) throws IOException {

        Map<Integer, List<FileWrapper>> fileWrappers = new HashMap<>();
        Files.walk(path)
                .forEach(p -> {
                    if (Files.isRegularFile(p)) {
                        FileWrapper fileWrapper = new FileWrapper(p);
                        List<FileWrapper> ensfiler = fileWrappers.get(fileWrapper.getFirstBytesChecksum());
                        if (ensfiler == null) {
                            ensfiler = new ArrayList<>();
                            fileWrappers.put(fileWrapper.getFirstBytesChecksum(), ensfiler);
                        }
                        ensfiler.add(fileWrapper);
                    }
                });

        return fileWrappers;
    }

    /**
     * @param listOfUniquePossibleImports
     * @param mapOfAllExisting
     * @return
     */
    private List<FileWrapper> findFilesToImport(List<FileWrapper> listOfUniquePossibleImports, Map<Integer, List<FileWrapper>> mapOfAllExisting) {

        List<FileWrapper> finalImports = new ArrayList<>();
        for (FileWrapper fileWrapperForPossibleImport : listOfUniquePossibleImports) {
            List<FileWrapper> listOfExistingPossibleDuplicates = mapOfAllExisting.get(fileWrapperForPossibleImport.getFirstBytesChecksum());
            if (listOfExistingPossibleDuplicates == null) {
                finalImports.add(fileWrapperForPossibleImport);
            } else {
                if (!getMapOfUniquesFromListWithDuplicates(listOfExistingPossibleDuplicates).containsKey(fileWrapperForPossibleImport.getChecksum())) {
                    finalImports.add(fileWrapperForPossibleImport);
                }
            }
        }
        return finalImports;
    }


    /**
     * @param mapWithListsOfDuplicates
     * @return Metoden renser en map med lister som kan indeholde duplikater. Der returneres en liste med unikke filer.
     */
    private List<FileWrapper> cleanMapWithListsOfDuplicates(Map<Integer, List<FileWrapper>> mapWithListsOfDuplicates) {

        List<FileWrapper> listOfUniques = new ArrayList<>();

        Collection<List<FileWrapper>> listOfListsWithMediafiles = mapWithListsOfDuplicates.values();
        for (List<FileWrapper> list : listOfListsWithMediafiles) {
            if (list.size() == 1) {
                listOfUniques.add(list.get(0));
                continue;
            }
            listOfUniques.addAll(getMapOfUniquesFromListWithDuplicates(list).values());
        }
        return listOfUniques;
    }

    private Map<String, FileWrapper> getMapOfUniquesFromListWithDuplicates(List<FileWrapper> fileWrappers) {
        Map<String, FileWrapper> mapOfUniques = new HashMap<>();

        fileWrappers.stream().forEach(fileWrapper -> mapOfUniques.put(fileWrapper.getChecksum(), fileWrapper));

        return mapOfUniques;
    }
}
