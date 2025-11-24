package dat.controllers;

import dat.dao.BoardgameDAO;
import dat.dao.GameListDAO;
import dat.dao.GenericDAO;
import dat.dto.ErrorMessage;
import dat.dto.GameListDTO;
import dat.entities.Game;
import dat.entities.GameList;
import dat.enums.Genre;
import dat.exceptions.DaoException;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dat.enums.Genre.STRATEGY;

public class GameController
{

    private final GenericDAO genericDAO;
    private final BoardgameDAO boardgameDAO;
    private final GameListDAO gameListDAO;
    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(EntityManagerFactory emf)
    {
        this.genericDAO = new GenericDAO(emf);
        this.boardgameDAO = new BoardgameDAO(emf);
        this.gameListDAO = new GameListDAO(emf);
    }

    public void populateBoardGames(@NotNull Context context)
    {
        try
        {
            Set<Genre> genres = new HashSet<>();
            genres.add(STRATEGY);

            Game game = Game.builder()
                    .title("Catan")
                    .description("Trade, build, and settle the island of Catan in this classic board game.")
                    .minNoOfPlayers(3)
                    .maxNoOfPlayers(4)
                    .releaseYear(1995)
                    .genres(genres)
                    .build();

            Game saved = genericDAO.create(game);
            context.status(200).json(saved);
        } catch (DaoException daoException)
        {
            logger.error(daoException.getMessage());
            context.status(400).result(daoException.getMessage());
        }
    }

    public void getAllBoardGames(@NotNull Context ctx)
    {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int pageSize = ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(25);
        try
        {
            List<Game> boardGameList = boardgameDAO.getBoardgames(page, pageSize);
            ctx.status(200).json(boardGameList);
        } catch (DaoException | IllegalArgumentException exception)
        {
            logger.error(exception.getMessage());
            ctx.status(400).result(exception.getMessage());
        }
    }

    public void getGameListsForUser(@NotNull Context ctx)
    {
        String username = ctx.pathParam("username");

        List<GameList> gameLists = gameListDAO.getUserWithGameLists(username);

        if (gameLists == null) {
            gameLists = java.util.Collections.emptyList();
        }

        ctx.status(200).json(gameLists);
    }

    public void deleteUserList(@NotNull Context ctx)
    {
        int listID = Integer.parseInt(ctx.pathParam("listID"));
        gameListDAO.deleteListFromUser(listID);
    }

    public void createGameList(@NotNull Context ctx)
    {
        GameListDTO gameListDTO = ctx.bodyAsClass(GameListDTO.class);
        // sets a timestamp for when list was created
        gameListDTO.setCreatedDate(LocalDateTime.now());

        GameList gameLists = gameListDTO.toEntity(gameListDTO);

        genericDAO.create(gameLists);
    }

    public void updateList(@NotNull Context ctx)
    {
        int listID = Integer.parseInt(ctx.pathParam("listID"));

        GameList databaseGameList;
        try {
            databaseGameList = genericDAO.getById(GameList.class, listID);
        } catch (Exception e) {
            ctx.status(404).json(new ErrorMessage("Game list not found"));
            return;
        }

        if (databaseGameList == null)
        {
            ctx.status(404).json(new ErrorMessage("Game list not found"));
            return;
        }

        GameListDTO gameListDTO = ctx.bodyAsClass(GameListDTO.class);
        GameList gameListToUpdate = gameListDTO.toEntity(gameListDTO);

        databaseGameList.setName(gameListToUpdate.getName());
        databaseGameList.setCustomList(gameListToUpdate.getCustomList());
        databaseGameList.setPublic(gameListToUpdate.isPublic());

        genericDAO.update(databaseGameList);
        ctx.status(200).json("{\"message\":\"Game list updated\"}");
    }

    public void getGameListById(@NotNull Context ctx)
    {
        int listID = Integer.parseInt(ctx.pathParam("listID"));

        GameList gl;
        try {
            gl = genericDAO.getById(GameList.class, listID);
        } catch (Exception e) {
            ctx.status(404).json(new ErrorMessage("Game list not found"));
            return;
        }

        if (gl == null) {
            ctx.status(404).json(new ErrorMessage("Game list not found"));
            return;
        }

        // 🔹 Tving lazy collection til at loade, mens sessionen stadig er åben
        gl.getCustomList().size();

        // 🔹 Brug din nuværende toDTO-signatur (med 1 parameter)
        GameListDTO dto = gl.toDTO(gl);

        ctx.status(200).json(dto);
    }

}
