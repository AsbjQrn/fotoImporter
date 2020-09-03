import dk.asbjoern.foto.fotoimporter.beans.FileWrapper
import dk.asbjoern.foto.fotoimporter.fileutils.FindFilesToMerge
import spock.lang.Specification

import java.nio.file.Path

class ApplicationSpec extends Specification {

    def "Test at de rigtige billeder bliver importeret mellem to mappestruktuer fulde af rod."() {

        FindFilesToMerge fileMapper = new FindFilesToMerge();

        when:
        List<FileWrapper> listOfFilesToImport = fileMapper.makeListOfImports(Path.of('files/imports'), Path.of('files/receivingLibrary'))
        List<FileWrapper> expectedImports = Arrays.asList(new FileWrapper(
                new File('files/imports/import2.JPG'))
                , new FileWrapper(new File('files/imports/import3.JPG'))
                , new FileWrapper(new File('files/imports/mappe1/import1.jpg')))

        then:
        listOfFilesToImport.size() == 3
        listOfFilesToImport.containsAll(expectedImports)


    }
}
