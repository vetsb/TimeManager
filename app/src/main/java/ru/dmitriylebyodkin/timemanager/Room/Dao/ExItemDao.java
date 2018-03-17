package ru.dmitriylebyodkin.timemanager.Room.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.timemanager.Room.Data.ExItem;


/**
 * Created by dmitr on 10.03.2018.
 */

@Dao
public interface ExItemDao {
//    @Query("SELECT * FROM ExItem WHERE executionId=:id AND createdAt>=:start AND createdAt<=:end AND ((seconds!=0 AND isStart=0) OR (seconds=0 OR isStart=1)) ORDER BY id DESC LIMIT 1")
    @Query("SELECT * FROM ExItem " +
            "WHERE executionId=:id AND " +
            "createdAt>=:start AND " +
            "createdAt<=:end AND " +
            "seconds=0 OR (seconds!=0 AND isStart=1 AND isPause=0) OR (seconds!=0 AND isStart=0 AND isPause=1) " +
            "ORDER BY id DESC " +
            "LIMIT 1")
    ExItem getByRange(int id, int start, int end);

    @Query("SELECT * FROM ExItem WHERE id=:id")
    ExItem getById(int id);

    @Query("SELECT * FROM ExItem WHERE executionId=:id")
    List<ExItem> getByExecutionId(int id);

    @Query("SELECT SUM(seconds) FROM ExItem WHERE executionId=:executionId")
    int getSumTime(int executionId);

    @Insert
    long[] insert(ExItem... exItems);

    @Update
    void update(ExItem... exItem);

    @Delete
    void delete(ExItem... exItem);

}
