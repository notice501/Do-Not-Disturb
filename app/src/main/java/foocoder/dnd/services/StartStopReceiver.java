package foocoder.dnd.services;

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
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

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
        CompositeSubscription subscriptions = new CompositeSubscription();
        if (!sp.isStarted()) {
            sp.start(true);

            if (sp.isQuiet()) {
                Subscription subscription = getSchedules.execute(new DefaultSubscriber<List<Schedule>>() {
                    @Override
                    public void onNext(List<Schedule> schedules) {
                        for (Schedule schedule : schedules) {
                            AlarmUtil.cancelOldAlarm(schedule);
                        }
                        sp.enable(false);
                        sp.setRunningId(-1);
                    }
                });
                subscriptions.add(subscription);
            }

            String note = context.getString(R.string.manual);
            Intent intent1 = new Intent(context, ListenerService.class);
            context.startService(intent1.putExtra("note", note));
        } else {
            sp.start(false);
            context.stopService(new Intent(context, ListenerService.class));
        }

//        if (sp.isLaunched()) {
//            if (sp.isVib()) {
//                audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//            }
//        } else {
//            audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        }


//        } else {
//        sp.start(false);
//
//        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

//            if (sp.isQuiet()) {
//                if (global.getScheduleList().size() > 0) {
//                    for (Schedule schedule : global.getScheduleList()) {
//                        AlarmUtil.startSchedule(context,schedule);
//                    }
//                }
//            }
//        }
        subscriptions.clear();
    }
}
