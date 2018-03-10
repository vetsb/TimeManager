package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by dmitr on 10.03.2018.
 */

@Entity(foreignKeys = @ForeignKey(entity = Task.class, parentColumns = "id", childColumns = "executionId", onDelete = CASCADE))
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
    private int status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }
}
