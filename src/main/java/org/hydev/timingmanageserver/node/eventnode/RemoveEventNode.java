package org.hydev.timingmanageserver.node.eventnode;

import api.ApiAccess;
import api.ApiNode;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.event.EventHelper;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;
import org.hydev.timingmanageserver.user.UserHelper;

import java.util.Objects;

public class RemoveEventNode implements ApiNode {
    @Override
    public String path() {
        return "/removeEvent";
    }

    /**
     * 移除已完成事件，要求字段：eventToken 和 userAccessToken，将返回提示信息（移除）
     *
     * @return 错误信息或 “事件已移除”
     */
    @Override
    public String process(ApiAccess access) {
        String username = access.getHeaders().get("username");
        String userPassword = access.getHeaders().get("password");
        String eventToken = access.getHeaders().get("eventToken");

        // 没传用户名/密码或为空
        if (Objects.isNull(username) || username.isEmpty() || Objects.isNull(userPassword) || userPassword.isEmpty() || Objects.isNull(eventToken) || eventToken.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有用户名（username）、密码（password）或事件 Token（eventToken）参数"));
        }
        // 用户信息不正确
        if (!UserHelper.isPasswordCorrect(username, SecureUtil.md5(userPassword))) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名或密码错误"));
        }
        // 该用户没有这个事件
        if (!UserHelper.parseEventTokens(Database.getUserByUsername(username).getEventTokens()).contains(eventToken)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "你没有这个事件"));
        }

        EventHelper.getFinishedEventByToken(eventToken).remove();
        UserHelper.removeEvent(username, eventToken);

        return new Gson().toJson(new ServerResponse(Status.OK, "事件已移除"));
    }
}
