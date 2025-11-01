package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.SecurityController;
import dat.routes.Routes;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;


public class Main
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


    public static void main(String[] args) throws IOException
    {
        SecurityController securityController = new SecurityController(emf);
        Routes routes = new Routes(securityController);

        //TODO fjern senere, tester om data bliver hentet
//        List<GameDTO> gameDTOS = BoardGameGeekService.getBGGGamesFromFile();
//        List<GameDTO> gameDTOS = BoardGameGeekService.getBGGGames();
//        gameDTOS.forEach(game -> System.out.println(game.toString()));

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(routes.getRoutes())
                .handleException()
                .setApiExceptionHandling()
                .checkSecurityRoles()
                .startServer(7070);
    }
}