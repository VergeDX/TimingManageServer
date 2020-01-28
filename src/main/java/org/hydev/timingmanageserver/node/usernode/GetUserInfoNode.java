package org.hydev.timingmanageserver.node.usernode;

import api.ApiAccess;
import api.ApiNode;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;
import org.hydev.timingmanageserver.user.UserManager;

import java.util.Objects;

public class GetUserInfoNode implements ApiNode {
    @Override
    public String path() {
        return "/getUserInfo";
    }

    /**
     * 获取用户信息，要求字段：userAccessToken，将反对用户对象 Json 的封装
     *
     * @return 错误信息或用户对象 Json
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

        return UserManager.buildUserJson(UserManager.getUserByToken(userAccessToken)).toString();
    }
}
