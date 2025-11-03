package dat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class GameDTO
{
    private Long BGG_API_ID;
    private String title;
    private String image;
    private String thumbnail;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int minAge;
    private int releaseYear;
    private Map<Long, String> genres;
}
