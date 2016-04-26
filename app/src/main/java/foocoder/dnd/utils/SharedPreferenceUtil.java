package foocoder.dnd.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {

    public static final String MAX_ID = "max_id";

    //手动勿扰
    public static final String START = "start";

    //自动勿扰
    private static final String USABLE = "usable";

    //勿扰设置
    private static final String LAUNCH = "launch";

    //自动恢复
    private static final String RECOVER = "recover";

    //震动
    private static final String VIBRATE = "vibrate";

    //定时勿扰
    private static final String QUIET = "QUIET";

    //重复来电
    private static final String REPEAT = "repeat";

    private static final String RUNNING_ID = "running_id";

    private static final String HEIGHT_TO_SCALE = "heightToScale";

    private static final String HEIGHT_TO_MOVE = "heightToMove";

    private SharedPreferences sp;

    private SharedPreferences.Editor editor;

    public SharedPreferenceUtil(Context context) {
        sp = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (sp != null) {
            sp.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (sp != null) {
            sp.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    public void setId(int id) {
        editor.putInt(MAX_ID,id);
        editor.commit();
    }

    public int getId() {
        return sp.getInt(MAX_ID,0);
    }

    public void start(boolean isStarted) {
        editor.putBoolean(START,isStarted);
        editor.commit();
    }

    public boolean isStarted(){
        return sp.getBoolean(START,false);
    }

    public void enable(boolean isUsable){
        editor.putBoolean(USABLE,isUsable);
        editor.commit();
    }

    public boolean isUsable(){
        return sp.getBoolean(USABLE,false);
    }

    public void setLaunch(boolean isUsable){
        editor.putBoolean(LAUNCH,isUsable);
        editor.commit();
    }

    public boolean isLaunched(){
        return sp.getBoolean(LAUNCH,false);
    }

    public void setRecover(boolean isRecoverable){
        editor.putBoolean(RECOVER,isRecoverable);
        editor.commit();
    }

    public boolean isRecoverable(){
        return sp.getBoolean(RECOVER,false);
    }

    public boolean isVib() {
        return sp.getBoolean(VIBRATE, false);
    }

    public void setVib(boolean isVib) {
        editor.putBoolean(VIBRATE, isVib);
        editor.commit();
    }

    public void setQuiet(boolean isQuiet){
        editor.putBoolean(QUIET,isQuiet);
        editor.commit();
    }

    public boolean isQuiet(){
        return sp.getBoolean(QUIET,false);
    }

    public void setRepeat(boolean isRepeated){
        editor.putBoolean(REPEAT,isRepeated);
        editor.commit();
    }

    public boolean isRepeated(){
        return sp.getBoolean(REPEAT,false);
    }

    public void setRunningId(int _id) {
        editor.putInt(RUNNING_ID,_id);
        editor.commit();
    }

    public int getRunningId() {
        return sp.getInt(RUNNING_ID,-1);
    }

    public void setHeightToScale(int heightToScale) {
        editor.putInt(HEIGHT_TO_SCALE,heightToScale);
        editor.commit();
    }

    public int getHeightToScale() {
        return sp.getInt(HEIGHT_TO_SCALE,0);
    }

    public void setHeightToMove(int heightToMove) {
        editor.putInt(HEIGHT_TO_MOVE,heightToMove);
        editor.commit();
    }

    public int getHeightToMove() {
        return sp.getInt(HEIGHT_TO_MOVE,0);
    }
}
