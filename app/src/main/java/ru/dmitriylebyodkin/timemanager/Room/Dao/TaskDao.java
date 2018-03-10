package ru.dmitriylebyodkin.timemanager.Room.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.Data.TaskWithExecutions;


/**
 * Created by dmitr on 08.03.2018.
 */

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task ORDER BY createdAt DESC")
    List<Task> getTasks();

    @Query("SELECT * FROM task WHERE id=:id")
    Task getTaskById(int id);

    @Query("SELECT * FROM task ORDER BY createdAt DESC")
    List<TaskWithExecutions> getTasksWithExecutions();

    @Insert
    long[] insert(Task... tasks);

    @Update
    void update(Task... task);

    @Delete
    void delete(Task... task);

    @Query("DELETE FROM task")
    void deleteAll();
}
