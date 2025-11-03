package dat.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.config.HibernateConfig;
import dat.controllers.GameController;
import dat.controllers.SecurityController;
import dat.enums.Roles;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import javax.management.relation.Role;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {
    private final SecurityController securityController;
    private final GameController gameController;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public Routes(SecurityController securityController) {

        this.securityController = securityController;
        this.gameController = new GameController(emf);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            path("trips", tripRoutes());
            path("auth", authRoutes());
            path("populate", populateRoutes());
        };
    }

    private EndpointGroup tripRoutes() {
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

    private EndpointGroup authRoutes() {
        return () -> {
            get("/test", ctx -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from Open")), Roles.ANYONE);
            get("/healthcheck", securityController::healthCheck, Roles.ANYONE);
            post("/login", securityController::login, Roles.ANYONE);
            post("/register", securityController::register, Roles.ANYONE);
            get("/verify", securityController::verify, Roles.ANYONE);
            get("/tokenlifespan", securityController::timeToLive, Roles.ANYONE);
        };
    }

    private EndpointGroup populateRoutes() {
        return () -> { // TODO what controller?
//            put("roles", adminController::populateDatabaseRoles,Role.ADMIN); //TODO better solution?
//            put("games", adminController::populateDatabaseGames, Role.ADMIN);
//            put("games/sync", adminController::updateDatabaseGames, Role.ADMIN);
        };
    }

}
