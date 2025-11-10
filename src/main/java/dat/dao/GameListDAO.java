package dat.dao;

import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameListDAO
{
    protected final EntityManagerFactory emf;
    private final Logger logger = LoggerFactory.getLogger(GameListDAO.class);

    public GameListDAO(EntityManagerFactory emf)
    {
        this.emf = emf;
    }

    public List<GameList> getUserWithGameLists(String username)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            UserAccount user = em.find(UserAccount.class, username);

            if (user != null)
            {
                List<GameList> lists = new ArrayList<>(user.getGameLists());
                lists.forEach(gl -> gl.getCustomList().size());
                return lists;
            }

        } catch (Exception e)
        {
            logger.error("Error reading user from db", e);
            throw new DaoException("Error reading user from db", e);
        }
        return null;
    }

    public void deleteListFromUser(String username, String clName)
    {
        try (EntityManager em = emf.createEntityManager())
        {
            em.getTransaction().begin();

            int deleted = em.createQuery(
                                    "DELETE FROM GameList gl " +
                                    "WHERE gl.user.username = :username " +
                                    "AND gl.name = :listname"
                            )
                            .setParameter("username", username)
                            .setParameter("listname", clName)
                            .executeUpdate();

            em.getTransaction().commit();

            if (deleted == 0)
            {
                logger.warn("No game list found for user: {} with name: {}", username, clName);
            }

        } catch (Exception e)
        {
            throw new DaoException("Error deleting object from db", e);
        }
    }
}
