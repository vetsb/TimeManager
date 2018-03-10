package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @InjectPresenter
    TaskPresenter presenter;

    private Intent intent;
    private final static int RUN_CODE = 1;
    private List<Execution> listExecutions;
    private int taskId;
    private ExecutionDao executionDao;
    private TimeLineAdapter timeLineAdapter;
    private int seconds;

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void setAdapter() {
        timeLineAdapter = new TimeLineAdapter(this, listExecutions);
        recyclerView.setAdapter(timeLineAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RUN_CODE && resultCode == RESULT_OK) {
            seconds = data.getIntExtra("seconds", 0);

            presenter.update(executionDao, listExecutions, taskId, seconds); // data.getIntExtra("status", 0)
            timeLineAdapter.updateList(executionDao.getExecutionsByTaskId(taskId));
        }
    }
}
