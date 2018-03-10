package ru.dmitriylebyodkin.timemanager.Models;


import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Dao.TaskDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;

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

    public static void delete(TaskDao taskDao, int id) {
        taskDao.deleteById(id);
    }
}
