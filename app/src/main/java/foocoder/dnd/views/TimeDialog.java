package foocoder.dnd.views;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import foocoder.dnd.R;
import foocoder.dnd.domain.Schedule;

public class TimeDialog extends Dialog {

    public static final int checked = -16737844;
//    public static final int unchecked = -5592406;
    public static final int unchecked = -1;
    private TimePickerDialog tpdFrom;
    private TimePickerDialog tpdTo;
    private TextView fromTime;
    private TextView toTime;
    private TextView monday;
    private TextView tuesday;
    private TextView wednesday;
    private TextView thursday;
    private TextView friday;
    private TextView saturday;
    private TextView sunday;
    private Context context;
    private OnCustomDialogListener listener;
    private Calendar cal;
    private List<Integer> isChecked;

    private Schedule sch;

    public TimeDialog(Context context) {
        super(context);
        this.context = context;
    }

    public TimeDialog(Context context, OnCustomDialogListener cancelListener) {
        super(context,R.style.TimeDialog);
        this.context = context;
        this.listener = cancelListener;
    }

    public TimeDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public TimeDialog(Schedule sch, Context context, OnCustomDialogListener cancelListener) {
        this(context, cancelListener);
        this.sch = sch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeselect);

        isChecked = new ArrayList<Integer>(7);
        isChecked.add(0);
        isChecked.add(0);
        isChecked.add(0);
        isChecked.add(0);
        isChecked.add(0);
        isChecked.add(0);
        isChecked.add(0);

