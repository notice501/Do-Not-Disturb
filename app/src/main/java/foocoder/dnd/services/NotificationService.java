package foocoder.dnd.services;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import javax.inject.Inject;

import foocoder.dnd.presentation.App;
import foocoder.dnd.utils.SharedPreferenceUtil;
import timber.log.Timber;

/**
 * Created by xuechi.
 * Time: 2016 五月 08 23:07
 * Project: dnd
 */
public class NotificationService extends NotificationListenerService {

    @Inject
    SharedPreferenceUtil spUtil;

    @Override
    public void onCreate() {
        super.onCreate();

        App.getContext().getApplicationComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Timber.d("onNotificationPosted %d", getCurrentInterruptionFilter());
        if (sbn.getPackageName().contains("foocoder.dnd")) {
            if (spUtil.isStarted()) {
                if (!(spUtil.isLaunched() && spUtil.isVib())) {
                    requestInterruptionFilter(INTERRUPTION_FILTER_NONE);
                }
            } else if (spUtil.isUsable()) {
                if (!spUtil.isVib()) {
                    requestInterruptionFilter(INTERRUPTION_FILTER_NONE);
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Timber.d("onNotificationRemoved %d", getCurrentInterruptionFilter());
    }
}
