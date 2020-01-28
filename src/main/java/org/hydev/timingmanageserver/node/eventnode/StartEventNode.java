package org.hydev.timingmanageserver.node.eventnode;

import api.ApiAccess;
import api.ApiNode;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.event.Event;
import org.hydev.timingmanageserver.event.EventManager;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;
import org.hydev.timingmanageserver.user.UserManager;

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
        String userAccessToken = access.getHeaders().get("userAccessToken");

        // 没传用户访问 Token 或为空
        if (Objects.isNull(userAccessToken) || userAccessToken.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有用户访问 Token（userAccessToken）参数"));
        }
        // 用户访问 Token 不存在
        if (!UserManager.isUserAccessTokenExist(userAccessToken)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户访问 Token 不存在"));
        }
        // 用户有未结束的事件
        if (EventManager.hasPendingEvent(userAccessToken)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "你有未完成的事件，请先结束该事件（/endEvent）"));
        }

        Event event = EventManager.startEvent(userAccessToken);
        return new Gson().toJson(new ServerResponse(Status.OK, event.getEventToken()));
    }
}