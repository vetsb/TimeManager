package ru.dmitriylebyodkin.timemanager.Presenters;

import android.content.Intent;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import ru.dmitriylebyodkin.timemanager.Room.Dao.TaskDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.Data.TaskWithExecutions;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.TasksView;

/**
 * Created by dmitr on 08.03.2018.
 */

@InjectViewState
public class TasksPresenter extends MvpPresenter<TasksView> {
    private static final String TAG = "myLogs";

    public TasksPresenter() {
        getViewState().setAdapter();
    }

    public void addTaskToList(Intent data) {
        TaskWithExecutions taskWithExecutions = new TaskWithExecutions();

        Task task = new Task();
        task.setId(data.getIntExtra("id", 0));
        task.setTitle(data.getStringExtra("title"));
        task.setPlanTime(data.getIntExtra("plan_time", 0));
        task.setUnit(data.getIntExtra("unit", 0));

        Execution execution = new Execution();
        execution.setTaskId(data.getIntExtra("id", 0));

        List<Execution> executionList = new ArrayList<>();
        executionList.add(execution);

        taskWithExecutions.setTask(task);
        taskWithExecutions.setExecutions(executionList);

        getViewState().addTaskToList(taskWithExecutions);
    }

    public void clearList(TaskDao taskDao) {
        taskDao.deleteAll();
        getViewState().clearList();
    }

    public void deleteTask(TaskDao taskDao, int id) {
        taskDao.deleteById(id);
    }

    public void checkAndHiddenList() {
        getViewState().checkAndHiddenList();
    }
}
