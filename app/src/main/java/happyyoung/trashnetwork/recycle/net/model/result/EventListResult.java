package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.Event;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-03
 */
public class EventListResult extends Result {
    private List<Event> eventList;

    public EventListResult(int resultCode, String message, List<Event> eventList) {
        super(resultCode, message);
        this.eventList = eventList;
    }

    public List<Event> getEventList() {
        return eventList;
    }
}
