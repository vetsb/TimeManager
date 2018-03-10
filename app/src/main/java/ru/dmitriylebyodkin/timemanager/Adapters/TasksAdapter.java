package ru.dmitriylebyodkin.timemanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import ru.dmitriylebyodkin.timemanager.Activities.RunTaskActivity;
import ru.dmitriylebyodkin.timemanager.Activities.TaskActivity;
import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.Data.TaskWithExecutions;

/**
 * Created by dmitr on 08.03.2018.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private static final String TAG = "myLogs";
    private Context context;
    private List<TaskWithExecutions> mData;

    public TasksAdapter(Context context, List<TaskWithExecutions> data) {
        this.context = context;
        this.mData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout container, layoutTimeLeft;
        private TextView tvTitle, tvRunningTime, tvTimeLeft, tvRun, tvReadMore;
        private View viewLine;

        public ViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            layoutTimeLeft = itemView.findViewById(R.id.layoutTimeLeft);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRunningTime = itemView.findViewById(R.id.tvRunningTime);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            tvRun = itemView.findViewById(R.id.tvRun);
            tvReadMore = itemView.findViewById(R.id.tvReadMore);
            viewLine = itemView.findViewById(R.id.viewLine);
        }
    }

    public void updateList(List<TaskWithExecutions> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.task_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = mData.get(position).getTask();
        List<Execution> listExecutions = mData.get(position).getExecutions();

        /**
         * Клик на весь элемент
         */
        holder.container.setOnClickListener(view -> {
            Intent intent = new Intent(context, TaskActivity.class);
            intent.putExtra("id", task.getId());
            intent.putExtra("title", task.getTitle());
            context.startActivity(intent);
        });
        holder.tvTitle.setText(task.getTitle());


        /**
         * Суммируется все время, в течение которого выполнялось занятие
         */
        int time = 0;

        for (Execution execution: listExecutions) {
            time += execution.getTime();
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
            holder.layoutTimeLeft.setVisibility(View.GONE);
        } else {
            switch (task.getUnit()) {
                case 0:
                    timeLeftText = App.formatSeconds(planTime-time);
                    break;
                case 1:
                    timeLeftText = App.formatSeconds(planTime*60-time);
                    break;
                case 2:
                    calendar.setTimeInMillis((planTime*60*60-time)*1000L);

                    int calendarHours = calendar.get(Calendar.HOUR);
                    int calendarMinutes = calendar.get(Calendar.MINUTE);

                    if (calendarHours == 0) {
                        timeLeftText = calendarMinutes + " " + App.formatWord(calendarMinutes, new String[] {"минута", "минуты", "минут"});
                    } else {
                        timeLeftText = calendarHours + " " + App.formatWord(calendarHours, new String[] {"час", "часа", "часов"});

                        if (calendarMinutes != 0) {
                            timeLeftText += " " + calendarMinutes + " " + App.formatWord(calendarMinutes, new String[] {"минута", "минуты", "минут"});
                        }
                    }
                    break;
            }

            holder.tvTimeLeft.setText(timeLeftText);
        }

        /**
         * Клик на кнопку ВЫПОЛНЯТЬ СЕЙЧАС
         */
        holder.tvRun.setOnClickListener(view -> {
            Intent intent = new Intent(context, RunTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            context.startActivity(intent);
        });

        /**
         * Клик на кнопку ПОДРОБНЕЕ
         */
        holder.tvReadMore.setOnClickListener(view -> {
            Intent intent = new Intent(context, TaskActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("id", task.getId());
            intent.putExtra("title", task.getTitle());
            context.startActivity(intent);
        });


        /**
         * У последнего элемента удаляется нижняя линия
         */
        if (position+1 == mData.size()) {
            holder.viewLine.setVisibility(View.GONE);
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
