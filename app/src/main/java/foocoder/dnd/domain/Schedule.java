package foocoder.dnd.domain;

import java.util.List;

public class Schedule {

    private int _id;

    private String from;

    private String to;

    private List<Integer> checked;

    private boolean del;

    private boolean running;

    public Schedule() {

    }

    public Schedule(String from, String to, List<Integer> checked) {
        this.from = from;
        this.to = to;
        this.checked = checked;
        this.del = false;
        this.running = false;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<Integer> getChecked() {
        return checked;
    }

    public void setChecked(List<Integer> checked) {
        this.checked = checked;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public int getId() {
        return this._id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
