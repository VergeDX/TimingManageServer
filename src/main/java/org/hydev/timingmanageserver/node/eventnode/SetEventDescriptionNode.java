package org.hydev.timingmanageserver.node.eventnode;

import api.ApiAccess;
import api.ApiNode;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.database.event.EventHelper;
import org.hydev.timingmanageserver.database.event.FinishedEvent;
import org.hydev.timingmanageserver.database.user.UserHelper;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;

import java.util.Objects;

public class SetEventDescriptionNode implements ApiNode {
    @Override
    public String path() {
        return "/setEventDescription";
    }

    /**
     * 设置事件说明，要求字段：eventToken & description，将返回事件对象 Json 的封装
     *
     * @return 错误信息或事件对象 Json 的封装
     */
    @Override
    public String process(ApiAccess access) {
        String username = access.getHeaders().get("username");
        String userPassword = access.getHeaders().get("password");
        String eventToken = access.getHeaders().get("eventToken");
        String description = access.getHeaders().get("description");

        // 没传事件 Token/事件说明 或为空
        if (Objects.isNull(username) || username.isEmpty() || Objects.isNull(userPassword) || userPassword.isEmpty() ||
                Objects.isNull(eventToken) || eventToken.isEmpty() || Objects.isNull(description) || description.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有用户名（username）、密码（password）、事件 Token（eventToken）或事件说明（description）参数"));
        }
        // 没有这个用户名/密碼
        if (!UserHelper.isUsernameTaken(username) || UserHelper.isPasswordWrong(username, SecureUtil.md5(userPassword))) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名或密码错误。请注意区分大小写"));
        }
        // 事件 Token 不存在或未结束 || 该用户没有这个事件
        if (EventHelper.isFinishedEventTokenNotExist(eventToken) ||
                !UserHelper.parseEventTokens(Database.getUserByUsername(username).getEventTokens()).contains(eventToken)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "事件 Token 不存在或不属于你，您可能没有结束该事件（/endEvent）"));
        }

        FinishedEvent finishedEvent = EventHelper.getFinishedEventByToken(eventToken);
        finishedEvent.setDescription(description);

        return EventHelper.buildEventJson(finishedEvent).toString();
    }
}
