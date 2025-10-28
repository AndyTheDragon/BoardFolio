package dat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Collection {

    @Id
    private Integer listID;

    private String name;

    @ManyToMany
    private Set<Game> games = new HashSet<>();

    @ManyToOne
    private UserAccount user;


    public Game addGame(Integer gameId, Integer listID){
        return null;
    }
    public Game removeGame(Integer gameId, Integer listID){
        return null;
    }
}
