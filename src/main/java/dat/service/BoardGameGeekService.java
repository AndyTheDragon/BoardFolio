package dat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JsonNode;
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
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final String bggUri = "https://boardgamegeek.com/xmlapi/collection/eekspider";  //TODO this is a temporary URI for getting mock-data

    public static List<GameDTO> getBGGGames()
    {
        List<GameDTO> gameDTOs = new ArrayList<>();

        //TODO fetch data from BGG
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(bggUri))
                    .header("User-Agent", "Mozilla/5.0") //TODO: should use API key, mimic browser
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
            {
                //TODO handle data from BGG, 200 we got the data
                String xml = response.body();

                // Parse XML to a JsonNode tree
                JsonNode rootNode = xmlMapper.readTree(xml);

                // Loop over all <item> nodes
                for (JsonNode itemNode : rootNode.path("item"))
                {
                    GameDTO game = xmlMapper.treeToValue(itemNode, GameDTO.class);
                    gameDTOs.add(game);
                }


            } else
            {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return gameDTOs;
    }
}
