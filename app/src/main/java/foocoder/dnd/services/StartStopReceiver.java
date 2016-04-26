package foocoder.dnd.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import foocoder.dnd.R;
import foocoder.dnd.presentation.App;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;

public class StartStopReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        App global = (App) App.getContext();
        SharedPreferenceUtil sp = global.getSharedPreferenceUtil();
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        global.set_isUnavailable(!global.get_isUnavailable());
        if (global.get_isUnavailable()) {
            sp.start(true);

            if (sp.isQuiet()) {
                if (global.getScheduleList().size() > 0) {
                    for (Schedule schedule : global.getScheduleList()) {
                        AlarmUtil.cancelOldAlarm(context,schedule);
                        sp.enable(false);
                        sp.setRunningId(-1);
                    }
                }
            }

            if(sp.isLaunched()){
                if(sp.isQuiet()){
//                    context.stopService(new Intent(context, TimeService.class));
                }
                if(sp.isVib()){
                    audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
            } else {
                audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }

            String note = context.getString(R.string.manual);
            Intent intent1 = new Intent(context, ListenerService.class);
            if (sp.isRecoverable()) {
//                context.startService(new Intent(context,StepService.class));
                note = context.getString(R.string.manual_auto_cancel);
            }
            context.startService(intent1.putExtra("note",note));
        } else {
            sp.start(false);
            context.stopService(new Intent(context, ListenerService.class));

            if (sp.isRecoverable()) {
//                context.stopService(new Intent(context,StepService.class));
            }
            if(sp.isLaunched()) {
                if(sp.isQuiet()){
//                    context.startService(new Intent(context, TimeService.class));
                }
            }
            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            if (sp.isQuiet()) {
                if (global.getScheduleList().size() > 0) {
                    for (Schedule schedule : global.getScheduleList()) {
                        AlarmUtil.startSchedule(context,schedule);
                    }
                }
            }
        }
    }
}
