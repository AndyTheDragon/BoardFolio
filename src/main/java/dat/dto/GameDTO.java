package dat.dto;

import dat.entities.Game;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameDTO {
    private Long BGG_API_ID;
    private String title;
    private String image;
    private String thumbnail;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int minAge;
    private int releaseYear;
    private Set<String> genres;
}

//public Game toEntity(GameDTO gameDTO) {
//    Game game = Game.builder()
//            .title("Dragonmaster")
//            .description()
//            .build();
//}
