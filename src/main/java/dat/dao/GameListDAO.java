package dat.dao;

import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import dat.exceptions.DaoException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameListDAO {

    private final Logger logger = LoggerFactory.getLogger(GameListDAO.class);
    protected final EntityManagerFactory emf;

    public GameListDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void addGameToCollection(String username, Long gameId) throws DaoException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Find the user and the game
            UserAccount user = em.find(UserAccount.class, username);
            Game game = em.find(Game.class, gameId);

            if (user == null || game == null) {
                throw new DaoException("User or Game not found");
            }

            // Ensure the collection exists
            GameList collection = user.getMyCollection();
            if (collection == null) {
                collection = new GameList("My Collection");
                collection.setUser(user);
                user.setMyCollection(collection);
                em.persist(collection);
            }

            // Add game to the list
            collection.addGame(game);
            em.merge(collection);
            em.merge(user);

            em.getTransaction().commit();
            logger.info("Added game '{}' to {}'s collection", game.getTitle(), user.getUsername());
        } catch (Exception e) {
            logger.error("Error adding game to collection", e);
            throw new DaoException("Error adding game to collection", e);
        }
    }
//    /**
//     * Remove a game from a user's main collection
//     */
//    public void removeGameFromCollection(String username, Long gameId) throws DaoException {
//        try (EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//
//            UserAccount user = em.find(UserAccount.class, username);
//            Game game = em.find(Game.class, gameId);
//
//            if (user == null || game == null) {
//                throw new DaoException("User or Game not found");
//            }
//
//            GameList collection = user.getMyCollection();
//            if (collection == null) {
//                throw new DaoException("User has no collection");
//            }
//
//            collection.removeGame(game);
//            em.merge(collection);
//            em.getTransaction().commit();
//
//            logger.info("Removed game '{}' from {}'s collection", game.getTitle(), user.getUsername());
//        } catch (Exception e) {
//            logger.error("Error removing game from collection", e);
//            throw new DaoException("Error removing game from collection", e);
//        }
//    }
//
//    /**
//     * View all games in a user's main collection
//     */
//    public List<Game> getAllGamesInCollection(String username) throws DaoException {
//        try (EntityManager em = emf.createEntityManager()) {
//            TypedQuery<Game> query = em.createQuery(
//                    "SELECT g FROM UserAccount u JOIN u.myCollection c JOIN c.customList g WHERE u.username = :username",
//                    Game.class);
//            query.setParameter("username", username);
//            return query.getResultList();
//        } catch (NoResultException e) {
//            return List.of();
//        } catch (Exception e) {
//            logger.error("Error retrieving games from collection", e);
//            throw new DaoException("Error retrieving games from collection", e);
//        }
//    }

}
