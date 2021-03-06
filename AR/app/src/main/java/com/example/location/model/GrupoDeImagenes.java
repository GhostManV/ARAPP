package com.example.location.model;

import java.io.Serializable;

public class GrupoDeImagenes implements Serializable {
    Integer id;
    String name;
    String description;
    Integer image;
    boolean isSelected;

    public GrupoDeImagenes() {
    }

    public GrupoDeImagenes(Integer id, String name, String description, Integer image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.isSelected = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "GrupoDeImagenes{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image +
                '}';
    }
}
