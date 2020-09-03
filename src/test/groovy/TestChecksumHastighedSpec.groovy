import dk.asbjoern.foto.fotoimporter.beans.FileWrapper
import dk.asbjoern.foto.fotoimporter.commandexecution.CommandExecuter
import dk.asbjoern.foto.fotoimporter.hashing.SlowAndGoodHashCalculator
import dk.asbjoern.foto.fotoimporter.commandexecution.LinuxCommandExecuter
import dk.asbjoern.foto.fotoimporter.hashing.FastAndBadHashCalculator
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

class TestChecksumHastighedSpec extends Specification {

    @Shared
    CommandExecuter commandExecuter = new LinuxCommandExecuter()

    @Shared
    SlowAndGoodHashCalculator hashCalculator = new SlowAndGoodHashCalculator();

    def "Test kendt mod ukendt metode til udregning af checksum"() {

        when:
        File file = new File("files/DSC_01401.JPG")
        String someValue = commandExecuter.executeCommand(['md5sum', file.absolutePath]);
        String someOthervalue = hashCalculator.checksum(file);

        then:
        (someValue.split(' ')).getAt(0) == someOthervalue
        '33a4312eb59756e21e43a5665bd4de28' == someOthervalue
    }

    def "Hastighedstest rå linux mod java"() {

        when:
        long startLinux = System.currentTimeMillis()
        Files.walk(Paths.get('files')).filter({ f -> f.toFile().isFile() }).forEach({ f -> commandExecuter.executeCommand(['md5sum', f.toAbsolutePath().toString()]) })
        long slutLinux = System.currentTimeMillis()

        long startJava = System.currentTimeMillis()
        Files.walk(Paths.get('files')).filter({ f -> f.toFile().isFile() }).forEach({ f -> hashCalculator.checksum(f.toFile()) })
        long slutJava = System.currentTimeMillis()

        long timeJava = slutJava - startJava
        long timeLinux = slutLinux - startLinux

        println 'timeJava: ' + timeJava
        println 'timeLinux: ' + timeLinux
        println String.format('java kører %s procent langsommere en linux: ', (timeJava / timeLinux - 1) * 100)

        then:
        timeLinux < timeJava
    }


    def "hastighedstest største maaned 2019"() {
        Path.of('/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/2019/JULY');

        when:
        Long start = System.currentTimeMillis()
        Map<String, String> map =
                Files.walk(Paths.get('/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/2019'))
                        .filter({ path -> path.toFile().isFile() })
                        .collect(Collectors.toMap({ Path f -> commandExecuter.executeCommand(['md5sum', f.toAbsolutePath().toString()]) }, { f -> f.toAbsolutePath().toString() }))

        Long slut = System.currentTimeMillis()

        then:
        map.size() > 0
        println String.format('Antal filer i map: %s ', map.size())
        println String.format('Tidsforbrug i sekunder: %f', (slut - start) / 1000)
    }


    def "Hvor mange filer ialt"() {
        when:
        Long antalFiler = 0
        Long antalMapper = 0


        antalFiler = Files.walk(Paths.get('/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/'))
                .filter({ path -> path.toFile().isFile() }).count()

        antalMapper = Files.walk(Paths.get('/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/'))
                .filter({ path -> path.toFile().isDirectory() }).count()

        then:
        true
        println String.format('antalMapper: %s ', antalMapper)
        println String.format('antalFiler: %s ', antalFiler)
        println String.format('antalFiler og mapper: %s ', antalFiler + antalMapper)

    }

    def "hastighedstest speedreader alle filer bytearray"() {
        when:
        Long startSpeed = System.currentTimeMillis()

        Map<Long, String> mapSpeed =
                Files.walk(Paths.get('/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/'))
                        .filter({ path -> path.toFile().isFile() })
                        .collect(Collectors.toMap({ Path path -> FastAndBadHashCalculator.firstBytes(path.toFile()) }, { f -> f.toAbsolutePath().toString() }))

        Long slutSpeed = System.currentTimeMillis()

        then:
        mapSpeed.size() > 0
        println String.format('Antal filer i map: %s ', mapSpeed.size())
        println String.format('Tidsforbrug i sekunder: %f', (slutSpeed - startSpeed) / 1000)

    }

    def "hastighedstest speedreader alle filer integer"() {
        when:
        Long startSpeed = System.currentTimeMillis()

        Set<Integer> mapSpeed =
                Files.walk(Paths.get('/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/'))
                        .filter({ path -> path.toFile().isFile() }).map({ path -> return FastAndBadHashCalculator.firstBytesAsInteger(path.toFile()) })
                        .collect(Collectors.toSet())

        Long slutSpeed = System.currentTimeMillis()

        then:
        mapSpeed.size() > 0
        println String.format('Antal filer i map: %s ', mapSpeed.size())
        println String.format('Tidsforbrug i sekunder: %f', (slutSpeed - startSpeed) / 1000)

    }

    def "hastighedstest speedreader alle filer map med lists"() {
        when:
        Long startSpeed = System.currentTimeMillis()

        Map<Integer, List<FileWrapper>> mediefiler = new HashMap<>()

        int count = 0 //95348
        Files.walk(Paths.get('/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput/'))
                .forEach({ path ->

                    File file = path.toFile();
                    if (file.isFile()) {
                        FileWrapper mediafile = new FileWrapper(file)
                        List<FileWrapper> ensfiler = mediefiler.get(mediafile.getFirstBytesChecksum())
                        if (ensfiler == null) {
                            ensfiler = new ArrayList<>()
                            mediefiler.put(mediafile.getFirstBytesChecksum(), ensfiler)
                        }
                        ensfiler.add(mediafile)
                    }
                })

        Long slutSpeed = System.currentTimeMillis()


        then:
        true
        println String.format('Tidsforbrug i sekunder: %f', (slutSpeed - startSpeed) / 1000)
        println mediefiler.size()
        Long imagesAllInAll = 0
        for (Collection<List<FileWrapper>> list : mediefiler.values()){
            imagesAllInAll = imagesAllInAll + list.size()
        }
        println String.format('filer ialt: %s', imagesAllInAll )

    }
}