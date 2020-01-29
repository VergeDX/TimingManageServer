package org.hydev.timingmanageserver.event;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hydev.timingmanageserver.database.Database;

import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PendingEvent extends Event {
    /**
     * 事件创建的用户名
     */
    @DatabaseField(id = true)
    private String creator;

    /**
     * 事件开始时间，单位：秒（s）
     */
    @DatabaseField
    private long startSecond;

    /**
     * 事件访问 Token，格式：md5($username + $startSecond)
     *
     * @see EventHelper#generateEventToken(PendingEvent)
     */
    @DatabaseField
    private String eventToken;

    public PendingEvent(String username) {
        this.start(username);
    }

    @Override
    protected void start(String username) {
        creator = username;
        startSecond = Instant.now().getEpochSecond();
        EventHelper.generateEventToken(this);

        Database.insertPendingEvent(this);
    }

    @Override
    public FinishedEvent end() {
        return new FinishedEvent(this);
    }
}
