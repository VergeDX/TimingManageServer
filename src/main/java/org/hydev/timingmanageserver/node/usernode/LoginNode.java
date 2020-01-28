package org.hydev.timingmanageserver.node.usernode;

import api.ApiAccess;
import api.ApiNode;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;
import org.hydev.timingmanageserver.user.User;
import org.hydev.timingmanageserver.user.UserManager;

import java.util.Objects;

public class LoginNode implements ApiNode {
    @Override
    public String path() {
        return "/login";
    }

    /**
     * 登录用户，要求字段：username 和 password，将刷新并返回用户访问 Token
     *
     * @return 错误信息或用户访问 Token
     */
    @Override
    public String process(ApiAccess access) {
        String username = access.getHeaders().get("username");
        String userPassword = access.getHeaders().get("password");

        // 没传用户名/密码或为空
        if (Objects.isNull(username) || username.isEmpty() || Objects.isNull(userPassword) || userPassword.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有用户名（username）或密码（password）参数"));
        }
        // 没有这个用户名
        if (!UserManager.isUsernameTaken(username) || !UserManager.isPasswordCorrect(username, SecureUtil.md5(userPassword))) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名或密码错误。请注意，用户名区分大小写"));
        }

        User user = Database.queryUser(username);

        // 更新用户访问 Token，并更新进数据库
        user.setUserAccessToken(UserManager.updateUserAccessToken(user));
        Database.updateUser(user);

        return new Gson().toJson(new ServerResponse(Status.OK, user.getUserAccessToken()));
    }
}
