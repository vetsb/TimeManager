package ru.dmitriylebyodkin.timemanager.Activities;

import android.arch.persistence.room.Room;
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
import ru.dmitriylebyodkin.timemanager.Presenters.EditTaskPresenter;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;
import ru.dmitriylebyodkin.timemanager.Views.EditTaskView;

public class EditTaskActivity extends MvpAppCompatActivity implements EditTaskView {

    private static final String TAG = "myLogs";
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etPlanTime)
    EditText etPlanTime;
    @BindView(R.id.spinnerTimeUnit)
    Spinner spinnerTimeUnit;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvDeadline)
    TextView tvDeadline;

    @InjectPresenter
    EditTaskPresenter presenter;

    private Intent intent;
    private Task task;
    private final static String FRAG_TAG_DATE_PICKER = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ButterKnife.bind(this);
        intent = getIntent();
        task = new Task();

        String title = intent.getStringExtra("title");
        int planTime = intent.getIntExtra("plan_time", 0);

        task.setId(intent.getIntExtra("id", 0));

        if (title != null && title.length() > 0) {
            etTitle.setText(title);
            etTitle.setSelection(title.length());
        }

        if (planTime != 0) {
            etPlanTime.setText(String.valueOf(planTime));
        }

        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Task.UNITS);
        spinnerTimeUnit.setAdapter(spinnerAdapter);
        spinnerTimeUnit.setSelection(intent.getIntExtra("unit", 2));

        int tDayOfMonth, tMonthOfYear;
        int timestampStart = intent.getIntExtra("timestamp_start", 0);
        int timestampDeadline = intent.getIntExtra("timestamp_deadline", 0);

        Calendar calendar = Calendar.getInstance(App.getTimeZone());

        if (timestampStart != 0) {
            calendar.setTimeInMillis(timestampStart);

            tDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            tMonthOfYear = calendar.get(Calendar.MONTH);

            tvDate.setText(tDayOfMonth + " " + App.getMonthRByNumber(tMonthOfYear));
        }

        if (timestampDeadline != 0) {
            calendar.setTimeInMillis(timestampDeadline);

            tDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            tMonthOfYear = calendar.get(Calendar.MONTH);

            tvDeadline.setText(tDayOfMonth + " " + App.getMonthRByNumber(tMonthOfYear));
        }

        tvDate.setOnClickListener(view -> {
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        intent.putExtra("has_changes", false);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navSave:
                task.setTitle(getTaskTitle());
                task.setUnit(getTaskUnit());
                task.setPlanTime(getTaskPlanTime());

                presenter.editTask(RoomDb.getInstance(this).getTaskDao(), task);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finishActivity() {
        intent.putExtra("title", getTaskTitle());
        intent.putExtra("has_changes", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}
