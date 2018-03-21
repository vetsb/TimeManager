package ru.dmitriylebyodkin.timemanager.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.dmitriylebyodkin.timemanager.R;
import ru.dmitriylebyodkin.timemanager.Room.Data.Label;

/**
 * Created by dmitr on 19.03.2018.
 */

public class LabelAdapter extends BaseAdapter {
    private static final String TAG = "myLogs";
    private Context context;
    private List<Label> data;
    private LayoutInflater layoutInflater;

    public LabelAdapter(Context context, List<Label> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }

        return data.size();
    }

    @Override
    public Label getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getId();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Label label = getItem(i);
        view = layoutInflater.inflate(R.layout.label_item, null);

        ImageView image = view.findViewById(R.id.image);
        TextView text = view.findViewById(R.id.text);

        if (label.getImageId() == 0) {
            image.setImageResource(R.drawable.undefinded_label);
        } else {
            image.setImageResource(label.getImageId());
        }

        text.setText(label.getTitle());

        return view;
    }

    public void add(Label label) {
        data.add(label);
        this.notifyDataSetChanged();
    }
}
