package me.minigames.Map;

import java.util.List;

public class Map {

    private String name;
    private List<String> authors;
    private String desc;
    private int id;

    public Map(String name, String desc, List<String> author, int id) {
        this.name = name;
        this.desc = desc;
        this.authors = author;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
    	return desc;
    }
    
    public List<String> getAuthors() {
        return authors;
    }

    public int getId() {
        return id;
    }
    
    

}
