package dat;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.controllers.SecurityController;
import dat.dto.GameDTO;
import dat.routes.Routes;
import dat.service.BoardGameGeekService;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;


public class Main
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


    public static void main(String[] args)
    {
        SecurityController securityController = new SecurityController(emf);
        Routes routes = new Routes(securityController);

        //TODO fjern senere, tester om data bliver hentet
        List<GameDTO> gameDTOS = BoardGameGeekService.getBGGGames();
        System.out.println(gameDTOS);

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