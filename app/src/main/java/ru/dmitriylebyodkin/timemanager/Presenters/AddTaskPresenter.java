package ru.dmitriylebyodkin.timemanager.Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.dmitriylebyodkin.timemanager.Models.TaskModel;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.AddTaskView;

/**
 * Created by dmitr on 08.03.2018.
 */

@InjectViewState
public class AddTaskPresenter extends MvpPresenter<AddTaskView> {
    public AddTaskPresenter() {

    }

    public void addTask(RoomDb roomDb, Task task) {
        long taskId = TaskModel.insert(roomDb, task);

        getViewState().finishActivity(taskId);
    }
}
