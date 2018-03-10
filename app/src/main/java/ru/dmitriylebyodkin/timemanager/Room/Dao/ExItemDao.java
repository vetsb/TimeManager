package ru.dmitriylebyodkin.timemanager.Room.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import ru.dmitriylebyodkin.timemanager.Room.Data.ExItem;


/**
 * Created by dmitr on 10.03.2018.
 */

@Dao
public interface ExItemDao {
    @Insert
    long[] insert(ExItem... exItems);

    @Update
    void update(ExItem... exItem);

    @Delete
    void delete(ExItem... exItem);
}
