package foocoder.dnd.utils.rxpreference;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

/**
 * Created by xuechi.
 * Time: 2016 五月 05 13:47
 * Project: dnd
 */
public final class RxPreference {

    private static Map<String, RxPreference> cached = new HashMap<>();
    private SharedPreferences sharedPreferences;
    private Observable<String> observable;

    private RxPreference(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        observable = Observable.<String>create(subscriber -> {
            SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences1, key) -> subscriber.onNext(key);
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
            subscriber.add(Subscriptions.create(() -> sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)));
        }).share();
    }

    public static RxPreference getInstance(@NonNull String name, Application context) {
        RxPreference instance = cached.get(name);
        if (instance == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            instance = new RxPreference(sharedPreferences);
            cached.put(name, instance);
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key, T defaultValue) {
        Object result = null;
        if (defaultValue instanceof Boolean) {
            result = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof String) {
            result = sharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            result = sharedPreferences.getInt(key, (Integer) defaultValue);
        }

        return (T) result;
    }

    public <T> void putValue(String key, T value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, Boolean.class.cast(value));
        } else if (value instanceof String) {
            editor.putString(key, String.class.cast(value));
        } else if (value instanceof Integer) {
            editor.putInt(key, Integer.class.cast(value));
        }
        editor.apply();
    }

    public <T> Observable<T> getChangeObservable(@NonNull String key, T defaultValue) {
        return observable
                .filter(s -> Objects.equals(s, key))
                .map(s -> getValue(key, defaultValue));
    }

    public <T> Action1<T> getAction(String key) {
        return t -> putValue(key, t);
    }
}
