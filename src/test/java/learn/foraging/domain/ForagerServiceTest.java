package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForagerRepositoryDouble;
import learn.foraging.models.Forager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ForagerServiceTest {

    ForagerService service = new ForagerService(new ForagerRepositoryDouble());

    @Test
    void shouldFindByExistingState() throws DataException {
        List<Forager> foragers = service.findByState("GA");
        assertEquals(1, foragers.size());
    }

    @Test
    void shouldNotFindByMissingState() {
        List<Forager> foragers = service.findByState("ABCD");
        assertEquals(0, foragers.size());
    }

    @Test
    void shouldFindByExistingLastName() {
        List<Forager> foragers = service.findByLastName("Sisse");
        assertEquals(1, foragers.size());
    }

    @Test
    void shouldNotFindByMissingLastName() {
        List<Forager> foragers = service.findByLastName("Sissie");
        assertEquals(0, foragers.size());
    }

    @Test
    void shouldAddNew() throws DataException {
        Forager forager = new Forager();
        forager.setId(String.valueOf((UUID.randomUUID())));
        forager.setFirstName("Mochi");
        forager.setLastName("Coco");
        forager.setState("DC");

        Result<Forager> result = service.add(forager);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotAddExisting() throws DataException {
        Forager forager = new Forager();
        forager.setId("0e4707f4-407e-4ec9-9665-baca0aabe88c");
        forager.setFirstName("Jilly");
        forager.setLastName("Sisse");
        forager.setState("GA");

        Result<Forager> result = service.add(forager);
        assertFalse(result.isSuccess());
    }
}