package dk.asbjoern.foto.fotoimporter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class Application implements CommandLineRunner {

    Runner runner;

    public Application(Runner runner) {
        this.runner = runner;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }



    @Override
    public void run(String... args) throws Exception {


//        Path receivingLibrary = Paths.get("/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/fotoOrganiserOutput2/");
//        Path possibleImports = Paths.get("/media/asbjoern/a78b1484-dfe0-47b8-9e7e-0214b1caad70/possibleImports");

        this.runner.run();

    }
}