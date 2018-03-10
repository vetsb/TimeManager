package ru.dmitriylebyodkin.timemanager.Room.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by dmitr on 09.03.2018.
 */

@Entity(foreignKeys = @ForeignKey(entity = Task.class, parentColumns = "id", childColumns = "taskId", onDelete = CASCADE))
public class Execution {
    public static final String[] STATUSES = new String[] {
            "Непродуктивно",
            "Продуктивно",
            "Очень продуктивно"
    };

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int taskId;
    private int time;
//    private int status;
    private int createdAt = (int) (System.currentTimeMillis()/1000L);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }
}
