package dat.dto;

import dat.entities.UserAccount;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

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
    private Set<GameDTO> games;
    private LocalDateTime createdDate;
    private boolean isPublic;
    private UserAccount user;


}
