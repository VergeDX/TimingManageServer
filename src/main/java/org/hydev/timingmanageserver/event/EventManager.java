package org.hydev.timingmanageserver.event;

import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;
import org.hydev.timingmanageserver.user.UserManager;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventManager {
    /**
     * 管理状态中的 Event（还未结束），creator -> Event
     *
     * @see Event#getEventToken()
     */
    private static Map<String, Event> pendingEvent = new HashMap<>();

    /**
     * 生成事件访问 Token，防止多次赋值
     *
     * @param event 用户创建的事件
     * @return 生成的访问 Token
     * @see Event#getEventToken()
     */
    public static String generateEventToken(Event event) {
        // 防止重复赋值
        if (!Objects.isNull(event.getEventToken())) {
            throw new AssertionError("事件访问 Token 重复赋值了！");
        }

        String data = event.getCreator() + event.getStartSecond() + Instant.now().getEpochSecond();
        event.setEventToken(SecureUtil.md5(data));

        return event.getEventToken();
    }

    /**
     * 通过用户访问 Token，查询用户是否有未完成的事件
     *
     * @param userAccessToken 要查询的用户访问 Token
     * @return true（有未完成的事件），false（没有未完成的事件）
     */
    public static boolean hasPendingEvent(String userAccessToken) {
        String username = UserManager.getUserByToken(userAccessToken).getUsername();
        return !Objects.isNull(pendingEvent.get(username));
    }

    /**
     * 开始一个事件
     *
     * @param userAccessToken 用户访问 Token.
     * @return 开始的事件
     */
    public static Event startEvent(String userAccessToken) {
        String username = UserManager.getUserByToken(userAccessToken).getUsername();
        Event event = new Event(username, Instant.now().getEpochSecond());
        pendingEvent.put(username, event);
        return event;
    }

    /**
     * 结束一个事件
     *
     * @param userAccessToken 用户访问 Token
     * @return 结束的事件 Token
     */
    public static String endEvent(String userAccessToken) {
        String username = UserManager.getUserByToken(userAccessToken).getUsername();

        // 把 Event 拿回来，（解除 pending 状态）
        Event event = pendingEvent.get(username);
        pendingEvent.remove(username);

        // 设置结束时间以持续时间
        event.setEndSecond(Instant.now().getEpochSecond());
        event.setIntervalSecond(event.getEndSecond() - event.getStartSecond());

        // 插入数据库并返回
        Database.insertEvent(event);
        return event.getEventToken();
    }

    /**
     * 查询（已完成）事件是否存在
     *
     * @param eventToken 要查询的事件 Token
     * @return true（存在），false（不存在）
     */
    public static boolean isEventTokenExist(String eventToken) {
        Dao<Event, String> eventDao = Database.getEventDao();

        try {
            Event event = eventDao.queryForId(eventToken);
            return !Objects.isNull(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 直接根据事件 Token 取回对应事件
     *
     * @param eventToken 事件访问 Token
     * @return 对应的事件对象
     */
    public static Event getEventByToken(String eventToken) {
        Dao<Event, String> eventDao = Database.getEventDao();

        try {
            return eventDao.queryForId(eventToken);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 构建服务器 OK 状态的事件 Json
     *
     * @param event 需要构建的事件对象
     * @return 构建好的 Json 对象
     * @see ServerResponse#ServerResponse(Status, String)
     */
    public static JsonObject buildEventJson(Event event) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", "OK");
        jsonObject.add("info", new Gson().toJsonTree(event));

        return jsonObject;
    }

    /**
     * 移除一个事件，参数必须有效
     *
     * @param eventToken 事件 Token
     * @param userAccessToken 用户访问 Token
     */
    public static void removeEvent(String eventToken, String userAccessToken) {
        // 移除用户资料中的事件
        UserManager.parseEventTokens(UserManager.getUserByToken(userAccessToken).getEventTokens());
        UserManager.removeEvent(userAccessToken, eventToken);

        // 移除数据库中的事件
        Database.removeEvent(eventToken);
    }
}
