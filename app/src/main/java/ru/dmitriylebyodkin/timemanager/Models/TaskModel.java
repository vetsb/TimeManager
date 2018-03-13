package ru.dmitriylebyodkin.timemanager.Models;


import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Dao.TaskDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.ExItem;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;

/**
 * Created by dmitr on 09.03.2018.
 */

public class TaskModel {
    public static void updateExecution(ExecutionDao executionDao, Execution execution) {
        executionDao.update(execution);
    }

    public static void insertExecution(ExecutionDao executionDao, Execution execution) {
        executionDao.insert(execution);
    }
    public static long insert(RoomDb roomDb, Task task) {
        long taskId = roomDb.getTaskDao().insert(task)[0];

        Execution execution = new Execution();
        execution.setTaskId((int) taskId);
        long executionId = roomDb.getExecutionDao().insert(execution)[0];

        ExItem exItem = new ExItem();
        exItem.setExecutionId((int) executionId);

        roomDb.getExItemDao().insert(exItem);

        return taskId;
    }

    public static void update(TaskDao taskDao, Task task) {
        taskDao.update(task);
    }

    public static void delete(TaskDao taskDao, int id) {
        taskDao.deleteById(id);
    }
}
