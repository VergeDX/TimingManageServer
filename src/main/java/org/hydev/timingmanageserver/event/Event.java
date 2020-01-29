package org.hydev.timingmanageserver.event;

import org.hydev.timingmanageserver.database.Database;

public abstract class Event {
    protected Event start(String username) {
        throw new UnsupportedOperationException();
    }

    public FinishedEvent end() {
        throw new UnsupportedOperationException();
    }

    protected void update() {
        throw new UnsupportedOperationException();
    }

    public void remove() {
        Database.removeEvent(this, this.getClass());
    }

    // 子类的 @Data 自动覆盖
    public abstract String getCreator();

    public abstract String getEventToken();
}
