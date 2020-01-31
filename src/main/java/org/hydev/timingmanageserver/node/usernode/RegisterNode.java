package org.hydev.timingmanageserver.node.usernode;

import api.ApiAccess;
import api.ApiNode;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.database.user.User;
import org.hydev.timingmanageserver.database.user.UserHelper;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;

import java.util.Objects;

public class RegisterNode implements ApiNode {
    @Override
    public String path() {
        return "/register";
    }

    /**
     * 注册用户，要求字段：username 和 password，将返回 "注册成功"
     *
     * @return 错误信息或 "注册成功"
     */
    @Override
    public String process(ApiAccess access) {
        String username = access.getHeaders().get("username");
        String userPassword = access.getHeaders().get("password");

        // 没传用户名/密码或为空
        if (Objects.isNull(username) || username.isEmpty() || Objects.isNull(userPassword) || userPassword.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有用户名（username）或密码（password）参数"));
        }
        // 用户名长度不合法
        if (username.length() > 32) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名太长，它只能小于 32 个字符"));
        }
        // 用户名不合法
        if (!UserHelper.isUsernameValid(username)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "用户名不合法，它必须由字母、数字或下划线组成"));
        }
        // 用户名被占用
        if (UserHelper.isUsernameTaken(username)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "该用户名已被占用"));
        }

        User user = new User(username, SecureUtil.md5(userPassword));
        return new Gson().toJson(new ServerResponse(Status.OK, "注册成功"));
    }
}