        cal = Calendar.getInstance();
        fromTime = (TextView) findViewById(R.id.fromTime);
        toTime = (TextView) findViewById(R.id.toTime);
        if (sch != null) {
            isChecked = sch.getChecked();
            fromTime.setText(sch.getFrom());
            toTime.setText(sch.getTo());
        }
        fromTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tpdFrom == null) {

                    int fromHour;
                    int fromMinute;

                    if (sch != null) {
                        String[] froms = sch.getFrom().split(":");
                        fromHour = Integer.parseInt(froms[0]);
                        fromMinute = Integer.parseInt(froms[1]);
                    } else {
                        fromHour = cal.get(Calendar.HOUR_OF_DAY);
                        fromMinute = cal.get(Calendar.MINUTE);
                    }
                    tpdFrom = new TimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    fromTime.setText(trans(hourOfDay) + ":" + trans(minute));
                                    tpdFrom.dismiss();
                                }
                            }, fromHour, fromMinute, DateFormat.is24HourFormat(context));
                    tpdFrom.show();
                } else {
                    tpdFrom.show();
                }
            }
        });

        toTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tpdTo == null) {

                    int toHour;
                    int toMinute;

                    if (sch != null) {
                        String[] tos = sch.getTo().split(":");
                        toHour = Integer.parseInt(tos[0]);
                        toMinute = Integer.parseInt(tos[1]);
                    } else {
                        toHour = cal.get(Calendar.HOUR_OF_DAY);
                        toMinute = cal.get(Calendar.MINUTE);
                    }
                    tpdTo = new TimePickerDialog(getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    toTime.setText(trans(hourOfDay) + ":" + trans(minute));
                                    tpdTo.dismiss();
                                }
                            }, toHour, toMinute, DateFormat.is24HourFormat(context));
                    tpdTo.show();
                } else {
                    tpdTo.show();
                }
            }
        });

        findViewById(R.id.over).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(sch == null){
                    sch = new Schedule(fromTime.getText().toString(),
                            toTime.getText().toString(), isChecked);
                } else {
                    sch.setFrom(fromTime.getText().toString());
                    sch.setTo(toTime.getText().toString());
                    sch.setChecked(isChecked);
                }

                listener.back(sch);
                cancel();
            }
        });

        findViewById(R.id.del).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sch == null) {
                    dismiss();
                } else {
                    sch.setDel(true);
                    listener.back(sch);
                    cancel();
                }
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        initWeeks();

    }

    private int check(int color) {
        if (color == checked) {
            return 0;
        } else {
            return 1;
        }
    }

    private String trans(int time) {
        if (time < 10 && time >= 0) {
            return "0"+time;
        }
        return ""+time;
    }

    private void initWeeks() {
        monday = (TextView) findViewById(R.id.M);
        monday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentColor = ((TextView) v).getCurrentTextColor();
                switch (currentColor) {
                    case checked:
                        monday.setTextColor(context.getResources().getColor(
                                android.R.color.white));
                        break;
                    case unchecked:
                        monday.setTextColor(context.getResources().getColor(
                                android.R.color.holo_blue_dark));
                        break;
                }
                isChecked.set(0, check(currentColor));
            }
        });

        tuesday = (TextView) findViewById(R.id.Tu);
        tuesday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentColor = ((TextView) v).getCurrentTextColor();
                switch (currentColor) {
                    case checked:
                        tuesday.setTextColor(context.getResources().getColor(
                                android.R.color.white));
                        break;
                    case unchecked:
                        tuesday.setTextColor(context.getResources().getColor(
                                android.R.color.holo_blue_dark));
                        break;
                }
                isChecked.set(1, check(currentColor));
            }
        });

        wednesday = (TextView) findViewById(R.id.W);
        wednesday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentColor = ((TextView) v).getCurrentTextColor();
                switch (currentColor) {
                    case checked:
                        wednesday.setTextColor(context.getResources().getColor(
                                android.R.color.white));
                        break;
                    case unchecked:
                        wednesday.setTextColor(context.getResources().getColor(
                                android.R.color.holo_blue_dark));
                        break;
                }
                isChecked.set(2, check(currentColor));
            }
        });

        thursday = (TextView) findViewById(R.id.Th);
        thursday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentColor = ((TextView) v).getCurrentTextColor();
                switch (currentColor) {
                    case checked:
                        thursday.setTextColor(context.getResources().getColor(
                                android.R.color.white));
                        break;
                    case unchecked:
                        thursday.setTextColor(context.getResources().getColor(
                                android.R.color.holo_blue_dark));
                        break;
                }
                isChecked.set(3, check(currentColor));
            }
        });

        friday = (TextView) findViewById(R.id.F);
        friday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentColor = ((TextView) v).getCurrentTextColor();
                switch (currentColor) {
                    case checked:
                        friday.setTextColor(context.getResources().getColor(
                                android.R.color.white));
                        break;
                    case unchecked:
                        friday.setTextColor(context.getResources().getColor(
                                android.R.color.holo_blue_dark));
                        break;
                }
                isChecked.set(4, check(currentColor));
            }
        });

        saturday = (TextView) findViewById(R.id.Sa);
        saturday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentColor = ((TextView) v).getCurrentTextColor();
                Log.i("color",currentColor+"");
                switch (currentColor) {
                    case checked:
                        saturday.setTextColor(context.getResources().getColor(
                                android.R.color.white));
                        break;
                    case unchecked:
                        saturday.setTextColor(context.getResources().getColor(
                                android.R.color.holo_blue_dark));
                        break;
                }
                isChecked.set(5, check(currentColor));
            }
        });

        sunday = (TextView) findViewById(R.id.Su);
        sunday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentColor = ((TextView) v).getCurrentTextColor();
                switch (currentColor) {
                    case checked:
                        sunday.setTextColor(context.getResources().getColor(
                                android.R.color.white));
                        break;
                    case unchecked:
                        sunday.setTextColor(context.getResources().getColor(
                                android.R.color.holo_blue_dark));
                        break;
                }
                isChecked.set(6, check(currentColor));
            }
        });

        if (sch != null) {
            monday.setTextColor(sch.getChecked().get(0) == 0 ? unchecked : checked);
            tuesday.setTextColor(sch.getChecked().get(1) == 0 ? unchecked : checked);
            wednesday.setTextColor(sch.getChecked().get(2) == 0 ? unchecked : checked);
            thursday.setTextColor(sch.getChecked().get(3) == 0 ? unchecked : checked);
            friday.setTextColor(sch.getChecked().get(4) == 0 ? unchecked : checked);
            saturday.setTextColor(sch.getChecked().get(5) == 0 ? unchecked : checked);
            sunday.setTextColor(sch.getChecked().get(6) == 0 ? unchecked : checked);
        }
    }

    public interface OnCustomDialogListener {
        public void back(Schedule sch);
    }
}
