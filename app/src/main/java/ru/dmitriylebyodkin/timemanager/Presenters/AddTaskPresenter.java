package ru.dmitriylebyodkin.timemanager.Presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.dmitriylebyodkin.timemanager.Models.AddTaskModel;
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
        long taskId = AddTaskModel.insert(roomDb, task);

        getViewState().finishActivity(taskId);
    }
}
