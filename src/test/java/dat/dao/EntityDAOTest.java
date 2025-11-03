package dat.dao;

import dat.config.HibernateConfig;
import dat.entities.Game;
import dat.enums.Genre;
import dat.exceptions.DaoException;
import dat.services.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityDAOTest
{

    private static EntityManagerFactory emf;
    private GenericDAO dao;

    private final Set<Genre> genres = EnumSet.of(Genre.RACING, Genre.CIVILIZATION);

    // --- Setup & Teardown ---

    @BeforeAll
    void setupClass()
    {
        // Use your test DB
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // 🔹 Call your existing TestPopulator to seed initial data
        System.out.println(">>> Calling TestPopulator.populate() before DAO tests...");
        TestPopulator.populate();
        System.out.println(">>> TestPopulator finished seeding data.");
    }

    @BeforeEach
    void setupTest()
    {
        dao = new GenericDAO(emf);
    }

    @AfterAll
    void tearDownClass()
    {
        if (emf != null)
        {
            emf.close();
        }
    }

    // --- Helper methods ---

    private void clearTable(String entityName)
    {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from " + entityName).executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    private Game newGame(String title)
    {
        return new Game(
                "ReadTest",
                "Klassisk økonomi/handel brætspil for hele familien",
                2,
                4,
                6,
                1935,               // udgivelsesår for Matador/Monopoly
                "https://example.com/catan.jpg",        // imageURL
                "https://example.com/catan-thumb.jpg",
                Collections.emptySet(),
                genres);
    }

    // --- DAO Tests ---

    @Test
    void create_shouldAssignId() throws DaoException
    {
        Game created = dao.create(newGame("CreateTest"));
        assertNotNull(created.getGameId(), "Game ID should be set after create");
    }

    @Test
    void read_shouldReturnPersistedEntity() throws DaoException
    {
        Game created = dao.create(newGame("ReadTest"));
        Game found = dao.getById(Game.class, created.getGameId());
        assertEquals("ReadTest", found.getTitle());
    }

    @Test
    void update_shouldPersistChanges() throws DaoException
    {
        Game created = dao.create(newGame("BeforeUpdate"));
        created.setTitle("AfterUpdate");
        Game updated = dao.update(created);
        assertEquals("AfterUpdate", updated.getTitle());
    }

    @Test
    void delete_shouldRemoveRow() throws DaoException
    {
        Game created = dao.create(newGame("DeleteMe"));
        dao.delete(Game.class, created.getGameId());
        assertThrows(DaoException.class, () ->
                             dao.getById(Game.class, created.getGameId()),
                     "Expected DaoException when reading deleted game");
    }
}
