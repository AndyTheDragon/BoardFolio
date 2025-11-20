package dat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dat.dto.GameDTO;
import dat.dto.GameListDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@ToString
public class GameList
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer listID;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "custom_list",
            joinColumns = @JoinColumn(name = "list_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    @ToString.Exclude
    private Set<Game> customList = new HashSet<>();

    private LocalDateTime createdDate;
    private boolean isPublic;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @Setter
    @JsonIgnore
    private UserAccount user;

    public GameList(String name)
    {
        this.name = name;
    }

    public void addGame(Game game)
    {
        if (game == null)
        {
            return;
        }
        this.customList.add(game);

    }

    public void removeGame(Game game)
    {
        if (game == null)
        {
            return;
        }
        this.customList.remove(game);
    }

    public GameListDTO toDTO(GameList gameList)
    {
        Set<GameDTO> customListDTO = gameList.getCustomList().stream()
                                             .map(game ->
                                                          game.toDTO(game))
                                             .collect(Collectors.toSet());

        GameListDTO gameListDTO = new GameListDTO();
        gameListDTO.setCustomList(customListDTO);
        gameListDTO.setName(gameList.getName());
        gameListDTO.setListID(gameList.getListID());
        gameListDTO.setCreatedDate(gameList.getCreatedDate());
        gameListDTO.setPublic(gameList.isPublic());

        return gameListDTO;
    }
}