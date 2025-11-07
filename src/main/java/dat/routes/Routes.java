package dat.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.controllers.AdminController;
import dat.controllers.GameController;
import dat.controllers.SecurityController;
import dat.enums.Roles;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;


import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes
{
    private SecurityController securityController;
    private final GameController gameController;
    private final AdminController adminController;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public Routes(EntityManagerFactory emf)
    {
        this.securityController = new SecurityController(emf);
        this.gameController = new GameController(emf);
        this.adminController = new AdminController(emf);
    }

    public EndpointGroup getRoutes()
    {
        return () -> {
            path("trips", tripRoutes());
            path("auth", authRoutes());
            path("populate", populateRoutes());
        };
    }

    private EndpointGroup tripRoutes()
    {
        return () -> {
            /*get(tripController::getAllTrips);
            get("/{id}", tripController::getTripById);
            post(tripController::createTrip);
            put("/{id}", tripController::updateTrip);
            delete("/{id}", tripController::deleteTrip);
            put("/{tripId}/guides/{guideId}", tripController::addGuideToTrip);
            post("/populate", tripController::populate);*/
        };
    }

    private EndpointGroup authRoutes()
    {
        return () -> {
            get("/test", ctx -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from Open")), Roles.ANYONE);
            get("/healthcheck", securityController::healthCheck, Roles.ANYONE);
            post("/login", securityController::login, Roles.ANYONE);
            post("/register", securityController::register, Roles.ANYONE);
            get("/verify", securityController::verify, Roles.ANYONE);
            get("/tokenlifespan", securityController::timeToLive, Roles.ANYONE);
        };
    }

    private EndpointGroup populateRoutes()
    {
        return () -> {
            post("games", adminController::populateDatabaseGames, Roles.ADMIN);
            post("games/dev", adminController::populateDevDatabaseGames, Roles.ADMIN);
//            put("games/sync", adminController::updateDatabaseGames, Role.ADMIN);
        };
    }

}
