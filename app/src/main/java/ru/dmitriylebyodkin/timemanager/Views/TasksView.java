package ru.dmitriylebyodkin.timemanager.Views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.dmitriylebyodkin.timemanager.Room.Data.TaskWithExecutions;

/**
 * Created by dmitr on 08.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface TasksView extends MvpView {
    void setAdapter();

    void addTaskToList(TaskWithExecutions taskWithExecutions);

    void clearList();

    void deleteTask(int position, int id);
}
