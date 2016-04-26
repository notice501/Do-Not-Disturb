package foocoder.dnd.presentation.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import foocoder.dnd.R;
import foocoder.dnd.views.TimeDialog;
import foocoder.dnd.domain.Schedule;

public class ScheduleAdapter extends BaseAdapter {

    private List<Schedule> schs;

    private LayoutInflater inflater;

    public ScheduleAdapter(Context context, List<Schedule> schs) {
        this.inflater = LayoutInflater.from(context);
        this.schs = schs;
    }

    @Override
    public int getCount() {
        return schs.size();
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
        Schedule sch = schs.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.timelist, null);
            holder.times = (TextView) convertView.findViewById(R.id.time_show);
            holder.mon = (TextView) convertView.findViewById(R.id.M);
            holder.tue = (TextView) convertView.findViewById(R.id.Tu);
            holder.wed = (TextView) convertView.findViewById(R.id.W);
            holder.thu = (TextView) convertView.findViewById(R.id.Th);
            holder.fri = (TextView) convertView.findViewById(R.id.F);
            holder.sat = (TextView) convertView.findViewById(R.id.Sa);
            holder.sun = (TextView) convertView.findViewById(R.id.Su);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.times.setText(sch.getFrom() + "—" + sch.getTo());
        holder.mon.setTextColor(sch.getChecked().get(0) == 0 ? TimeDialog.unchecked : TimeDialog.checked);
        holder.tue.setTextColor(sch.getChecked().get(1) == 0 ? TimeDialog.unchecked : TimeDialog.checked);
        holder.wed.setTextColor(sch.getChecked().get(2) == 0 ? TimeDialog.unchecked : TimeDialog.checked);
        holder.thu.setTextColor(sch.getChecked().get(3) == 0 ? TimeDialog.unchecked : TimeDialog.checked);
        holder.fri.setTextColor(sch.getChecked().get(4) == 0 ? TimeDialog.unchecked : TimeDialog.checked);
        holder.sat.setTextColor(sch.getChecked().get(5) == 0 ? TimeDialog.unchecked : TimeDialog.checked);
        holder.sun.setTextColor(sch.getChecked().get(6) == 0 ? TimeDialog.unchecked : TimeDialog.checked);

        return convertView;
    }

    class ViewHolder {
        public TextView times;
        public TextView mon;
        public TextView tue;
        public TextView wed;
        public TextView thu;
        public TextView fri;
        public TextView sat;
        public TextView sun;
    }
}
