package com.alpha.messenger.persistance.model;

public class Image {
    private Long id;
    private String name;
    private String type;
    private String pic;

    public Image(String name, String type, String pic) {
        this.name = name;
        this.type = type;
        this.pic = pic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
