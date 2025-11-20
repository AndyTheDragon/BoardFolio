package dat.controllers;

import dat.dao.GenericDAO;
import dat.dto.GameDTO;
import dat.entities.Game;
import dat.services.BoardGameGeekService;
import dat.services.Populator;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class AdminController
{
    private final GenericDAO genericDAO;


    public AdminController(EntityManagerFactory emf)
    {
        this.genericDAO = new GenericDAO(emf);
    }

    public void populateDatabaseGames(Context context)
    {
        try {
            List<GameDTO> gameDTOList = BoardGameGeekService.fetchAllGames();

            for (GameDTO gameDTO : gameDTOList)
            {
                Game game = gameDTO.toEntity(gameDTO);
                genericDAO.create(game);
            }
            context.status(200).result("Database populated with " + gameDTOList.size() + " games.");
        } catch (Exception e) {
            context.status(500).result("Failed to populate database: " + e.getMessage());
        }
    }

    public void populateDevDatabaseGames(Context context)
    {
        Populator.DevPopulator();
        context.status(201);
    }
}
