package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.timemanager.Adapters.TimeLineAdapter;
import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.Presenters.TaskPresenter;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.ExItem;
import ru.dmitriylebyodkin.timemanager.Room.Data.ExecutionWithItems;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.TaskView;

public class TaskActivity extends MvpAppCompatActivity implements TaskView {

    private static final String TAG = "myLogs";
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvHoursRunning)
    TextView tvHoursRunning;
    @BindView(R.id.tvHoursLeft)
    TextView tvHoursLeft;
    @BindView(R.id.tvDifficulty)
    TextView tvDifficulty;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.tvRun)
    TextView tvRun;
    @BindView(R.id.tvEdit)
    TextView tvEdit;
    @BindView(R.id.tvDelete)
    TextView tvDelete;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.layoutDescription)
    LinearLayout layoutDescription;

    @InjectPresenter
    TaskPresenter presenter;

    private Intent intent;
    private final static int RUN_CODE = 1;
    private final static int EDIT_TASK_CODE = 2;
    private List<ExecutionWithItems> listExecutions;
    private int taskId;
    private ExecutionDao executionDao;
    private TimeLineAdapter timeLineAdapter;
    private boolean hasChanges = false;
    private MenuItem menuItemRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        intent = getIntent();
        taskId = intent.getIntExtra("id", 0);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String title = intent.getStringExtra("title");

        if (title != null && title.length() > 0) {
            tvTitle.setText(title);
        }

        int difficulty = intent.getIntExtra("difficulty", 0);

        if (difficulty == -1) {
            tvDifficulty.setText(getString(R.string.not_specified));
        } else {
            tvDifficulty.setText(Task.DIFFICULTIES[difficulty]);
        }

        String description = intent.getStringExtra("description");

        if (description != null && description.length() > 0) {
            tvDescription.setText(description);
        } else {
            layoutDescription.setVisibility(View.GONE);
        }

        updateTimes();

        tvRun.setOnClickListener(view -> startRunActivity());
        tvEdit.setOnClickListener(view -> startEditActivity());
        tvDelete.setOnClickListener(view -> presenter.showDeleteDialog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        menuItemRun = menu.getItem(0);
        checkMenuItemRun();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void checkMenuItemRun() {
        Task task = new Task();
        task.setId(taskId);
        task.setUnit(intent.getIntExtra("unit", 0));
        task.setPlanTime(intent.getIntExtra("plan_time", 0));

        int planSeconds = task.getPlanSeconds();

        if (planSeconds == 0) {
            menuItemRun.setVisible(true);
        } else {
            executionDao = RoomDb.getInstance(this).getExecutionDao();
            listExecutions = executionDao.getWithItemsById(taskId);

            int time = 0;

            for (ExecutionWithItems executionWithItems: listExecutions) {
                for (ExItem exItem: executionWithItems.getItems()) {
                    time += exItem.getSeconds();
                }
            }

            if (time >= planSeconds) {
                menuItemRun.setVisible(false);
            } else {
                menuItemRun.setVisible(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navRun:
                startRunActivity();
                break;
            case R.id.navEdit:
                startEditActivity();
                break;
            case R.id.navDelete:
                presenter.showDeleteDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateTimes() {
        int time = 0;

        executionDao = RoomDb.getInstance(this).getExecutionDao();
        listExecutions = executionDao.getWithItemsById(taskId);

        for (ExecutionWithItems execution: listExecutions) {
            for (ExItem exItem: execution.getItems()) {
                time += exItem.getSeconds();
            }
        }

        int planTime = intent.getIntExtra("plan_time", 0);
        String timeLeftText = "";
        Calendar calendar = Calendar.getInstance(App.getTimeZone());

        if (planTime == 0) {
            tvHoursLeft.setText(R.string.no_time_limit);
        } else {
            switch (intent.getIntExtra("unit", -1)) {
                case 0:
                    if (time >= planTime) {
                        timeLeftText = getString(R.string.completed);
                    } else {
                        timeLeftText = App.formatSeconds(planTime-time);
                    }
                    break;
                case 1:
                    if (time >= planTime*60) {
                        timeLeftText = getString(R.string.completed);
                    } else {
                        timeLeftText = App.formatSeconds(planTime*60-time);
                    }
                    break;
                case 2:
                    if (time >= planTime*60*60) {
                        timeLeftText = getString(R.string.completed);
                    } else {
                        calendar.setTimeInMillis((planTime*60*60-time)*1000L);

                        int calendarHours = (planTime*60*60-time)/60/60;
                        int calendarMinutes = calendar.get(Calendar.MINUTE);

                        if (calendarHours == 0) {
                            timeLeftText = calendarMinutes + " " + App.formatWord(calendarMinutes, new String[] {"минута", "минуты", "минут"});
                        } else {
                            timeLeftText = calendarHours + " " + App.formatWord(calendarHours, new String[] {"час", "часа", "часов"});

                            if (calendarMinutes != 0) {
                                timeLeftText += " " + calendarMinutes + " " + App.formatWord(calendarMinutes, new String[] {"минута", "минуты", "минут"});
                            }
                        }
                    }
                    break;
            }

            tvHoursLeft.setText(timeLeftText);
        }

        if (time == 0) {
            tvHoursRunning.setText(R.string.not_yet_started);
        } else {
            tvHoursRunning.setText(App.formatSeconds(time));
        }
    }

    @Override
    public void startRunActivity() {
        Intent runIntent = new Intent(this, RunTaskActivity.class);
        runIntent.putExtra("id", intent.getIntExtra("id", 0));
        startActivityForResult(runIntent, RUN_CODE);
    }

    @Override
    public void startEditActivity() {
        Intent editIntent = new Intent(this, AddTaskActivity.class);
        editIntent.putExtras(intent.getExtras());
        startActivityForResult(editIntent, EDIT_TASK_CODE);
    }

    @Override
    public void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.deleting_task));
        alertDialog.setMessage(R.string.deleting_task_dialog_text);
        alertDialog.setPositiveButton(getString(R.string.yes), (dialog, i) -> {
            dialog.dismiss();

            presenter.delete(RoomDb.getInstance(this).getTaskDao(), taskId);
            hasChanges = true;

            onBackPressed();
        });
        alertDialog.setNeutralButton(getString(R.string.cancel), null);
        alertDialog.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        intent.putExtra("has_changes", hasChanges);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void setAdapter() {
        timeLineAdapter = new TimeLineAdapter(this, listExecutions, intent);
        recyclerView.setAdapter(timeLineAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * Возврат из RunTaskActivity
         */
        if (requestCode == RUN_CODE && resultCode == RESULT_OK) {
            listExecutions = executionDao.getWithItemsById(taskId);
            timeLineAdapter.setList(listExecutions);

            checkMenuItemRun();
            updateTimes();
        }

        /**
         * Возврат из AddTaskActivity
         */
        if (requestCode == EDIT_TASK_CODE && resultCode == RESULT_OK) {
            boolean dHasChanges = data.getBooleanExtra("has_changes", false);
            boolean isDeleted = data.getBooleanExtra("deleted", false);

            if (dHasChanges) {
                String title = data.getStringExtra("title");
                String description = data.getStringExtra("description");
                int difficulty = data.getIntExtra("difficulty", -1);

                intent.putExtras(data.getExtras());

                if (title != null && !title.equals("")) {
                    tvTitle.setText(title);
                    hasChanges = true;
                }

                if (description != null && !description.equals("")) {
                    if (tvDescription.getVisibility() == View.GONE) {
                        tvDescription.setVisibility(View.VISIBLE);
                    }

                    tvDescription.setText(description);
                    hasChanges = true;
                }

                if (difficulty != -1) {
                    tvDifficulty.setText(Task.DIFFICULTIES[difficulty]);
                    hasChanges = true;
                }

                checkMenuItemRun();
                updateTimes();
            }

            if (isDeleted) {
                intent.putExtra("has_changes", true);
                intent.putExtra("deleted", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void updateAdapter() {
        listExecutions = executionDao.getWithItemsById(taskId);
        timeLineAdapter = new TimeLineAdapter(this, listExecutions, intent);
        recyclerView.setAdapter(timeLineAdapter);
    }
}
