package org.hydev.timingmanageserver.event;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Event {
    /**
     * 事件访问 Token，格式：md5($username + $startSecond + Instant.now().getEpochSecond())
     *
     * @see EventManager#generateEventToken(PendingEvent)
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

    public Event(String username, long startSecond, String eventToken) {
        creator = username;
        this.startSecond = startSecond;
        this.eventToken = eventToken;
    }
}
