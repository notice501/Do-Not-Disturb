package foocoder.dnd.data.exception;

/**
 * Created by xuechi.
 * Time: 2016 五月 04 23:16
 * Project: dnd
 */
public class TimeNotSetException extends RuntimeException {

    public TimeNotSetException() {
        super("Time must be set!");
    }
}
