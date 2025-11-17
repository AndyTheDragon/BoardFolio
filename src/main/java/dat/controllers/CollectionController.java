package dat.controllers;

import dat.dto.GameDTO;
import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class CollectionController {

    private final EntityManagerFactory emf;

    public CollectionController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void addToCollection(Context ctx) {
        String username = ctx.queryParam("username");
        GameDTO dto = ctx.bodyAsClass(GameDTO.class);

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            UserAccount user = em.find(UserAccount.class, username);
            if (user == null) {
                throw new NotFoundResponse("User not found: " + username);
            }

            GameList collection = user.getMyCollection();

            Game game = dto.toEntity(dto);

            Game existingGame = em.createQuery(
                            "SELECT g FROM Game g WHERE g.gameId = :apiId", Game.class)
                    .setParameter("apiId", dto.getBGG_API_ID())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            Game persistentGame;
            if (existingGame != null) {
                persistentGame = existingGame;
            } else {
                em.persist(game);
                persistentGame = game;
            }

            collection.addGame(persistentGame);

            em.merge(user);

            em.getTransaction().commit();
            ctx.json(collection);

        } finally {
            em.close();
        }
    }

    public void removeFromCollection(Context ctx) {
        String username = ctx.queryParam("username");
        long gameId = Long.parseLong(ctx.queryParam("gameId"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            UserAccount user = em.find(UserAccount.class, username);
            if (user == null) {
                throw new NotFoundResponse("User not found");
            }

            GameList collection = user.getMyCollection();

            Game game = em.find(Game.class, gameId);
            if (game == null) {
                throw new NotFoundResponse("Game not found");
            }

            collection.removeGame(game);

            em.merge(user);

            em.getTransaction().commit();
            ctx.json(collection);

        } finally {
            em.close();
        }
    }
}
