package org.hydev.timingmanageserver.event;

import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import org.hydev.timingmanageserver.database.Database;

import java.sql.SQLException;
import java.util.Objects;

public class EventHelper {
    /**
     * 生成事件访问 Token，防止多次赋值
     *
     * @param pendingEvent 用户创建的事件
     * @return 生成的访问 Token
     */
    public static String generateEventToken(PendingEvent pendingEvent) {
        // 防止重复赋值
        if (!Objects.isNull(pendingEvent.getEventToken())) {
            throw new AssertionError();
        }

        String data = pendingEvent.getCreator() + pendingEvent.getStartSecond();
        pendingEvent.setEventToken(SecureUtil.md5(data));

        return pendingEvent.getEventToken();
    }

    /**
     * 查询已完成事件是否存在
     *
     * @param eventToken 要查询的事件 Token
     * @return true（存在），false（不存在）
     */
    public static boolean isFinishedEventTokenNotExist(String eventToken) {
        FinishedEvent finishedEvent = getFinishedEventByToken(eventToken);
        return Objects.isNull(finishedEvent);
    }

    /**
     * 根据事件 Token 取回对应事件
     *
     * @param eventToken 事件访问 Token
     * @return 对应的事件对象
     */
    public static FinishedEvent getFinishedEventByToken(String eventToken) {
        Dao<FinishedEvent, String> finishedEventDao = Database.getFinishedEventDao();

        try {
            return finishedEventDao.queryForId(eventToken);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 构建服务器 OK 状态的事件 Json
     *
     * @param finishedEvent 需要构建的事件对象
     * @return 构建好的 Json 对象
     */
    public static JsonObject buildEventJson(FinishedEvent finishedEvent) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", "OK");
        jsonObject.add("info", new Gson().toJsonTree(finishedEvent));

        return jsonObject;
    }
}
