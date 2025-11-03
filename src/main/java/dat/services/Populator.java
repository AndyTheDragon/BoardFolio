package dat.services;

import dat.dto.GameDTO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Populator {
    public void testPopulator() {
    }

    public static List<GameDTO> DevPopulator() throws Exception {
        String csvFilePath = "src/main/java/dat/services/testdata/bgg_response_1_to_10.xml"; // path to your CSV file
        String csvAsString = readCsvFileToString(csvFilePath);

        List<GameDTO> gameDTOS = BoardGameGeekService.parseBatchOfGames(csvAsString);

        return gameDTOS;

    }

    public static String readCsvFileToString(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }
}
