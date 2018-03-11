package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.timemanager.Adapters.TimeLineAdapter;
import ru.dmitriylebyodkin.timemanager.Presenters.TaskPresenter;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Dao.ExecutionDao;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.TaskView;

public class TaskActivity extends MvpAppCompatActivity implements TaskView {

    private static final String TAG = "myLogs";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @InjectPresenter
    TaskPresenter presenter;

    private Intent intent;
    private final static int RUN_CODE = 1;
    private final static int EDIT_TASK_CODE = 2;
    private List<Execution> listExecutions;
    private int taskId;
    private ExecutionDao executionDao;
    private TimeLineAdapter timeLineAdapter;
    private int seconds;
    private boolean hasChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        intent = getIntent();
        setTitle(intent.getStringExtra("title"));

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        executionDao = RoomDb.getInstance(this).getExecutionDao();

        taskId = intent.getIntExtra("id", 0);
        listExecutions = executionDao.getExecutionsByTaskId(taskId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navRun:
                Intent runIntent = new Intent(this, RunTaskActivity.class);
                runIntent.putExtra("id", intent.getIntExtra("id", 0));
                startActivityForResult(runIntent, RUN_CODE);
                break;
            case R.id.navEdit:
                Intent editIntent = new Intent(this, EditTaskActivity.class);
                editIntent.putExtras(intent.getExtras());
                startActivityForResult(editIntent, EDIT_TASK_CODE);
                break;
            case R.id.navDelete:
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
                break;
        }

        return super.onOptionsItemSelected(item);
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
        timeLineAdapter = new TimeLineAdapter(this, listExecutions);
        recyclerView.setAdapter(timeLineAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * Возврат из RunTaskActivity
         */
        if (requestCode == RUN_CODE && resultCode == RESULT_OK) {
            seconds = data.getIntExtra("seconds", 0);

            if (seconds != 0) {
                presenter.update(executionDao, listExecutions, taskId, seconds); // data.getIntExtra("status", 0)
                hasChanges = true;
            }
        }

        if (requestCode == EDIT_TASK_CODE && resultCode == RESULT_OK) {
            boolean dHasChanges = data.getBooleanExtra("has_changes", false);

            if (dHasChanges) {
                String title = data.getStringExtra("title");

                if (title != "") {
                    setTitle(title);
                    hasChanges = true;
                }
            }
        }
    }

    @Override
    public void updateAdapter() {
        listExecutions = executionDao.getExecutionsByTaskId(taskId);
        timeLineAdapter = new TimeLineAdapter(this, listExecutions);
        recyclerView.setAdapter(timeLineAdapter);
    }
}
