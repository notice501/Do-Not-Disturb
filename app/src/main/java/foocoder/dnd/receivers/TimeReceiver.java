package foocoder.dnd.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.interactor.DefaultSubscriber;
import foocoder.dnd.domain.interactor.ScheduleCase;
import foocoder.dnd.presentation.App;
import foocoder.dnd.services.ListenerService;
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;

import static foocoder.dnd.presentation.App.AUTO_TIME_SCHEDULE;

public class TimeReceiver extends BroadcastReceiver {

    @Inject
    AlarmManager alarmManager;

    @Inject
    AudioManager audioManager;

    @Inject
    SharedPreferenceUtil spUtil;

    @Inject
    ScheduleCase<List<Schedule>> getSchedules;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getContext().getApplicationComponent().inject(this);
        Bundle extras = intent.getExtras();

        if (AUTO_TIME_SCHEDULE.equals(intent.getAction())) {

                getSchedules.execute(new DefaultSubscriber<List<Schedule>>() {
                    @Override
                    public void onNext(List<Schedule> result) {
                        AlarmUtil.start(result);
                    }
                });

        } else {
            if (audioManager.getRingerMode() > 1) {
                if (spUtil.isUsable()) {
                    spUtil.setRunningId(-1);
                    spUtil.enable(false);
                    context.startService(new Intent(context, ListenerService.class));
                }
            }
        }
    }
}
