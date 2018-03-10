package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.dmitriylebyodkin.timemanager.Presenters.RunTaskPresenter;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Views.RunTaskView;

public class RunTaskActivity extends MvpAppCompatActivity implements RunTaskView {

    private static final String TAG = "myLogs";
    @BindView(R.id.tvTimerMinutes)
    TextView tvTimerMinutes;
    @BindView(R.id.tvTimerSeconds)
    TextView tvTimerSeconds;
    @BindView(R.id.btnComplete)
    Button btnComplete;
    @BindView(R.id.btnStart)
    Button btnStart;

    @InjectPresenter
    RunTaskPresenter presenter;

    private Intent intent;
    private int minutes, seconds = 0;
    private String minutesText, secondsText;
    private boolean isStartTimer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_task);

        ButterKnife.bind(this);

        intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Disposable[] disposable = {null};

        btnStart.setOnClickListener(view -> {
            Observable<Long> observable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            if (isStartTimer) {
                btnStart.setText(getString(R.string.start));
                isStartTimer = false;

                disposable[0].dispose();
            } else {
                btnStart.setText(getString(R.string.stop));
                isStartTimer = true;

                disposable[0] = observable.subscribe(
                        onNext -> {
                            seconds = seconds + 1;

                            if (seconds == 60) {
                                seconds = 0;
                                minutes = minutes + 1;
                            }

                            if (seconds < 10) {
                                secondsText = 0 + String.valueOf(seconds);
                            } else {
                                secondsText = String.valueOf(seconds);
                            }

                            if (minutes < 10) {
                                minutesText = 0 + String.valueOf(minutes);
                            } else {
                                minutesText = String.valueOf(minutes);
                            }

                            tvTimerSeconds.setText(secondsText);
                            tvTimerMinutes.setText(minutesText);
                        }
                );
            }
        });

        btnComplete.setOnClickListener(view -> {
            disposable[0].dispose();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.completion_task));
            alertDialog.setMessage(R.string.completion_task_dialog_text);
            alertDialog.setPositiveButton(getString(R.string.yes), (dialog, i) -> {
                dialog.dismiss();

                intent.putExtra("seconds", seconds);
                setResult(RESULT_OK, intent);
                finish();
            });
            alertDialog.setNeutralButton(getString(R.string.cancel), null);
            alertDialog.create().show();

//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//            alertDialog.setTitle(getString(R.string.how_work));
//            alertDialog.setSingleChoiceItems(Execution.STATUSES, 0, null);
//            alertDialog.setPositiveButton(getString(R.string.ready), (dialog, i) -> {
//                int checkedStatus = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
//                dialog.dismiss();
//
//                intent.putExtra("seconds", seconds);
//                intent.putExtra("status", checkedStatus);
//                setResult(RESULT_OK, intent);
//                finish();
//            });
//            alertDialog.setNeutralButton(getString(R.string.cancel), null);
//            alertDialog.create().show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
