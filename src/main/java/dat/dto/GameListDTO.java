package dat.dto;

import dat.entities.Game;
import dat.entities.GameList;
import dat.entities.UserAccount;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameListDTO
{
    private Integer listID;
    private String name;
    private Set<GameDTO> customList;
    private LocalDateTime createdDate;
    private boolean isPublic;
    private UserAccount user;

    public void addGame(GameDTO game)
    {
        if (game == null)
        {
            return;
        }
        this.customList.add(game);
    }

    public GameList toEntity(GameListDTO gameListDTO)
    {
        Set<Game> customList = gameListDTO.getCustomList().stream()
                                          .map(gameDTO ->
                                                       gameDTO.toEntity(gameDTO))
                                          .collect(Collectors.toSet());

        GameList gameList = new GameList();
        gameList.setCustomList(customList);
        gameList.setName(gameList.getName());
        gameList.setListID(gameList.getListID());
        gameList.setCreatedDate(gameList.getCreatedDate());
        gameList.setPublic(gameList.isPublic());

        return gameList;
    }
}