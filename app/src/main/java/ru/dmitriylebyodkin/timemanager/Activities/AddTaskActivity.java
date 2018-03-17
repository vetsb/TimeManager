package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.Presenters.AddTaskPresenter;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.AddTaskView;

public class AddTaskActivity extends MvpAppCompatActivity implements AddTaskView {

    private static final String TAG = "myLogs";
    private static final String FLAG_DEADLINES_DIALOG = "deadlines_dialog";

    @BindView(R.id.layoutDescription)
    LinearLayout layoutDescription;
    @BindView(R.id.layoutDifficulty)
    LinearLayout layoutDifficulty;
    @BindView(R.id.layoutDateStart)
    LinearLayout layoutDateStart;
    @BindView(R.id.layoutDeadline)
    LinearLayout layoutDeadline;
    @BindView(R.id.layoutPlanTime)
    LinearLayout layoutPlanTime;

    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.tvDifficulty)
    TextView tvDifficulty;
    @BindView(R.id.tvDateStart)
    TextView tvDateStart;
    @BindView(R.id.tvDeadline)
    TextView tvDeadline;
    @BindView(R.id.tvPlanTime)
    TextView tvPlanTime;
    @BindView(R.id.tvDelete)
    TextView tvDelete;

    @InjectPresenter
    AddTaskPresenter presenter;

    private Task task;
    private RoomDb roomDb;
    private MenuItem gMenuItem;
    private Intent intent;
    private boolean isEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ButterKnife.bind(this);
        intent = getIntent();

        roomDb = RoomDb.getInstance(this);
        task = new Task();

        /**
         * Добавление текста в поля дат (изменение занятия) и показ кнопки удаления
         */
        if (intent.getExtras() != null) {
            isEditable = true;
            tvDelete.setVisibility(View.VISIBLE);
        }

        layoutDescription.setOnClickListener(view -> presenter.showDescriptionDialog());
        layoutDifficulty.setOnClickListener(view -> presenter.showDifficultyDialog());
        layoutDateStart.setOnClickListener(view -> presenter.showDeadlinesDialog(true));
        layoutDeadline.setOnClickListener(view -> presenter.showDeadlinesDialog(false));
        layoutPlanTime.setOnClickListener(view -> presenter.showPlanTimeDialog());

        /**
         * Если изменяется занятие, то записать в поля значения, изменить название экрана и поменять текст пункта меню в тулбаре
         */
        if (isEditable) {
            task.setId(intent.getIntExtra("id", 0));

            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            int difficulty = intent.getIntExtra("difficulty", 0);
            int timestampStart = intent.getIntExtra("timestamp_start", 0);
            int timestampDeadline = intent.getIntExtra("timestamp_deadline", 0);
            int planTime = intent.getIntExtra("plan_time", 0);
            int unit = intent.getIntExtra("unit", 0);

            if (title != null && title.length() > 0) {
                task.setTitle(intent.getStringExtra("title"));
                etTitle.setText(title);
                etTitle.setSelection(title.length());
            }

            if (description != null && description.length() > 0) {
                presenter.updateDescription(description);
            }

            if (difficulty != 0) {
                presenter.updateDifficulty(difficulty);
            }

            if (timestampStart != 0) {
                presenter.updateDateStart(timestampStart*1000L);
            }

            if (timestampDeadline != 0) {
                presenter.updateDeadline(timestampDeadline*1000L);
            }

            if (planTime != 0) {
                presenter.updatePlanTime(planTime, unit);
            }

            setTitle(R.string.edit_task_activity_title);
        }

        tvDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.deleting_task));
            alertDialog.setMessage(R.string.deleting_task_dialog_text);
            alertDialog.setPositiveButton(getString(R.string.yes), (dialog, i) -> {
                dialog.dismiss();

                presenter.delete(roomDb.getTaskDao(), intent.getIntExtra("id", 0));
                intent.putExtra("deleted", true);

                setResult(RESULT_OK, intent);
                finish();
            });
            alertDialog.setNeutralButton(getString(R.string.cancel), null);
            alertDialog.create().show();
        });

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String getTaskTitle() {
        return etTitle.getText().toString();
    }

    public Task getTask() {
        task.setTitle(getTaskTitle());

        return task;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_task_menu, menu);
        gMenuItem = menu.getItem(0);

        if (isEditable) {
            gMenuItem.setTitle(getString(R.string.save));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navAdd:
                if (isEditable) {
                    presenter.updateTask(roomDb.getTaskDao(), getTask());
                } else {
                    presenter.addTask(roomDb, getTask());
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDescriptionDialog() {
        View layoutView = getLayoutInflater().inflate(R.layout.dialog_add_description, null);
        EditText etDescription = layoutView.findViewById(R.id.etDescription);

        String taskDescription = task.getDescription();

        if (taskDescription != null && taskDescription != "") {
            etDescription.setText(taskDescription);
            etDescription.setSelection(taskDescription.length());
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.adding_description);
        alertDialog.setPositiveButton(R.string.add, (dialogInterface, i) -> {
            String description = etDescription.getText().toString().trim();

            if (!description.equals("")) {
                presenter.updateDescription(description);
            }

            dialogInterface.dismiss();
        });
        alertDialog.setNeutralButton(R.string.cancel, null);
        alertDialog.setView(layoutView);
        alertDialog.create().show();
    }

    @Override
    public void updateDescription(String text) {
        task.setDescription(text);
        tvDescription.setText(getString(R.string.description) + " " + text);
    }

    @Override
    public void showDifficultyDialog() {
        int checkedItem = 0;

        if (task.getDifficulty() != 0) {
            checkedItem = task.getDifficulty();
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.adding_description);
        alertDialog.setSingleChoiceItems(Task.DIFFICULTIES, checkedItem, null);
        alertDialog.setPositiveButton(R.string.add, (dialogInterface, i) -> {
            presenter.updateDifficulty(((AlertDialog) dialogInterface).getListView().getCheckedItemPosition());
        });
        alertDialog.setNeutralButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    @Override
    public void updateDifficulty(int i) {
        task.setDifficulty(i);
        tvDifficulty.setText(getString(R.string.difficulty_level) + ": " + Task.DIFFICULTIES[i]);
    }

    @Override
    public void showDeadlinesDialog(boolean isStart) {
        Calendar calendar = Calendar.getInstance(App.getTimeZone());
        calendar.setTimeInMillis(System.currentTimeMillis());

        int year, month, day;

        if (isStart) {
            if (task.getTimestampStart() != 0) {
                calendar.setTimeInMillis(task.getTimestampStart()*1000L);
            }
        } else {
            if (task.getTimestampDeadline() != 0) {
                calendar.setTimeInMillis(task.getTimestampDeadline()*1000L);
            }
        }

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerFragmentDialog dialog = DatePickerFragmentDialog.newInstance(new DatePickerFragmentDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerFragmentDialog view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance(App.getTimeZone());
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (isStart) {
                    presenter.updateDateStart(calendar.getTimeInMillis());
                } else {
                    presenter.updateDeadline(calendar.getTimeInMillis());
                }
            }
        }, year, month, day);

        dialog.show(getSupportFragmentManager(), FLAG_DEADLINES_DIALOG);
    }

    @Override
    public void updateDateStart(long millis) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        sdfDate.setTimeZone(App.getTimeZone());

        task.setTimestampStart((int) (millis/1000L));
        tvDateStart.setText(getString(R.string.start2) + ": " + sdfDate.format(new Date(millis)));
    }

    @Override
    public void updateDeadline(long millis) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        sdfDate.setTimeZone(App.getTimeZone());

        task.setTimestampDeadline((int) (millis/1000L));
        tvDeadline.setText(getString(R.string.end) + ": " + sdfDate.format(new Date(millis)));
    }

    @Override
    public void showPlanTimeDialog() {
        View layoutView = getLayoutInflater().inflate(R.layout.dialog_add_plantime, null);
        EditText etPlanTime = layoutView.findViewById(R.id.etPlanTime);
        Spinner spinnerTimeUnit = layoutView.findViewById(R.id.spinnerTimeUnit);

        int time = task.getPlanTime();
        int selectedUnit = 2;

        if (time != 0) {
            etPlanTime.setText(String.valueOf(time));
        }

        if (task.getUnit() != 0) {
            selectedUnit = task.getUnit();
        }

        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Task.UNITS);
        spinnerTimeUnit.setAdapter(spinnerAdapter);
        spinnerTimeUnit.setSelection(selectedUnit);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.plan_time);
        alertDialog.setPositiveButton(R.string.add, (dialogInterface, i) -> {
            presenter.updatePlanTime(Integer.parseInt(etPlanTime.getText().toString().trim()), (int) spinnerTimeUnit.getSelectedItemId());
            dialogInterface.dismiss();
        });
        alertDialog.setNeutralButton(R.string.cancel, null);
        alertDialog.setView(layoutView);
        alertDialog.create().show();
    }

    @Override
    public void updatePlanTime(int time, int unit) {
        task.setPlanTime(time);
        task.setUnit(unit);

        String text = getString(R.string.plan_time);

        if (time != 0) {
            text = String.valueOf(time) + " ";

            switch (task.getUnit()) {
                case 0:
                    text += App.formatWord(time, new String[] {"секунда", "секунды", "секунд"});
                    break;
                case 1:
                    text += App.formatWord(time, new String[] {"минута", "минуты", "минут"});
                    break;
                case 2:
                    text += App.formatWord(time, new String[] {"час", "часа", "часов"});
            }
        }

        tvPlanTime.setText(text);
    }

    @Override
    public void finishEdit() {
        intent.putExtra("title", task.getTitle());
        intent.putExtra("plan_time", task.getPlanTime());
        intent.putExtra("unit", task.getUnit());
        intent.putExtra("description", task.getDescription());
        intent.putExtra("difficulty", task.getDifficulty());
        intent.putExtra("timestamp_start", task.getTimestampStart());
        intent.putExtra("timestamp_deadline", task.getTimestampDeadline());
        intent.putExtra("has_changes", true);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finishAdd(long taskId) {
        Intent intent = new Intent();
        intent.putExtra("id", (int) taskId);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("plan_time", task.getPlanTime());
        intent.putExtra("unit", task.getUnit());

        setResult(RESULT_OK, intent);
        finish();
    }
}
