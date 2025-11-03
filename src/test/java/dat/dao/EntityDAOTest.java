package dat.dao;

import dat.config.HibernateConfig;
import dat.entities.Game;
import dat.enums.Genre;
import dat.enums.Languages;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EntityDAOTest {

    private static EntityManagerFactory emf;
    private GenericDAO dao;  // Vi bruger den generiske DAO til databaseoperationer

    @BeforeAll
    static void setupClass() {
        // Initialisér EntityManagerFactory til test (bruger testdatabase via HibernateConfig)
        emf = HibernateConfig.getEntityManagerFactoryForTest();
    }

    @BeforeEach
    void setupTest() {
        // Opret en ny DAO-instans før hver test (med delt EntityManagerFactory)
        dao = new GenericDAO(emf);
    }

    @AfterAll
    static void tearDownClass() {
        // Frigiv ressourcer efter alle tests (valgfrit luk EMF hvis nødvendigt)
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void testCreateReadUpdateDeleteGame() throws DaoException {
        // **Create**: Opret et nyt Game-objekt og persistér det via DAO
        Game newGame = new Game(
                "TestSpil",
                "Beskrivelse af testspil",
                1, 4,
                10, 18,
                2021,
                List.of(Languages.ENGLISH),
                Set.of(Genre.FANTASY)
        );
        Game createdGame = dao.create(newGame);  // gemmer spillet i databasen
        assertNotNull(createdGame.getGameId(), "Game ID burde være sat efter create");

        // **Read**: Hent spillet fra databasen ved ID og verificér felter
        Game foundGame = dao.getById(Game.class, createdGame.getGameId());
        assertEquals("TestSpil", foundGame.getTitle(), "Titlen på det fundne spil skal matche det oprettede");
        assertEquals(2021, foundGame.getReleaseYear(), "Udgivelsesåret skal matche det oprettede");

        // **Update**: Opdater en værdi på spillet og gem ændringen
        foundGame.setTitle("OpdateretTitel");
        Game updatedGame = dao.update(foundGame);
        assertEquals("OpdateretTitel", updatedGame.getTitle(), "Titlen skal være opdateret i databasen");

        // **Delete**: Slet spillet og forsøg at læse det igen for at sikre det er væk
        dao.delete(Game.class, updatedGame.getGameId());
        // Efter sletning forventer vi, at et opslag nu kaster en undtagelse (DaoException/EntityNotFoundException)
        assertThrows(DaoException.class, () -> {
            dao.getById(Game.class, updatedGame.getGameId());
        }, "Forventet DaoException når man forsøger at finde slettet spil");
    }
}