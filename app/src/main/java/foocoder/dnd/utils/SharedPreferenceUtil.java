package foocoder.dnd.utils;

import foocoder.dnd.utils.rxpreference.RxPreference;
import rx.Observable;

public class SharedPreferenceUtil {

    public static final String MAX_ID = "max_id";

    //手动勿扰
    public static final String START = "start";

    //自动勿扰
    public static final String USABLE = "usable";

    //勿扰设置
    public static final String LAUNCH = "launch";

    //震动
    public static final String VIBRATE = "vibrate";

    //定时勿扰
    public static final String QUIET = "quiet";

    //重复来电
    public static final String REPEAT = "repeat";

    public static final String RUNNING_ID = "running_id";

    public RxPreference rxPreference;

    public SharedPreferenceUtil(RxPreference rxPreference) {
        this.rxPreference = rxPreference;
    }

    public int getId() {
        return rxPreference.getValue(MAX_ID, 0);
    }

    public void setId(int id) {
        rxPreference.putValue(MAX_ID, id);
    }

    public void start(boolean isStarted) {
        rxPreference.putValue(START, isStarted);
    }

    public boolean isStarted(){
        return rxPreference.getValue(START,false);
    }

    public void enable(boolean isUsable){
        rxPreference.putValue(USABLE, isUsable);
    }

    public boolean isUsable(){
        return rxPreference.getValue(USABLE, false);
    }

    public void setLaunch(boolean isUsable){
        rxPreference.putValue(LAUNCH, isUsable);
    }

    public boolean isLaunched(){
        return rxPreference.getValue(LAUNCH, false);
    }

    public boolean isVib() {
        return rxPreference.getValue(VIBRATE, false);
    }

    public void setVib(boolean isVib) {
        rxPreference.putValue(VIBRATE, isVib);
    }

    public boolean isQuiet(){
        return rxPreference.getValue(QUIET, false);
    }

    public void setQuiet(boolean isQuiet){
        rxPreference.putValue(QUIET, isQuiet);
    }

    public void setRepeat(boolean isRepeated){
        rxPreference.putValue(REPEAT, isRepeated);
    }

    public boolean isRepeated(){
        return rxPreference.getValue(REPEAT, false);
    }

    public int getRunningId() {
        return rxPreference.getValue(RUNNING_ID, -1);
    }

    public void setRunningId(int _id) {
        rxPreference.putValue(RUNNING_ID, _id);
    }
    
    public <T> Observable<T> getChangeObservable(String key, T defaultValue) {
        return rxPreference.getChangeObservable(key, defaultValue);
    }
}
