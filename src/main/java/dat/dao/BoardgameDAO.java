package dat.dao;

import dat.entities.Game;
import dat.enums.Genre;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
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

    public List<Game> searchGame(String title, String category) throws DaoException
    {
        try (EntityManager em = emf.createEntityManager())
        {
            int limit = 10;

            String jpql = "SELECT g FROM Game g " +
                    "WHERE LOWER(g.title) LIKE :title " +
                    "AND (:category IS NULL OR :category MEMBER OF g.genres)";

            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("title", "%" + title.toLowerCase() + "%");

            if(category != null && !category.isEmpty()) {
                query.setParameter("category", Genre.valueOf(category.toUpperCase().replace(" ", "_")));
            } else {
                query.setParameter("category", null);
            }

            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            throw new DaoException("Error searching objects from db", e);
        }

    }
}
