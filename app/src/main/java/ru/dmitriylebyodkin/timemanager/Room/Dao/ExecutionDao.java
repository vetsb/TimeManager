package ru.dmitriylebyodkin.timemanager.Room.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;


/**
 * Created by dmitr on 09.03.2018.
 */

@Dao
public interface ExecutionDao {
    @Query("SELECT * FROM execution ORDER BY createdAt")
    List<Execution> getExecutions();

    @Query("SELECT * FROM execution WHERE taskId=:id ORDER BY createdAt")
    List<Execution> getExecutionsByTaskId(int id);

//    @Query("SELECT * FROM execution WHERE taskId=:id ORDER BY createdAt")
//    List<ExecutionWithItems> getExecutionsWithItemsByTaskId(int id);

    @Insert
    void insert(Execution... executions);

    @Update
    void update(Execution... execution);

    @Delete
    void delete(Execution... execution);
}
