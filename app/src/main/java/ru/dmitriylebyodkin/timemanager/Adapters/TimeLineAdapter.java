package ru.dmitriylebyodkin.timemanager.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.Calendar;
import java.util.List;

import ru.dmitriylebyodkin.timemanager.App;
import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.Execution;


/**
 * Created by dmitr on 09.03.2018.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {
    private static final String TAG = "myLogs";
    private Context context;
    private List<Execution> mData;

    public TimeLineAdapter(Context context, List<Execution> data) {
        this.context = context;
        this.mData = data;
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

    public void updateList(List<Execution> data) {
        this.mData = data;
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
        Execution execution = mData.get(position);
//        List<ExItem> listExItems = mData.get(position).getItems();
//        Task task = RoomDb.getInstance(context).getTaskDao().getTaskById(execution.getTaskId());

        Calendar calendar = Calendar.getInstance(App.getTimeZone());
        calendar.setTimeInMillis(execution.getCreatedAt()*1000L);

        holder.tvDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " + App.getMonthRByNumber(calendar.get(Calendar.MONTH)));

        if (execution.getTime() == 0) {
            holder.layoutTime.setVisibility(View.GONE);
        }

        if (position == 0) {
            holder.tvDate.setText(holder.tvDate.getText() + ".");
            holder.tvStart.setVisibility(View.VISIBLE);
        }

        int time = 0;

//        for (ExItem item: listExItems) {
//            time += item.getSeconds();
//            sumStatuses += item.getStatus();
//        }
//
//        int midStatus;
//
//        if (listExItems == null) {
//            midStatus = 0;
//        } else {
//            midStatus = sumStatuses / listExItems.size();
//        }

//        Calendar calendarTime = Calendar.getInstance(App.getTimeZone());
//        calendarTime.setTimeInMillis(time*1000L);

        holder.tvTime.setText(App.formatSeconds(execution.getTime()));
//        holder.tvStatus.setText(Execution.STATUSES[0]);
    }
}
