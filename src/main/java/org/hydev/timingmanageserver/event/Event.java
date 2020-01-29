package org.hydev.timingmanageserver.event;

import org.hydev.timingmanageserver.database.Database;

/**
 * 抽象事件类，事件可以被开始、结束、更新、移除自己
 */
public abstract class Event {
    protected void start(String username) {
        throw new UnsupportedOperationException();
    }

    public FinishedEvent end() {
        throw new UnsupportedOperationException();
    }

    protected void update() {
        throw new UnsupportedOperationException();
    }

    /**
     * 事件从数据库移除自己，表不同故需要类信息
     */
    public void remove() {
        Database.removeEvent(this, this.getClass());
    }

    /**
     * Lombok @Getter 自动覆盖此方法
     */
    public abstract String getCreator();

    /**
     * Lombok @Getter 自动覆盖此方法
     */
    public abstract String getEventToken();
}
