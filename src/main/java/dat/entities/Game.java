package dat.entities;

import dat.dto.GameDTO;
import dat.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Game
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;
    private String title;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int minAge;
    private int maxAge;
    private int releaseYear;
    @ElementCollection
    @CollectionTable(name = "game_languages", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "language")
    private List<String> languages;
    @Enumerated(EnumType.STRING)
    private Genre genre;

    public Game(GameDTO gameDTO)
    {
        this.gameId = gameDTO.getGameId();
        this.title = gameDTO.getTitle();
        this.description = gameDTO.getDescription();
        this.minNoOfPlayers = gameDTO.getMinNoOfPlayers();
        this.maxNoOfPlayers = gameDTO.getMaxNoOfPlayers();
        this.minAge = gameDTO.getMinAge();
        this.maxAge = gameDTO.getMaxAge();
        this.releaseYear = gameDTO.getReleaseYear();
        this.languages = gameDTO.getLanguages();
        this.genre = gameDTO.getGenre();
    }


}
