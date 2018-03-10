package ru.dmitriylebyodkin.timemanager.Models;


import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;

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
}
