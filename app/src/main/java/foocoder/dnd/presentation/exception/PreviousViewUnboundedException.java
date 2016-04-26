package foocoder.dnd.presentation.exception;

/**
 * Created by xuechi.
 * Time: 2016 四月 24 07:45
 * Project: dnd
 */
public final class PreviousViewUnboundedException extends RuntimeException {

    public PreviousViewUnboundedException() {
        super("Previous view is not unbounded!");
    }
}
