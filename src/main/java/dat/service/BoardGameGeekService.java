package dat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.dto.GameDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

// BGG = Board Game Geek
// API URL: https://boardgamegeek.com/using_the_xml_api
// API2 Docs: https://boardgamegeek.com/wiki/page/BGG_XML_API2
public class BoardGameGeekService
{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String bggUri = "";

    public static List<GameDTO> getBGGGames()
    {
        List<GameDTO> gameDTOs = new ArrayList<>();

        //TODO fetch data from BGG
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(bggUri))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200)
            {
                //TODO handle data from BGG, 200 we got the data
            } else
            {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        //TODO create GameDTOs from BGG data

        return gameDTOs;
    }
}
