package learn.foraging.data;

import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ForagerFileRepositoryTest {

    static final String SEED_FILE_PATH = "./data/forage_data_test/forager-seed.txt";
    static final String TEST_FILE_PATH = "./data/forage_data_test/forager_data_test.csv";
    static final String TEST_DIR_PATH = "./data/forage_data_test/forager_data_test";

    ForagerFileRepository repository = new ForagerFileRepository(TEST_DIR_PATH);

    @BeforeEach
    void setup() throws IOException {
        Path seedPath = Paths.get(SEED_FILE_PATH);
        Path testPath = Paths.get(TEST_FILE_PATH);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void shouldFindAll() {
        ForagerFileRepository repo = new ForagerFileRepository("./data/foragers.csv");
        List<Forager> all = repo.findAll();
        assertEquals(1000, all.size());
    }

    @Test
    void shouldAdd() throws DataException {
        Forager forager = new Forager();
        forager.setId(String.valueOf(UUID.randomUUID()));
        forager.setFirstName("Mochi");
        forager.setLastName("Mazzone");
        forager.setState("DC");

        Forager actual = repository.add(forager);
        assertEquals("Mochi", actual.getFirstName());
    }

    @Test
    void shouldNotAddDuplicate() throws DataException {
        Forager forager = new Forager();
        forager.setId("11db7255-1d0e-41cc-b5f6-0d8bb2364a38");
        forager.setFirstName("Aymer");
        forager.setLastName("Rayer");
        forager.setState("ID");

        repository.add(forager);
        List<Forager> all = repository.findAll();
        assertEquals(1000, all.size());
    }

}