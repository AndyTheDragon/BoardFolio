package dat.entities;

import dat.dto.GameDTO;
import dat.enums.Genre;
import dat.enums.Languages;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Game
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    private String title;
    @Lob
    private String description;
    private int minAge;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int releaseYear;

    private String imageURL;
    private String thumbnailURL;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private UserAccount owner;


    @ManyToMany(mappedBy = "customList")
    @ToString.Exclude
    private Set<GameList> gameLists = new HashSet<>();

    @ElementCollection(targetClass = Languages.class)
    @Enumerated(EnumType.STRING)
    private List<Languages> languages;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    private Set<Genre> genres = new HashSet<>();

    public Game(String title,
                String description,
                int minNoOfPlayers,
                int maxNoOfPlayers,
                int releaseYear,
                String imageURL,
                String thumbnailURL,
                Set<GameList> GameLists, Set<Genre> genres)
    {
        this.title = title;
        this.description = description;
        this.minNoOfPlayers = minNoOfPlayers;
        this.maxNoOfPlayers = maxNoOfPlayers;
        this.releaseYear = releaseYear;
        this.imageURL = imageURL;
        this.thumbnailURL = thumbnailURL;
        this.genres = genres;
    }

    public void setOwner(UserAccount user)
    {
        if (this.owner != null)
        {
            this.owner.getMyCollection().remove(this);
        }
        this.owner = user;
        if (user != null && !user.getMyCollection().contains(this))
        {
            user.getMyCollection().add(this);
        }
    }

    public void addToCollection(GameList gameList)
    {
        if (gameList == null)
        {
            return;
        }
        gameLists.add(gameList);
        gameList.getCustomList().add(this);
    }

    public void removeFromCollection(GameList gameList)
    {
        if (gameList == null)
        {
            return;
        }
        gameLists.remove(gameList);
        gameList.getCustomList().remove(this);
    }

    public static GameDTO toDTO(Game game)
    {
        Set<String> genreStrings = game.getGenres().stream()
                                       .map(Enum::name) // e.g. "CARD_GAME"
                                       .map(name -> name.replaceAll("_", " ")) // convert back to spaces
                                       .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1)
                                                                                             .toLowerCase()) // optional: make it pretty
                                       .collect(Collectors.toSet());

        GameDTO gameDTO = new GameDTO();
        gameDTO.setTitle(game.getTitle());
        gameDTO.setDescription(game.getDescription());
        gameDTO.setMinNoOfPlayers(game.getMinNoOfPlayers());
        gameDTO.setMaxNoOfPlayers(game.getMaxNoOfPlayers());
        gameDTO.setMinAge(game.getMinAge());
        gameDTO.setReleaseYear(game.getReleaseYear());
        gameDTO.setGenres(genreStrings);
        gameDTO.setImage(game.getImageURL());
        gameDTO.setThumbnail(game.getThumbnailURL());

        return gameDTO;
    }

}