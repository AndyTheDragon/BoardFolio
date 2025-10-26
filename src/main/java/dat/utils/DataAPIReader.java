package dat.utils;


import dat.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DataAPIReader
{
    private final Logger logger = LoggerFactory.getLogger(DataAPIReader.class);

    public String getDataFromClient(String url)
    {

        try
        {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
            {
                return response.body();
            }
            else
            {
                logger.error("GET request failed. Status code: {}", response.statusCode());
                throw new ApiException(response.statusCode(), "GET request failed. Status code: " + response.statusCode());
            }
        }
        catch (InterruptedException | URISyntaxException  | IOException e)
        {
            logger.error("Error fetching data from API", e);
            throw new RuntimeException("Error fetching data from API", e);
        }
    }
}

