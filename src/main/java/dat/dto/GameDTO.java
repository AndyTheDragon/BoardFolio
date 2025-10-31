package dat.dto;

import dat.enums.Genre;

import java.util.List;

public class GameDTO
{
    private String title;
    private String description;
    private int minNoOfPlayers;
    private int maxNoOfPlayers;
    private int releaseYear;
    private List<String> languages;
    private Genre genre;

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public int getMinNoOfPlayers()
    {
        return minNoOfPlayers;
    }

    public int getMaxNoOfPlayers()
    {
        return maxNoOfPlayers;
    }

    public int getReleaseYear()
    {
        return releaseYear;
    }

    public List<String> getLanguages()
    {
        return languages;
    }

    public Genre getGenre()
    {
        return genre;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setMinNoOfPlayers(int minNoOfPlayers)
    {
        this.minNoOfPlayers = minNoOfPlayers;
    }

    public void setMaxNoOfPlayers(int maxNoOfPlayers)
    {
        this.maxNoOfPlayers = maxNoOfPlayers;
    }

    public void setReleaseYear(int releaseYear)
    {
        this.releaseYear = releaseYear;
    }

    public void setLanguages(List<String> languages)
    {
        this.languages = languages;
    }

    public void setGenre(Genre genre)
    {
        this.genre = genre;
    }

    @Override
    public String toString()
    {
        return "GameDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", minNoOfPlayers=" + minNoOfPlayers +
                ", maxNoOfPlayers=" + maxNoOfPlayers +
                ", releaseYear=" + releaseYear +
                ", languages=" + languages +
                ", genre=" + genre +
                '}';
    }
}
