package ru.dmitriylebyodkin.timemanager.Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.dmitriylebyodkin.timemanager.Views.RunTaskView;

/**
 * Created by dmitr on 09.03.2018.
 */

@InjectViewState
public class RunTaskPresenter extends MvpPresenter<RunTaskView> {
    public RunTaskPresenter() {

    }

    public void startStopwatch() {
        getViewState().startStopwatch();
    }

    public void stopStopWatch(boolean changeText, boolean pause) {
        getViewState().stopStopwatch(changeText, pause);
    }
}
