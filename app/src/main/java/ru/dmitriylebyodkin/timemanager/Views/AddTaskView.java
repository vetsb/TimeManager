package ru.dmitriylebyodkin.timemanager.Views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by dmitr on 08.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface AddTaskView extends MvpView {

    void setTaskId(long taskId);
}
