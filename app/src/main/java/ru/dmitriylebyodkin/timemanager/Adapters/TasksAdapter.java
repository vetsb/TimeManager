package ru.dmitriylebyodkin.timemanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.dmitriylebyodkin.timemanager.Activities.AddTaskActivity;
import ru.dmitriylebyodkin.timemanager.Activities.RunTaskActivity;
import ru.dmitriylebyodkin.timemanager.Activities.TaskActivity;
import ru.dmitriylebyodkin.timemanager.Activities.TasksActivity;
import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.Data.TaskWithExecutions;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;

/**
 * Created by dmitr on 08.03.2018.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private static final String TAG = "myLogs";
    private Context context;
    private List<TaskWithExecutions> mData = new ArrayList<>();
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private int openItem = -1;

    public TasksAdapter(Context context, List<TaskWithExecutions> data) {
        this.context = context;
        this.mData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout container;
        private TextView tvTitle, tvRunningTime, tvTimeLeft;
        private SwipeRevealLayout swipeRevealLayout;
        private LinearLayout layoutEdit, layoutDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRunningTime = itemView.findViewById(R.id.tvRunningTime);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayout);
            layoutEdit = itemView.findViewById(R.id.layoutEdit);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
        }
    }

    public void add(TaskWithExecutions taskWithExecutions) {
        if (getItemCount() == 0) {
            mData.add(taskWithExecutions);
        } else {
            mData.add(0, taskWithExecutions);
        }

        this.notifyDataSetChanged();
    }

    public void clear() {
        mData = new ArrayList<>();
        this.notifyDataSetChanged();
    }

    public void remove(int position) {
        mData.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, mData.size());
    }

    public void setList(List<TaskWithExecutions> data) {
        mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.task_item,
                        parent,
                        false
                ));
    }

//    public void saveStates(Bundle outState) {
//        viewBinderHelper.saveStates(outState);
//    }
//
//    public void restoreStates(Bundle inState) {
//        viewBinderHelper.restoreStates(inState);
//    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mData.get(position).getTask();
        List<Execution> listExecutions = mData.get(position).getExecutions();

        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(task.getId()));

        holder.layoutDelete.setOnClickListener(view -> {
            ((TasksActivity) context).deleteTask(position, task.getId());
            viewBinderHelper.closeLayout(String.valueOf(task.getId()));
        });

        holder.layoutEdit.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("id", task.getId());
            intent.putExtra("title", task.getTitle());
            intent.putExtra("unit", task.getUnit());
            intent.putExtra("label", task.getLabel());
            intent.putExtra("plan_time", task.getPlanTime());
            intent.putExtra("timestamp_start", task.getTimestampStart());
            intent.putExtra("timestamp_deadline", task.getTimestampDeadline());
            ((TasksActivity) context).startActivityForResult(intent, TasksActivity.TASK_CODE);
            viewBinderHelper.closeLayout(String.valueOf(task.getId()));
        });

        /**
         * Клик на весь элемент
         */
        holder.container.setOnClickListener(view -> {
            Log.d(TAG, "onBindViewHolder: " + task.getLabel());

            Intent intent = new Intent(context, TaskActivity.class);
//            intent.putExtra("position", position);
            intent.putExtra("id", task.getId());
            intent.putExtra("title", task.getTitle());
            intent.putExtra("unit", task.getUnit());
            intent.putExtra("label", task.getLabel());
            intent.putExtra("plan_time", task.getPlanTime());
            intent.putExtra("description", task.getDescription());
            intent.putExtra("difficulty", task.getDifficulty());
            intent.putExtra("timestamp_start", task.getTimestampStart());
            intent.putExtra("timestamp_deadline", task.getTimestampDeadline());
            ((TasksActivity) context).startActivityForResult(intent, TasksActivity.TASK_CODE);

        });
        holder.tvTitle.setText(task.getTitle());


        /**
         * Суммируется все время, в течение которого выполнялось занятие
         */
        int time = 0;

        for (Execution execution: listExecutions) {
            time += RoomDb.getInstance(context).getExItemDao().getSumTime(execution.getId());
        }

        if (time == 0) {
            holder.tvRunningTime.setText(context.getString(R.string.not_yet_started));
        } else {
            holder.tvRunningTime.setText(App.formatSeconds(time));
        }

        /**
         * Рассчитывается оставшееся время выполнения.
         * Если не указано время выполнения, то скрывать блок ОСТАЛОСЬ
         */
        Calendar calendar = Calendar.getInstance(App.getTimeZone());
        String timeLeftText = "";
        int planTime = task.getPlanTime();

        if (planTime == 0) {
            holder.tvTimeLeft.setText(context.getString(R.string.no_time_limit));
        } else {
            switch (task.getUnit()) {
                case 0:
                    if (time >= planTime) {
                        timeLeftText = context.getString(R.string.completed);
                    } else {
                        timeLeftText = App.formatSeconds(planTime-time);
                    }
                    break;
                case 1:
                    if (time >= planTime*60) {
                        timeLeftText = context.getString(R.string.completed);
                    } else {
                        timeLeftText = App.formatSeconds(planTime*60-time);
                    }
                    break;
                case 2:
                    if (time >= planTime*60*60) {
                        timeLeftText = context.getString(R.string.completed);
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

            holder.tvTimeLeft.setText(timeLeftText);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }

        return mData.size();
    }
}
