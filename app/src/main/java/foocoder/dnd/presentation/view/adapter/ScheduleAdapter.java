package foocoder.dnd.presentation.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import foocoder.dnd.R;
import foocoder.dnd.domain.Schedule;

import static foocoder.dnd.presentation.view.fragment.TimeDialogFragment.CHECKED;
import static foocoder.dnd.presentation.view.fragment.TimeDialogFragment.UNCHECKED;

public class ScheduleAdapter extends BaseAdapter {

    @Inject
    List<Schedule> schedules;

    private LayoutInflater inflater;

    private ButterKnife.Setter<TextView, List<Integer>> setter;

    @Inject
    public ScheduleAdapter(Activity context) {
        this.inflater = LayoutInflater.from(context);
        this.setter = (view, value, index) ->
                view.setTextColor(value.get(index) == 0 ? UNCHECKED : CHECKED);
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Schedule schedule = schedules.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.timelist, parent, false);
            ButterKnife.bind(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ButterKnife.apply(holder.dayViews, setter, schedule.days);

        holder.times.setText(schedule.from + "—" + schedule.to);

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.time_show)
        public TextView times;

        @BindViews(value = {R.id.M, R.id.Tu, R.id.W, R.id.Th, R.id.F, R.id.Sa, R.id.Su})
        List<TextView> dayViews;
    }
}
