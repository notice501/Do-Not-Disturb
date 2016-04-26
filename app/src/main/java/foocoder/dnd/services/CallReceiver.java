package foocoder.dnd.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import foocoder.dnd.presentation.App;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.utils.SharedPreferenceUtil;

public class CallReceiver extends BroadcastReceiver {

    private App global;

    @Override
    public void onReceive(Context context, Intent intent) {

        global = (App) ((App.getContext()));
        SharedPreferenceUtil sp = global.getSharedPreferenceUtil();
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        TelephonyManager telMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if((sp.isStarted() && sp.isLaunched()) || sp.isUsable()){
            switch (telMgr.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String incomingNumber = intent.getStringExtra("incoming_number");
                    Log.d("call",isWhiteContact(incomingNumber)+"");
                    if (isWhiteContact(incomingNumber)) {
                        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    } else {
                        if (sp.isRepeated()) {
                            if (global.hasNumber(incomingNumber)) {
                                audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            }
//                            else {
//                                global.saveNumber(incomingNumber,System.currentTimeMillis());
//                            }
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
//                    if (sp.isRepeated()) {
                      audio.setRingerMode(sp.isVib() ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_SILENT);
//                    }
                    break;
            }
        }
    }

    private boolean isWhiteContact(String number) {
        boolean flag = false;
        for(Contact contact : global.getContacts()){
            if(PhoneNumberUtils.compare(number,contact.phoneNo)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
