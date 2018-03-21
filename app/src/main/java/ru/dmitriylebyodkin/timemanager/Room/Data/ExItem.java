package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by dmitr on 10.03.2018.
 */

@Entity(foreignKeys = @ForeignKey(entity = Execution.class, parentColumns = "id", childColumns = "executionId", onDelete = CASCADE))
public class ExItem {
    public static final String[] STATUSES = new String[] {
            "Непродуктивно",
            "Продуктивно",
            "Очень продуктивно"
    };

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int executionId;
    private int seconds;
    private String description;
    private boolean isStart = false;
    private boolean isPause = false;
    private boolean isVisible = false;
    private int createdAt = (int) (System.currentTimeMillis()/1000L);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }
}
