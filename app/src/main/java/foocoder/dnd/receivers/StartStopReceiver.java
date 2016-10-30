package foocoder.dnd.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import java.util.List;

import javax.inject.Inject;

import foocoder.dnd.R;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.interactor.DefaultSubscriber;
import foocoder.dnd.domain.interactor.ScheduleCase;
import foocoder.dnd.presentation.App;
import foocoder.dnd.services.ListenerService;
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

public class StartStopReceiver extends BroadcastReceiver {

    @Inject
    ScheduleCase<List<Schedule>> getSchedules;

    @Inject
    SharedPreferenceUtil sp;

    @Inject
    AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getContext().getApplicationComponent().inject(this);
        if (!sp.isStarted()) {
            sp.start(true);

            if (sp.isQuiet()) {
                getSchedules.execute(new DefaultSubscriber<List<Schedule>>() {
                    @Override
                    public void onNext(List<Schedule> schedules) {
                        for (Schedule schedule : schedules) {
                            AlarmUtil.cancelOldAlarm(schedule);
                        }

                        sp.enable(false);
                        sp.setRunningId(-1);

                        String note = context.getString(R.string.manual);
                        Intent intent1 = new Intent(context, ListenerService.class);
                        context.startService(intent1.putExtra("note", note));
                    }
                });
            } else {
                String note = context.getString(R.string.manual);
                Intent intent1 = new Intent(context, ListenerService.class);
                context.startService(intent1.putExtra("note", note));
            }

            if (sp.isLaunched()) {
                if (sp.isVib()) {
                    audioManager.setRingerMode(RINGER_MODE_VIBRATE);
                } else {
                    audioManager.setRingerMode(RINGER_MODE_SILENT);
                }
            }
        } else {
            sp.start(false);
            context.startService(new Intent(context, ListenerService.class));

            audioManager.setRingerMode(RINGER_MODE_NORMAL);

            if (sp.isQuiet()) {
                getSchedules.execute(new DefaultSubscriber<List<Schedule>>() {
                    @Override
                    public void onNext(List<Schedule> schedules) {
                        for (Schedule schedule : schedules) {
                            AlarmUtil.startSchedule(schedule);
                        }
                    }
                });
            }
        }
    }
}
