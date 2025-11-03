package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.GameController;
import dat.controllers.SecurityController;
import dat.dto.GameDTO;
import dat.routes.Routes;
import dat.services.BoardGameGeekService;
import dat.services.Populator;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.List;


public class Main {
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


    public static void main(String[] args) throws Exception {
        SecurityController securityController = new SecurityController(emf);
        Routes routes = new Routes(securityController);

        //TODO fjern senere, tester om data bliver hentet
//        List<GameDTO> gameDTOs = BoardGameGeekService.getBGGGamesFromFile();
//        List<GameDTO> gameDTOs = BoardGameGeekService.fetchAllGames();
//        gameDTOs.forEach(game -> System.out.println(game.toString()));

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7070);

        Populator.DevPopulator();
    }
}