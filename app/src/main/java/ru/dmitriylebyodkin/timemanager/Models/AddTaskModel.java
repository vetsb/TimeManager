package ru.dmitriylebyodkin.timemanager.Models;


import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;

/**
 * Created by dmitr on 09.03.2018.
 */

public class AddTaskModel {
    public static long insert(RoomDb roomDb, Task task) {
        long taskId = roomDb.getTaskDao().insert(task)[0];

        Execution execution = new Execution();
        execution.setTaskId((int) taskId);

        roomDb.getExecutionDao().insert(execution);

        return taskId;

//        ExItem exItem = new ExItem();
//        exItem.setExecutionId((int) executionId);
//
//        roomDb.getExItemDao().insert(exItem);
    }
}
