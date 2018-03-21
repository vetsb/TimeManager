package ru.dmitriylebyodkin.timemanager.Models;

import ru.dmitriylebyodkin.timemanager.Room.Data.Label;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;

/**
 * Created by dmitr on 20.03.2018.
 */

public class LabelModel {
    public static long insert(RoomDb roomDb, Label label) {
        return roomDb.getLabelDao().insert(label)[0];
    }
}
