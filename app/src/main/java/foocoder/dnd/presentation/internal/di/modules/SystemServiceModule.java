package foocoder.dnd.presentation.internal.di.modules;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xuechi.
 * Time: 2016 五月 07 02:05
 * Project: dnd
 */
@Module
public class SystemServiceModule {

    @Provides
    @Singleton
    AudioManager provideAudioManager(Application context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @Singleton
    AlarmManager provideAlarmManager(Application context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
}
