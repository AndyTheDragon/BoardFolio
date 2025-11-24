package dat.dao;

import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class CollectionDAO {

    private final EntityManagerFactory emf;

    public CollectionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public GameList getCollection(String username) {
        EntityManager em = emf.createEntityManager();

        try {
            UserAccount user = em.find(UserAccount.class, username);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + username);
            }

            GameList collection = user.getMyCollection();

            collection.getCustomList().size();

            return collection;

        } finally {
            em.close();
        }
    }

    public GameList addGame(String username, Long gameId) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            UserAccount user = em.find(UserAccount.class, username);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + username);
            }

            GameList collection = user.getMyCollection();

            Game game = em.find(Game.class, gameId);
            if (game == null) {
                throw new IllegalArgumentException("Game not found: " + gameId);
            }

            collection.addGame(game);

            em.merge(user);

            em.getTransaction().commit();

            collection.getCustomList().size();

            return collection;

        } finally {
            em.close();
        }
    }

    public GameList removeGame(String username, long gameId) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            UserAccount user = em.find(UserAccount.class, username);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + username);
            }

            GameList collection = user.getMyCollection();

            Game game = em.find(Game.class, gameId);
            if (game == null) {
                throw new IllegalArgumentException("Game not found: " + gameId);
            }

            collection.removeGame(game);

            em.merge(user);

            em.getTransaction().commit();

            collection.getCustomList().size();

            return collection;

        } finally {
            em.close();
        }
    }
}