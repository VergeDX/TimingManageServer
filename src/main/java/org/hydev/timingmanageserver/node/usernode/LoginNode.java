package org.hydev.timingmanageserver.node.usernode;

import api.ApiAccess;
import api.ApiNode;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.database.user.User;
import org.hydev.timingmanageserver.database.user.UserHelper;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;

import java.util.Objects;

public class LoginNode implements ApiNode {
    @Override
    public String path() {
        return "/login";
    }

    /**
     * 登录用户，要求字段：username 和 password，将返回用户对象 Json 的封装
     *
     * @return 错误信息或用用户对象 Json 的封装
     */
    @Override
    public String process(ApiAccess access) {
        String username = access.getHeaders().get("username");
        String userPassword = access.getHeaders().get("password");

        // 没传用户名/密码或为空
        if (Objects.isNull(username) || username.isEmpty() || Objects.isNull(userPassword) || userPassword.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有用户名（username）或密码（password）参数"));
        }
        // 没有这个用户名/密碼
        if (!UserHelper.isUsernameTaken(username) || UserHelper.isPasswordWrong(username, SecureUtil.md5(userPassword))) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名或密码错误。请注意区分大小写"));
        }

        User user = Database.getUserByUsername(username);
        return UserHelper.buildUserJson(user).toString();
    }
}
