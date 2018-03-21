package ru.dmitriylebyodkin.timemanager.Room.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.timemanager.Room.Data.Label;

/**
 * Created by dmitr on 20.03.2018.
 */

@Dao
public interface LabelDao {
    @Query("SELECT * FROM Label ORDER BY id")
    List<Label> getAll();

    @Insert
    long[] insert(Label... labels);

    @Update
    void update(Label... label);

    @Delete
    void delete(Label... label);
}
