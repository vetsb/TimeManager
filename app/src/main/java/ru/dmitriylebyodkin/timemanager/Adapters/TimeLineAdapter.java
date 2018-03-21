package ru.dmitriylebyodkin.timemanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.Calendar;
import java.util.List;

import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.ExItem;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;
import ru.dmitriylebyodkin.timemanager.Room.Data.ExecutionWithItems;
import ru.dmitriylebyodkin.timemanager.Room.Data.Task;
import ru.dmitriylebyodkin.timemanager.Room.RoomDb;


/**
 * Created by dmitr on 09.03.2018.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {
    private static final String TAG = "myLogs";
    private Context context;
    private List<ExecutionWithItems> mData;
    private Intent taskIntent;

    public TimeLineAdapter(Context context, List<ExecutionWithItems> data, Intent taskData) {
        this.context = context;
        this.mData = data;
        this.taskIntent = taskData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvStart, tvTime, tvStatus;
        private TimelineView mTimelineView;
        private LinearLayout layoutTime;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvTime = itemView.findViewById(R.id.tvTime);
//            tvStatus = itemView.findViewById(R.id.tvStatus);
            layoutTime = itemView.findViewById(R.id.layoutTime);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeLine);
            mTimelineView.initLine(viewType);
        }
    }

    public void setList(List<ExecutionWithItems> data) {
        mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }

        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.task_timeline_item, null);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Execution execution = mData.get(position).getExecution();
        List<ExItem> exItemList = mData.get(position).getItems();

        Calendar calendar = Calendar.getInstance(App.getTimeZone());
        calendar.setTimeInMillis(execution.getCreatedAt()*1000L);

        holder.tvDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " + App.getMonthRByNumber(calendar.get(Calendar.MONTH)));

        if (position == 0) {
            holder.tvDate.setText(holder.tvDate.getText() + ".");
            holder.tvStart.setVisibility(View.VISIBLE);
        }

        int time = 0;

        for (ExItem item: exItemList) {
            time += item.getSeconds();
        }

        if (time == 0) {
            holder.tvTime.setText("Ещё не начиналось");
        } else {
            holder.tvTime.setText(App.formatSeconds(time));
        }

        if (taskIntent.getIntExtra("plan_time", 0) != 0) {
            if (position+1 == mData.size()) {
                Task task = new Task();
                task.setId(taskIntent.getIntExtra("id", 0));
                task.setUnit(taskIntent.getIntExtra("unit", 0));
                task.setPlanTime(taskIntent.getIntExtra("plan_time", 0));

                if (time >= task.getPlanSeconds()) {
                    holder.tvStart.setText(context.getString(R.string.finish));
                }
            }
        }
    }
}
