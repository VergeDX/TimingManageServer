package org.hydev.timingmanageserver.event;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hydev.timingmanageserver.database.Database;

import java.time.Instant;
import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FinishedEvent extends Event {
    /**
     * 事件访问 Token，格式：md5($username + $startSecond)
     *
     * @see EventHelper#generateEventToken(PendingEvent)
     */
    @DatabaseField(id = true)
    private String eventToken;

    /**
     * 事件创建的用户名
     */
    @DatabaseField
    private String creator;

    /**
     * 事件说明（标题 + 短描述）
     */
    @DatabaseField
    private String description = "null";

    /**
     * 事件开始时间，单位：秒（s）
     */
    @DatabaseField
    private long startSecond;

    /**
     * 事件结束时间，单位：秒（s）
     */
    @DatabaseField
    private long endSecond;

    /**
     * 事件持续时间，单位：秒（s），intervalSecond = endSecond - startSecond;
     */
    @DatabaseField
    private long intervalSecond;

    public FinishedEvent(PendingEvent pendingEvent) {
        if (Objects.isNull(pendingEvent)) {
            throw new AssertionError();
        }

        // 取回未完成事件的参数
        eventToken = pendingEvent.getEventToken();
        creator = pendingEvent.getCreator();
        startSecond = pendingEvent.getStartSecond();

        // 设置结束的字段
        endSecond = Instant.now().getEpochSecond();
        intervalSecond = endSecond - startSecond;

        // 建新的，删旧的
        Database.insertFinishedEvent(this);
        Database.removeEvent(pendingEvent, PendingEvent.class);
    }

    @Override
    protected void update() {
        Database.updateFinishedEvent(this);
    }

    public void setDescription(String description) {
        this.description = description;
        update();
    }
}
