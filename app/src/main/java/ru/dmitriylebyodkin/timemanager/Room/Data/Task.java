package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by dmitr on 08.03.2018.
 */

@Entity
public class Task {
    public static final String[] UNITS = new String[] {
            "секунды",
            "минуты",
            "часы",
    };

    public static final String[] DIFFICULTIES = new String[] {
            "Низкий",
            "Средний",
            "Высокий"
    };

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private int planTime;
    private int unit;
    private String description;
    private int difficulty;
    private int timestampStart;
    private int timestampDeadline;
    private int createdAt = (int) (System.currentTimeMillis()/1000L);
    private int updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlanTime() {
        return planTime;
    }

    public void setPlanTime(int planTime) {
        this.planTime = planTime;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(int timestampStart) {
        this.timestampStart = timestampStart;
    }

    public int getTimestampDeadline() {
        return timestampDeadline;
    }

    public void setTimestampDeadline(int timestampDeadline) {
        this.timestampDeadline = timestampDeadline;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }
}
