package dat.dto;

import dat.entities.Game;
import lombok.*;

import java.util.Map;
import java.util.Set;
import java.util.Set;
import java.util.stream.Collectors;

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

        Set<Genre> genres = gameDTO.getGenres().values().stream()
                .map(genreName -> Genre.valueOf(genreName.toUpperCase())) // hvis genreName matcher enum-navn
                .collect(Collectors.toSet());

        Game game = Game.builder()
                .title(gameDTO.getTitle())
                .description(gameDTO.getDescription())
                .minNoOfPlayers(gameDTO.getMinNoOfPlayers())
                .maxNoOfPlayers(gameDTO.getMaxNoOfPlayers())
                .minAge(gameDTO.getMinAge())
                .releaseYear(gameDTO.getReleaseYear())
                .genres(genres)
                .imageURL(gameDTO.getImage())
                .thumbnailURL(gameDTO.getThumbnail())
                .build();

        return  game;
    }
}