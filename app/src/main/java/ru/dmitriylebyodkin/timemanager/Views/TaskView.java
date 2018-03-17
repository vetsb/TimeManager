package ru.dmitriylebyodkin.timemanager.Views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by dmitr on 09.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface TaskView extends MvpView {
    void updateTimes();

    void setAdapter();

    void updateAdapter();

    void startRunActivity();

    void startEditActivity();

    void showDeleteDialog();
}
