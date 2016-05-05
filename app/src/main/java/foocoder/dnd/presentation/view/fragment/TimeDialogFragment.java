package foocoder.dnd.presentation.view.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import foocoder.dnd.R;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.presenter.TimePresenter;
import foocoder.dnd.presentation.view.TimeSelectView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by xuechi.
 * Time: 2016 四月 27 04:34
 * Project: dnd
 */
public class TimeDialogFragment extends DialogFragment implements TimeSelectView {

    public static final int CHECKED = 0xFF0099CC;

    public static final int UNCHECKED = 0xFFFFFFFF;

    @BindView(R.id.fromTime)
    TextView fromTime;

    @BindView(R.id.toTime)
    TextView toTime;

    @BindViews(value = {R.id.M, R.id.Tu, R.id.W, R.id.Th, R.id.F, R.id.Sa, R.id.Su})
    List<TextView> dayViews;

    @Inject
    TimePresenter timePresenter;
    @Nullable

    private Schedule schedule;

    private ButterKnife.Action<TextView> action;

    private CompositeSubscription subscriptions;

    @Nullable
    private OnScheduleSetListener listener;

    private TimePickerDialog timePicker;

    private DateTime dateTime = DateTime.now();

    public static TimeDialogFragment newInstance(@Nullable Schedule schedule) {
        Bundle args = new Bundle();
        args.putParcelable("schedule", schedule);
        TimeDialogFragment timeDialogFragment = new TimeDialogFragment();
        timeDialogFragment.setArguments(args);
        return timeDialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        schedule = getArguments().getParcelable("schedule");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getContext().getApplicationComponent().inject(this);

        subscriptions = new CompositeSubscription();
        action = (view, index) -> {
            Subscription subscription = RxView.clicks(view).subscribe(aVoid -> {
                int currentColor = view.getCurrentTextColor();
                switch (currentColor) {
                    case CHECKED:
                        view.setTextColor(UNCHECKED);
                        break;
                    case UNCHECKED:
                        view.setTextColor(CHECKED);
                        break;
                }
                timePresenter.modifyCheckedDay(index, currentColor & 1);
            });
            subscriptions.add(subscription);
            view.setTextColor(timePresenter.getCheckedDays().get(index) == 0 ? UNCHECKED : CHECKED);
        };
        timePresenter.setSchedule(schedule);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.timeselect, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ButterKnife.apply(dayViews, action);
        timePresenter.bindView(this);

        if (schedule != null) {
            fromTime.setText(schedule.getFrom());
            toTime.setText(schedule.getTo());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        subscriptions.clear();
    }

    @OnClick({R.id.toTime, R.id.fromTime})
    void onTimeClick(View aView) {
        int hour, minute;
        if (schedule != null) {
            String[] times = aView == fromTime ? schedule.getFrom().split(":") : schedule.getTo().split(":");
            hour = Integer.parseInt(times[0]);
            minute = Integer.parseInt(times[1]);
        } else {
            hour = dateTime.getHourOfDay();
            minute = dateTime.getMinuteOfHour();
        }
        timePicker = new TimePickerDialog(getActivity(), (view, hourOfDay, minute1) -> {
            TextView.class.cast(aView).setText(trans(hourOfDay) + ":" + trans(minute1));
            timePicker.dismiss();
        }, hour, minute, true);
        timePicker.show();
    }

    @OnClick(R.id.cancel)
    void onCancelClick() {
        dismiss();
    }

    @OnClick(R.id.ok)
    void onOkClick() {
        if (listener != null) {
            listener.call(timePresenter.modifySchedule(
                    fromTime.getText().toString(), toTime.getText().toString()));
        }
        dismiss();
    }

    @OnClick(R.id.del)
    void onDelClick() {
        if (schedule == null) {
            dismiss();
        } else {
            schedule.del = true;
            if (listener != null) {
                listener.call(schedule);
            }
            dismiss();
        }
    }

    public void setOnScheduleSetListener(@NonNull OnScheduleSetListener listener) {
        this.listener = listener;
    }

    private String trans(int time) {
        return (time < 10 && time >= 0 ? "0" : "") + time;
    }

    public interface OnScheduleSetListener {
        void call(Schedule schedule);
    }
}
