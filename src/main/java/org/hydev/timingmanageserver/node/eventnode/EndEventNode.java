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

public class EndEventNode implements ApiNode {
    @Override
    public String path() {
        return "/endEvent";
    }

    /**
     * 结束事件，要求字段：username & password，将结束一个事件并返回事件对象 Json 的封装
     *
     * @return 错误信息或事件对象 Json
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
        if (!UserHelper.isUsernameTaken(username) || UserHelper.isPasswordWrong(username, SecureUtil.md5(userPassword))) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名或密码错误。请注意区分大小写"));
        }
        // 用户没有未结束的事件
        if (Database.getPendingEventByUsername(username) == null) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "你没有未完成的事件，请先开始一个事件（/startEvent）"));
        }

        FinishedEvent finishedEvent = new FinishedEvent(Database.getPendingEventByUsername(username));
        UserHelper.addEvent(username, finishedEvent.getEventToken());

        return EventHelper.buildEventJson(finishedEvent).toString();
    }
}
