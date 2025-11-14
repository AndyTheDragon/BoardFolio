package dat.services;

import dat.config.HibernateConfig;
import dat.dao.GenericDAO;
import dat.dto.GameDTO;
import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import jakarta.persistence.EntityManagerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Populator
{
    private final static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final static GenericDAO genericDAO = new GenericDAO(emf);

    public void testPopulator()
    {
    }

    public static void DevPopulator()
    {
        try
        {
            String csvFilePath = "src/main/resources/testdata/bgg_response_1_to_10.xml";
            String csvAsString = readCsvFileToString(csvFilePath);

            List<GameDTO> gameDTOS = BoardGameGeekService.parseBatchOfGames(csvAsString);

            UserAccount user = new UserAccount("testUser", "test");


            for (GameDTO gameDTO : gameDTOS)
            {
                Game game = gameDTO.toEntity(gameDTO);
                genericDAO.create(game);
            }

            user.getMyCollection().setUser(user);
            user.getMyCollection().setCreatedDate(LocalDateTime.now());
            user.getMyCollection().setName("My collection of games");
            user.addToMyCollection(gameDTOS.get(1).toEntity(gameDTOS.get(1)));
            user.addToMyCollection(gameDTOS.get(2).toEntity(gameDTOS.get(2)));
            user.addToMyCollection(gameDTOS.get(5).toEntity(gameDTOS.get(5)));


            genericDAO.create(user);

            GameList customList = new GameList("test");
            customList.setCreatedDate(LocalDateTime.now());
            user.addList(customList);

            customList.addGame(gameDTOS.get(0).toEntity(gameDTOS.get(0)));
            customList.addGame(gameDTOS.get(1).toEntity(gameDTOS.get(1)));
            customList.addGame(gameDTOS.get(2).toEntity(gameDTOS.get(2)));
            genericDAO.create(customList);

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String readCsvFileToString(String filePath)
    {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                content.append(line).append("\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return content.toString();
    }
}
