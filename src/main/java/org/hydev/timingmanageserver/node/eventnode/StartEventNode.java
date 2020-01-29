package org.hydev.timingmanageserver.node.eventnode;

import api.ApiAccess;
import api.ApiNode;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.event.PendingEvent;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;
import org.hydev.timingmanageserver.user.UserHelper;

import java.util.Objects;

public class StartEventNode implements ApiNode {
    @Override
    public String path() {
        return "/startEvent";
    }

    /**
     * 开启事件，要求字段：userAccessToken，将挂起一个事件并返回事件 Token
     *
     * @return 错误信息或事件 Token
     */
    @Override
    public String process(ApiAccess access) {
        String username = access.getHeaders().get("username");
        String userPassword = access.getHeaders().get("password");

        // 没传用户名/密码或为空
        if (Objects.isNull(username) || username.isEmpty() || Objects.isNull(userPassword) || userPassword.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有用户名（username）或密码（password）参数"));
        }
        // 没有这个用户名/密码
        if (!UserHelper.isUsernameTaken(username) || !UserHelper.isPasswordCorrect(username, SecureUtil.md5(userPassword))) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名或密码错误。请注意区分大小写"));
        }
        // 用户有未结束的事件
        if (Database.getPendingEventByUsername(username) != null) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "你有未完成的事件，请先结束该事件（/endEvent）"));
        }

        PendingEvent pendingEvent = new PendingEvent(username);
        return new Gson().toJson(new ServerResponse(Status.OK, pendingEvent.getEventToken()));
    }
}
