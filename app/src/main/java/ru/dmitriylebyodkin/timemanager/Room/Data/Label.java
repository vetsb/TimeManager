package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by dmitr on 20.03.2018.
 */

@Entity
public class Label {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int imageId;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
