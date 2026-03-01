package org.example.sessionservice.dto.game;

public class CreateGameRequest {
    private String name;
    private String description;
    private String genre;

    public CreateGameRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}

