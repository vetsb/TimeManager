package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.ArrayList;
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

    @BindView(R.id.listTasks)
    RecyclerView recyclerView;
    @BindView(R.id.btnCreate)
    Button btnCreate;
    @BindView(R.id.layoutAdd)
    LinearLayout layoutAdd;

    @InjectPresenter
    TasksPresenter presenter;

    public static final int ADD_TASK_CODE = 1;
    public static final int TASK_CODE = 2;

    private TasksAdapter adapter;
    private RoomDb roomDb;
    private List<TaskWithExecutions> listTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ButterKnife.bind(this);

        btnCreate.setOnClickListener(view -> startAddActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        roomDb = RoomDb.getInstance(this);
        listTasks = roomDb.getTaskDao().getTasksWithExecutions();

        presenter.checkAndHiddenList();
    }

    @Override
    public void setAdapter() {
        adapter = new TasksAdapter(this, listTasks);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void addTaskToList(TaskWithExecutions taskWithExecutions) {
        adapter.add(taskWithExecutions);
        presenter.checkAndHiddenList();
    }

    @Override
    public void clearList() {
        adapter.clear();
        presenter.checkAndHiddenList();
    }

    @Override
    public void deleteTask(int position, int id) {
        presenter.deleteTask(roomDb.getTaskDao(), id);
        adapter.remove(position);
        presenter.checkAndHiddenList();
    }

    @Override
    public void checkAndHiddenList() {
        if (adapter.getItemCount() == 0) {
            layoutAdd.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutAdd.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TASK_CODE && resultCode == RESULT_OK) {
            presenter.addTaskToList(data);
        }

        if (requestCode == TASK_CODE && resultCode == RESULT_OK) {
            boolean hasChanges = data.getBooleanExtra("has_changes", false);
            boolean isDeleted = data.getBooleanExtra("deleted", false);

            if (hasChanges) {
                listTasks = roomDb.getTaskDao().getTasksWithExecutions();
                adapter.setList(listTasks);
            }

            if (isDeleted) {
                Toast.makeText(this, getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
            }
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
            case R.id.navAdd:
                startAddActivity();
                break;
            case R.id.navDeleteAll:
                presenter.clearList(roomDb.getTaskDao());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startAddActivity() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, ADD_TASK_CODE);
    }


}
