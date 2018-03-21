package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.Presenters.RunTaskPresenter;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Dao.ExItemDao;
import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.ExItem;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.RunTaskView;

public class RunTaskActivity extends MvpAppCompatActivity implements RunTaskView {

    private static final String TAG = "myLogs";
    @BindView(R.id.chronometer)
    Chronometer mChronometer;
    @BindView(R.id.btnComplete)
    Button btnComplete;
    @BindView(R.id.btnStart)
    Button btnStart;

    @InjectPresenter
    RunTaskPresenter presenter;

    private Intent intent;
    private int seconds = 0;
    private boolean isStartTimer = false;
    private Disposable disposable;
    private long millis = 0;
    private RoomDb roomDb;
    private int taskId, planSeconds;
    private Execution newExecution;
    private ExecutionDao executionDao;
    private ExItemDao exItemDao;
    private ExItem exItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_task);

        ButterKnife.bind(this);

        intent = getIntent();
        taskId = intent.getIntExtra("id", 0);
        roomDb = RoomDb.getInstance(this);

        /**
         * Начало текущего дня
         */
        Calendar calendarStart = Calendar.getInstance(App.getTimeZone());
        calendarStart.setTimeInMillis(System.currentTimeMillis());
        calendarStart.set(Calendar.MILLISECOND, 0);
        calendarStart.set(Calendar.SECOND, 0);
        calendarStart.set(Calendar.MINUTE, 0);
        calendarStart.set(Calendar.HOUR, 0);

        /**
         * Конец текущего дня
         */
        Calendar calendarEnd = Calendar.getInstance(App.getTimeZone());
        calendarEnd.setTimeInMillis(System.currentTimeMillis());
        calendarEnd.set(Calendar.MILLISECOND, 999);
        calendarEnd.set(Calendar.SECOND, 59);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.HOUR, 23);

        int timeStart = (int) (calendarStart.getTimeInMillis()/1000L);
        int timeEnd = (int) (calendarEnd.getTimeInMillis()/1000L);

        executionDao = roomDb.getExecutionDao();
        exItemDao = roomDb.getExItemDao();

        /**
         * Находим сегодняшний Execution. Если нет, создаем и добавляем в базу
         */
        newExecution = executionDao.getByRange(taskId, timeStart, timeEnd);

        /**
         * Если нет такого Execution, то создать и добавить в базу. Также создать ExItem с executionId
         * Если есть, то найти сегодняшний неначавшийся ExItem. Если нет, то создать и добавить в базу
         */
        if (newExecution == null) {
            newExecution = new Execution();
            newExecution.setTaskId(taskId);

            long executionId = executionDao.insert(newExecution)[0];

            exItem = new ExItem();
            exItem.setExecutionId((int) executionId);

            long exItemId = exItemDao.insert(exItem)[0];
            exItem.setId((int) exItemId);
        } else {
            int executionId = newExecution.getId();

            exItem = exItemDao.getByRange(executionId, timeStart, timeEnd);

            if (exItem == null) {
                exItem = new ExItem();
                exItem.setExecutionId(executionId);

                long exItemId = exItemDao.insert(exItem)[0];
                exItem.setId((int) exItemId);
            }
        }

        Task task = roomDb.getTaskDao().getTaskById(taskId);

        switch (task.getUnit()) {
            case 0:
                planSeconds = task.getPlanTime();
                break;
            case 1:
                planSeconds = task.getPlanTime()*60;
                break;
            case 2:
                planSeconds = task.getPlanTime()*60*60;
                break;
        }

        millis = exItem.getSeconds()*1000L;

        exItem.setVisible(true);

        /**
         * Установить стартовое время (нужно, если таймер на завершен)
         */
        mChronometer.setBase(SystemClock.elapsedRealtime() - millis);

        if (exItem.isStart()) {
            isStartTimer = true;
            mChronometer.start();

            btnStart.setText(getString(R.string.stop));
            exItem.setStart(true);
            exItem.setPause(false);
            exItemDao.update(exItem);
        }

        /**
         * Каждую секунду обновлять время текущего ExItem в базе
         */
        mChronometer.setOnChronometerTickListener(chronometer -> {
            millis = SystemClock.elapsedRealtime() - mChronometer.getBase();
            seconds = (int) (millis/1000L);

            if (seconds != 0) {
                exItem.setSeconds(exItem.getSeconds()+1);

                if (planSeconds != 0 && planSeconds <= seconds) {
                    exItem.setPause(false);
                    exItem.setStart(false);

                    mChronometer.stop();
                    Toast.makeText(this, "Занятие закончено", Toast.LENGTH_LONG).show();
                }

                exItemDao.update(exItem);
            }
        });

        /**
         * Кнопка назад в тулбаре
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnStart.setOnClickListener(view -> {
            if (isStartTimer) {
                presenter.stopStopWatch(true, true);

                exItem.setStart(false);
                exItem.setPause(true);
                exItemDao.update(exItem);
            } else {
                presenter.startStopwatch();
            }
        });

        btnComplete.setOnClickListener(view -> {
            presenter.stopStopWatch(false, false);

            View layout = getLayoutInflater().inflate(R.layout.dialog_add_description, null);
            EditText etDescription = layout.findViewById(R.id.etDescription);
            etDescription.setHint(R.string.completion_task_edittext_hint);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.completion_task);
            alertDialog.setView(layout);
            alertDialog.setPositiveButton(R.string.complete, (dialogInterface, i) -> {
                String description = etDescription.getText().toString().trim();

                if (!description.equals("")) {
                    exItem.setDescription(description);
                }

                exItem.setStart(false);
                exItem.setPause(false);
                exItem.setVisible(false);
                exItemDao.update(exItem);
                dialogInterface.dismiss();

                intent.putExtra("ex_item_id", exItem.getId());
                setResult(RESULT_OK, intent);
                finish();
            });
            alertDialog.setNeutralButton(R.string.cancel, null);
            alertDialog.create().show();
        });
    }

    /**
     * Нажатие кнопки назад
     */
    @Override
    public boolean onSupportNavigateUp() {
        /**
         * Если секундомер запущен, создать Observable, который будет каждую секунду обновлять время в текущем ExItem
         * Если нет, остановить секундомер, показать диалог и закончить
         */
        if (isStartTimer) {
            disposable = Observable.interval(1000, TimeUnit.MILLISECONDS, Schedulers.io())
                    .subscribe(
                            onNext -> {
                                ExItem mItem = exItemDao.getById(exItem.getId());

                                if (mItem.isStart()) {
                                    if (mItem.isVisible()) {
                                        if (!disposable.isDisposed()) {
                                            disposable.dispose();
                                        }
                                    } else {
                                        Task task = roomDb.getTaskDao().getTaskById(taskId);
                                        int planSeconds = task.getPlanSeconds();

                                        if (planSeconds == 0 || planSeconds > exItem.getSeconds() + 1) {
                                            exItem.setSeconds(exItem.getSeconds() + 1);
                                            exItemDao.update(exItem);
                                        } else {
                                            List<Execution> executionList = roomDb.getExecutionDao().getExecutionsByTaskId(taskId);
                                            int time = 0;

                                            for (Execution execution: executionList) {
                                                time += roomDb.getExItemDao().getSumTime(execution.getId());
                                            }

                                            exItem.setStart(false);
                                            exItem.setPause(false);
                                            exItem.setSeconds(planSeconds - time);
                                            exItemDao.update(exItem);
                                            disposable.dispose();
                                        }
                                    }
                                } else {
                                    if (!disposable.isDisposed()) {
                                        disposable.dispose();
                                    }
                                }
                            }
                    );
        } else {
            presenter.stopStopWatch(true, true);
            exItem.setStart(false);
            exItem.setPause(true);
        }

        exItem.setVisible(false);
        exItemDao.update(exItem);

        intent.putExtra("ex_item_id", exItem.getId());
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }

    @Override
    public void startStopwatch() {
        btnStart.setText(getString(R.string.stop));
        isStartTimer = true;

        mChronometer.setBase(SystemClock.elapsedRealtime() - millis);
        mChronometer.start();

        exItem.setStart(true);
        exItem.setPause(false);
        exItemDao.update(exItem);
    }

    @Override
    public void stopStopwatch(boolean changeText, boolean pause) {
        if (isStartTimer) {
            if (changeText) {
                btnStart.setText(getString(R.string.start));
            }

            isStartTimer = false;
            mChronometer.stop();
//            exItem.setStart(false);
//            exItem.setPause(pause);
//            exItemDao.update(exItem);
        }
    }
}
