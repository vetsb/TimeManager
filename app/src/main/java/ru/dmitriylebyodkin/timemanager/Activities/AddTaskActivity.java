package ru.dmitriylebyodkin.timemanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import java.util.Calendar;

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
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.etPlanTime)
    EditText etPlanTime;
    @BindView(R.id.spinnerTimeUnit)
    Spinner spinnerTimeUnit;
    @BindView(R.id.tvDeadline)
    TextView tvDeadline;
//    @BindView(R.id.btnAdd)
//    Button btnAdd;

    @InjectPresenter
    AddTaskPresenter presenter;

    private Task task;
    private final static String FRAG_TAG_DATE_PICKER = "tag";
    private RoomDb roomDb;
    private MenuItem gMenuItem;
    private long taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ButterKnife.bind(this);

        roomDb = RoomDb.getInstance(this);
        task = new Task();

        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Task.UNITS);
        spinnerTimeUnit.setAdapter(spinnerAdapter);
        spinnerTimeUnit.setSelection(2);

        tvDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance(App.getTimeZone());

            int finalYear, finalMonth, finalDay;

            if (task.getTimestampStart() == 0) {
                calendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                calendar.setTimeInMillis(task.getTimestampStart()*1000L);
            }

            finalYear = calendar.get(Calendar.YEAR);
            finalMonth = calendar.get(Calendar.MONTH);
            finalDay = calendar.get(Calendar.DAY_OF_MONTH);

            CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                    .setOnDateSetListener((dialog, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        task.setTimestampStart((int) (calendar.getTimeInMillis()/1000L));
                        tvDate.setText(dayOfMonth + " " + App.getMonthRByNumber(monthOfYear));
                    })
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setPreselectedDate(finalYear, finalMonth, finalDay)
                    .setDoneText(getString(R.string.next))
                    .setCancelText(getString(R.string.cancel));
            cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
        });

        tvDeadline.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance(App.getTimeZone());

            int finalYear, finalMonth, finalDay;

            if (task.getTimestampDeadline() == 0) {
                calendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                calendar.setTimeInMillis(task.getTimestampDeadline()*1000L);
            }

            finalYear = calendar.get(Calendar.YEAR);
            finalMonth = calendar.get(Calendar.MONTH);
            finalDay = calendar.get(Calendar.DAY_OF_MONTH);

            CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                    .setOnDateSetListener((dialog, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        task.setTimestampDeadline((int) (calendar.getTimeInMillis()/1000L));
                        tvDeadline.setText(dayOfMonth + " " + App.getMonthRByNumber(monthOfYear));
                    })
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setPreselectedDate(finalYear, finalMonth, finalDay)
                    .setDoneText(getString(R.string.next))
                    .setCancelText(getString(R.string.cancel));
            cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
        });

//        btnAdd.setOnClickListener(view -> {
//            presenter.addTask(roomDb, getTask());
//
//            setResult(RESULT_OK);
//            finish();
//        });

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String getTaskTitle() {
        return etTitle.getText().toString();
    }

    public int getTaskUnit() {
        return spinnerTimeUnit.getSelectedItemPosition();
    }

    public int getTaskPlanTime() {
        String text = etPlanTime.getText().toString();

        if (text.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(text);
        }
    }

    public Task getTask() {
        Task task = new Task();
        task.setTitle(getTaskTitle());
        task.setUnit(getTaskUnit());
        task.setPlanTime(getTaskPlanTime());

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navAdd:
                presenter.addTask(roomDb, getTask());

                Intent intent = new Intent();
                intent.putExtra("id", taskId);
                intent.putExtra("title", getTaskTitle());
                intent.putExtra("plan_time", getTaskPlanTime());
                intent.putExtra("unit", getTaskUnit());

                setResult(RESULT_OK, intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
}
