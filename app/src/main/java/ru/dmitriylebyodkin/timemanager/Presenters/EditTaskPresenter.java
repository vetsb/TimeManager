package ru.dmitriylebyodkin.timemanager.Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.dmitriylebyodkin.timemanager.Models.TaskModel;
import ru.dmitriylebyodkin.timemanager.Room.Dao.TaskDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Views.EditTaskView;

/**
 * Created by dmitr on 11.03.2018.
 */

@InjectViewState
public class EditTaskPresenter extends MvpPresenter<EditTaskView> {
    public EditTaskPresenter() {

    }

    public void editTask(TaskDao taskDao, Task task) {
        TaskModel.update(taskDao, task);

        getViewState().finishActivity();
    }
}
