package dat.dao;

import dat.entities.Game;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BoardgameDAO
{
    protected final EntityManagerFactory emf;
    private final Logger logger = LoggerFactory.getLogger(BoardgameDAO.class);

    public BoardgameDAO(EntityManagerFactory emf)
    {
        this.emf = emf;
    }

    public List<Game> getBoardgames(int page, int pageSize) throws DaoException
    {
        if (page < 0 || pageSize < 1)
        {
            logger.error(
                    "Page must be 0 or greater, and pageSize must be greater than 0. Received page: {}, pageSize: {}",
                    page, pageSize);
            throw new IllegalArgumentException("Page must be 0 or greater, and pageSize must be greater than 0");
        }
        int offset = page * pageSize;
        try (EntityManager em = emf.createEntityManager())
        {
            List<Game> entities = em.createQuery("SELECT g FROM Game g", Game.class)
                                    .setFirstResult(offset)
                                    .setMaxResults(pageSize)
                                    .getResultList();
            if (entities.isEmpty())
            {
                logger.debug("No entities found in db");
            }
            return entities;
        } catch (Exception e)
        {
            logger.error("Error reading objects from db", e);
            throw new DaoException("Error reading objects from db", e);
        }
    }
}
