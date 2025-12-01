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

    public GameList toEntity(GameListDTO dto)
    {

        // convert games
        Set<Game> customList = new HashSet<>();
        if (dto.getCustomList() != null)
        {
            customList = dto.getCustomList().stream()
                            .map(g -> g.toEntity(g))
                            .collect(Collectors.toSet());
        }

        GameList entity = new GameList();
        entity.setListID(dto.getListID());
        entity.setName(dto.getName());
        entity.setCustomList(customList);
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setPublic(dto.isPublic());
        entity.setUser(dto.getUser());

        return entity;
    }

}