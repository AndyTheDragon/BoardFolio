package dat.controllers;

import dat.dao.CollectionDAO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

public class CollectionController {

    private final CollectionDAO dao;

    public CollectionController(EntityManagerFactory emf) {
        this.dao = new CollectionDAO(emf);
    }

    public void getCollection(Context ctx) {
        String username = ctx.queryParam("username");

        if (username == null || username.isBlank()) {
            ctx.status(400).json("Username is required");
            return;
        }

        try {
            var collection = dao.getCollection(username);
            ctx.json(collection.getCustomList());
        } catch (IllegalArgumentException e) {
            ctx.status(404).json(e.getMessage());
        }
    }

    public void addToCollection(Context ctx) {
        String username = ctx.queryParam("username");
        String gameIdStr = ctx.queryParam("gameId");

        if (username == null || username.isBlank()) {
            ctx.status(400).json("Username is required");
            return;
        }

        if (gameIdStr == null) {
            ctx.status(400).json("gameId is required");
            return;
        }

        long gameId;
        try {
            gameId = Long.parseLong(gameIdStr);
        } catch (NumberFormatException e) {
            ctx.status(400).json("Invalid gameId format");
            return;
        }

        try {
            var collection = dao.addGame(username, gameId);
            ctx.json(collection.getCustomList());
        } catch (IllegalArgumentException e) {
            ctx.status(404).json(e.getMessage());
        }
    }

    public void removeFromCollection(Context ctx) {
        String username = ctx.queryParam("username");
        String gameIdStr = ctx.queryParam("gameId");

        if (username == null || username.isBlank()) {
            ctx.status(400).json("Username is required");
            return;
        }

        if (gameIdStr == null) {
            ctx.status(400).json("gameId is required");
            return;
        }

        long gameId;
        try {
            gameId = Long.parseLong(gameIdStr);
        } catch (NumberFormatException e) {
            ctx.status(400).json("Invalid gameId format");
            return;
        }

        try {
            var collection = dao.removeGame(username, gameId);
            ctx.json(collection.getCustomList());
        } catch (IllegalArgumentException e) {
            ctx.status(404).json(e.getMessage());
        }
    }
}
