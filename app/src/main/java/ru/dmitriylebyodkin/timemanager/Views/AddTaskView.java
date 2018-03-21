package ru.dmitriylebyodkin.timemanager.Views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.dmitriylebyodkin.timemanager.Room.Data.Label;

/**
 * Created by dmitr on 08.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface AddTaskView extends MvpView {

    void showDescriptionDialog();

    void updateDescription(String text);

    void showDifficultyDialog();

    void updateDifficulty(int i);

    void showDeadlinesDialog(boolean isStart);

    void updateDateStart(long millis);

    void updateDeadline(long millis);

    void showPlanTimeDialog();

    void updatePlanTime(int time, int unit);

    void showLabelDialog();

    void updateLabel(int position);

    void addLabel(Label label);

    void finishEdit();

    void finishAdd(long taskId);
}
