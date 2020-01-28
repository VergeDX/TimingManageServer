package org.hydev.timingmanageserver.event;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PendingEvent {
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
     * 事件访问 Token，格式：md5($username + $startSecond + Instant.now().getEpochSecond())
     *
     * @see EventManager#generateEventToken(PendingEvent)
     */
    @DatabaseField
    private String eventToken;

    public PendingEvent(String username, long startSecond) {
        creator = username;
        this.startSecond = startSecond;

        // 赋值事件 Token
        EventManager.generateEventToken(this);
    }
}
