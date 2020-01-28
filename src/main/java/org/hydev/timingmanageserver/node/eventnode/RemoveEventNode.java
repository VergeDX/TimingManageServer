package org.hydev.timingmanageserver.node.eventnode;

import api.ApiAccess;
import api.ApiNode;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.event.EventManager;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;
import org.hydev.timingmanageserver.user.UserManager;

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
        String eventToken = access.getHeaders().get("eventToken");
        String userAccessToken = access.getHeaders().get("userAccessToken");

        // 没传事件 Token/用户访问 Token 或为空
        if (Objects.isNull(eventToken) || eventToken.isEmpty() || Objects.isNull(userAccessToken) || userAccessToken.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有事件 Token（eventToken）或用户访问 Token（userAccessToken）参数"));
        }
        // 事件 Token / 用户访问 Token 不存在/不匹配
        if (!EventManager.isEventTokenExist(eventToken) || !UserManager.isUserAccessTokenExist(userAccessToken) ||
                !UserManager.getUserByToken(userAccessToken).getUsername().equals(EventManager.getEventByToken(eventToken).getCreator())) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "事件 Token 或用户访问 Token 不存在/不匹配，您可能未结束该事件（/endEvent）"));
        }

        EventManager.removeEvent(eventToken, userAccessToken);
        return new Gson().toJson(new ServerResponse(Status.OK, "事件已移除"));
    }
}
