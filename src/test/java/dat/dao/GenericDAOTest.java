package dat.dao;

import dat.entities.Game;
import dat.exceptions.DaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GenericDAOTest extends DAOTestBase
{
    private GenericDAO dao;

    @BeforeEach
    void setUp()
    {
        dao = new GenericDAO(emf);
    }

    private Game makeGame(String title)
    {
        Game g = new Game();
        g.setTitle(title);
        g.setDescription("test desc");
        g.setMinAge(8);
        g.setMinNoOfPlayers(2);
        g.setMaxNoOfPlayers(4);
        g.setReleaseYear(2020);
        // optional:
        // g.setImageURL("http://example.com/img.jpg");
        // g.setThumbnailURL("http://example.com/thumb.jpg");
        return g;
    }


    @Test
    void create_shouldAssignId() throws DaoException
    {
        Game created = dao.create(makeGame("CreateTest"));
        assertNotNull(created);
        assertNotNull(created.getGameId());
        assertEquals("CreateTest", created.getTitle());
    }

    @Test
    void read_shouldReturnPersistedEntity() throws DaoException
    {
        Game created = dao.create(makeGame("ReadTest"));
        Game found = dao.getById(Game.class, created.getGameId());

        assertNotNull(found);
        assertEquals(created.getGameId(), found.getGameId());
        assertEquals("ReadTest", found.getTitle());
    }

    @Test
    void readAll_shouldContainSeedData() throws DaoException
    {
        List<Game> games = dao.getAll(Game.class);

        assertThat(games, hasSize(greaterThanOrEqualTo(1)));
        assertThat(games, hasItem(hasProperty("title", is("Catan"))));
    }

    @Test
    void update_shouldPersistChanges() throws DaoException
    {
        Game created = dao.create(makeGame("BeforeUpdate"));
        created.setTitle("AfterUpdate");

        Game updated = dao.update(created);

        assertNotNull(updated);
        assertEquals(created.getGameId(), updated.getGameId());
        assertEquals("AfterUpdate", updated.getTitle());
    }

    @Test
    void deleteById_shouldRemoveRow() throws DaoException
    {
        Game created = dao.create(makeGame("DeleteMe"));
        Long id = created.getGameId();

        dao.delete(Game.class, id);

        assertThrows(DaoException.class, () -> dao.getById(Game.class, id));
    }

    @Test
    void deleteByEntity_shouldRemoveRow() throws DaoException
    {
        Game created = dao.create(makeGame("DeleteEntity"));
        dao.delete(created);

        assertThrows(DaoException.class, () -> dao.getById(Game.class, created.getGameId()));
    }

    @Test
    void create_batch_shouldPersistAllOrThrow() throws DaoException
    {
        List<Game> batch = new ArrayList<>();
        batch.add(makeGame("Batch-1"));
        batch.add(makeGame("Batch-2"));

        List<Game> created = dao.create(batch);

        assertThat(created, hasSize(2));
        assertThat(created, everyItem(hasProperty("gameId", notNullValue())));
    }

    @Test
    void update_batch_shouldPersistAll() throws DaoException
    {
        List<Game> batch = new ArrayList<>();
        batch.add(dao.create(makeGame("BatchUpdate-1")));
        batch.add(dao.create(makeGame("BatchUpdate-2")));

        batch.get(0).setTitle("BatchUpdate-1-updated");
        batch.get(1).setTitle("BatchUpdate-2-updated");

        List<Game> updated = dao.update(batch);

        assertThat(updated, hasSize(2));
        assertThat(updated, hasItem(hasProperty("title", is("BatchUpdate-1-updated"))));
        assertThat(updated, hasItem(hasProperty("title", is("BatchUpdate-2-updated"))));
    }

    @Test
    void getById_missing_shouldThrowDaoException()
    {
        assertThrows(DaoException.class, () -> dao.getById(Game.class, 9_999_999L));
    }
}
