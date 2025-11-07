package dat.services;

import dat.config.HibernateConfig;
import dat.dao.GenericDAO;
import dat.dto.GameDTO;
import dat.entities.Game;
import jakarta.persistence.EntityManagerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
            String csvFilePath = "src/main/java/dat/services/testdata/bgg_response_1_to_10.xml";
            String csvAsString = readCsvFileToString(csvFilePath);

            List<GameDTO> gameDTOS = BoardGameGeekService.parseBatchOfGames(csvAsString);

            for (GameDTO gameDTO : gameDTOS)
            {
                Game game = gameDTO.toEntity(gameDTO);
                genericDAO.create(game);
            }
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
