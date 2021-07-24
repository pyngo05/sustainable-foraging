package learn.foraging.data;

import learn.foraging.models.Forager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ForagerFileRepositoryTest {

    static final String SEED_FILE_PATH = "./data/forager_data_test/forager-seed.txt";
    static final String TEST_FILE_PATH = "./data/forager_data_test/forager_data_test.csv";
    static final String TEST_DIR_PATH = "./data/forager_data_test/forager_data_test";

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
        assertEquals(1001, all.size());
    }

    @Test
    void shouldFindByExistingId() {
        Forager forager = repository.findById("e0ec606d-09e5-4e97-b077-43475cca1638");
        assertNotNull(forager);
        assertEquals("Shirlene", forager.getFirstName());
    }

    @Test
    void shouldNotFindByMissingId() {
        Forager forager = repository.findById("1000");
        assertNull(forager);
    }

    @Test
    void shouldFindByExisitngState() {
        List<Forager> forager = repository.findByState("DC");
        assertNotNull(forager);
    }

    @Test
    void shouldAdd() throws DataException {
        Forager forager = new Forager();
        forager.setId(String.valueOf(UUID.randomUUID()));
        forager.setFirstName("Mochi");
        forager.setLastName("Coco");
        forager.setState("DC");

        Forager actual = repository.add(forager);
        assertEquals("Mochi", actual.getFirstName());
    }
}