package foocoder.dnd.receivers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import javax.inject.Inject;

import foocoder.dnd.data.repository.ContactDataRepository;
import foocoder.dnd.presentation.App;
import foocoder.dnd.utils.SharedPreferenceUtil;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public static class IncomingListener extends PhoneStateListener {

        private static final IncomingListener incomingListener = new IncomingListener();

        @Inject
        ContactDataRepository contactDataRepository;

        @Inject
        SharedPreferenceUtil spUtil;

        @Inject
        AudioManager audioManager;

        private Ringtone r;

        private IncomingListener() {
            App.getContext().getApplicationComponent().inject(this);
        }

        public static void init(Application application) {
            TelephonyManager telephonyManager = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(incomingListener, LISTEN_CALL_STATE);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if ((spUtil.isStarted() || spUtil.isUsable()) && spUtil.isLaunched()) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (r != null && r.isPlaying()) {
                            r.stop();
                            r = null;
                            audioManager.setRingerMode(spUtil.isVib() ? RINGER_MODE_VIBRATE : RINGER_MODE_SILENT);
                        }
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Timber.d("offhook");
                        break;

                    case TelephonyManager.CALL_STATE_RINGING:
                        contactDataRepository.searchContact(incomingNumber, spUtil.isRepeated())
                                .subscribeOn(Schedulers.io())
                                .subscribe(aBoolean -> {
                                    if (aBoolean) {
                                        audioManager.setRingerMode(RINGER_MODE_NORMAL);
                                        Uri alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                        r = RingtoneManager.getRingtone(App.getContext(), alertUri);
                                        if (r != null && !r.isPlaying()) {
                                            r.play();
                                        }
                                    }
                                });
                        break;
                }
            }
        }
    }
}
