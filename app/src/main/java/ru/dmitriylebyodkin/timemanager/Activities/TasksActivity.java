package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.timemanager.Adapters.TasksAdapter;
import ru.dmitriylebyodkin.timemanager.Presenters.TasksPresenter;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.TaskWithExecutions;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.TasksView;

public class TasksActivity extends MvpAppCompatActivity implements TasksView {

    private static final String TAG = "myLogs";
    @BindView(R.id.floatButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.listTasks)
    RecyclerView recyclerView;

    @InjectPresenter
    TasksPresenter presenter;

    public static final int ADD_TASK_CODE = 1;
    private TasksAdapter adapter;
    private RoomDb roomDb;
    private List<TaskWithExecutions> listTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ButterKnife.bind(this);

        floatingActionButton.setOnClickListener(view -> startActivityForResult(
                new Intent(this, AddTaskActivity.class),
                ADD_TASK_CODE
        ));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        roomDb = RoomDb.getInstance(this);
        listTasks = roomDb.getTaskDao().getTasksWithExecutions();
    }

    @Override
    public void setAdapter() {
        adapter = new TasksAdapter(this, listTasks);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void addTaskToList(TaskWithExecutions taskWithExecutions) {
        listTasks.add(0, taskWithExecutions);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearList() {
        listTasks = null;
        adapter.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TASK_CODE && resultCode == RESULT_OK) {
            presenter.addTaskToList(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navDeleteAll:
                presenter.clearList(roomDb);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
