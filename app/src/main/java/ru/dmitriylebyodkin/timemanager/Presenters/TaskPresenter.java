package ru.dmitriylebyodkin.timemanager.Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.Calendar;
import java.util.List;

import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.Models.TaskModel;
import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Views.TaskView;


/**
 * Created by dmitr on 09.03.2018.
 */

@InjectViewState
public class TaskPresenter extends MvpPresenter<TaskView> {
    private static final String TAG = "myLogs";

    public TaskPresenter() {
        getViewState().setAdapter();
    }

    public void update(ExecutionDao executionDao, List<Execution> listExecutions, int taskId, int seconds) {
        Calendar calendarStart = Calendar.getInstance(App.getTimeZone());
        calendarStart.setTimeInMillis(System.currentTimeMillis());
        calendarStart.set(Calendar.MILLISECOND, 0);
        calendarStart.set(Calendar.SECOND, 0);
        calendarStart.set(Calendar.MINUTE, 0);
        calendarStart.set(Calendar.HOUR, 0);

        Calendar calendarEnd = Calendar.getInstance(App.getTimeZone());
        calendarEnd.setTimeInMillis(System.currentTimeMillis());
        calendarEnd.set(Calendar.MILLISECOND, 999);
        calendarEnd.set(Calendar.SECOND, 59);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.HOUR, 23);

        /**
         * ищется Execution сегодняшнего дня
         */

        Execution newExecution = null;

        for (Execution execution: listExecutions) {
            int createdAt = execution.getCreatedAt();

            if (createdAt >= calendarStart.getTimeInMillis()/1000L && createdAt <= calendarEnd.getTimeInMillis()/1000L) {
                newExecution = execution;
            }
        }

        /**
         * Если не находится, то создается новый и записывается в базу
         */

        if (newExecution == null) {
            newExecution = new Execution();
            newExecution.setTaskId(taskId);
            newExecution.setTime(seconds);
//            newExecution.setStatus(status);

            TaskModel.insertExecution(executionDao, newExecution);
        } else {
            newExecution.setTime(newExecution.getTime() + seconds);
//            newExecution.setStatus(status);

            TaskModel.updateExecution(executionDao, newExecution);
        }
    }
}
