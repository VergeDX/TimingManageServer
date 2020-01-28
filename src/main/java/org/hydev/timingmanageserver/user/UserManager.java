package org.hydev.timingmanageserver.user;

import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserManager {
    /**
     * 查询用户名是否已存在
     *
     * @param username 要查询的用户名
     * @return true（存在），false（不存在）
     */
    public static boolean isUsernameTaken(String username) {
        Dao<User, String> userDao = Database.getUserDao();

        try {
            User user = userDao.queryForId(username);
            return !Objects.isNull(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 检查用户名的合法性，用户名只能包含字母、数字或下划线
     *
     * @param username 要检查的用户名
     * @return true（合法），false（不合法）
     * @see User#getUsername()
     */
    public static boolean isUsernameValid(String username) {
        char[] usernameArray = username.toCharArray();

        for (char c : usernameArray) {
            if (!(Character.isLetter(c) || Character.isDigit(c) || c == '_')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查密码是否正确，前提：这个用户名必须存在
     *
     * @param username 用户名，必须存在（先检查）
     * @param userPasswordMd5 Md5 后的用户密码
     * @return true（正确），false（错误）
     * @see UserManager#isUsernameTaken(String)
     */
    public static boolean isPasswordCorrect(String username, String userPasswordMd5) {
        Dao<User, String> userDao = Database.getUserDao();

        try {
            User user = userDao.queryForId(username);
            return user.getUserPasswordMd5().equals(userPasswordMd5);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 更新用户访问 Token 并返回
     *
     * @param user 用户对象
     * @return 更新后的用户访问 Token
     * @see User#getUserAccessToken()
     */
    public static String updateUserAccessToken(User user) {
        String data = user.getUsername() + user.getUserPasswordMd5() + Instant.now().getEpochSecond();
        user.setUserAccessToken(SecureUtil.md5(data));

        return user.getUserAccessToken();
    }

    /**
     * 查询用户访问 Token 是否存在
     *
     * @param userAccessToken 要查询的用户访问 Token
     * @return true（存在），false（不存在）
     */
    public static boolean isUserAccessTokenExist(String userAccessToken) {
        Dao<User, String> userDao = Database.getUserDao();

        try {
            List<User> result = userDao.queryForEq("userAccessToken", userAccessToken);
            return !result.isEmpty();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 通过用户访问 Token 查询用户对象，前提：用户访问 Token 必须存在
     *
     * @param userAccessToken 要查询的用户访问 Token
     * @return 查询到的用户对象
     */
    public static User getUserByToken(String userAccessToken) {
        Dao<User, String> userDao = Database.getUserDao();

        try {
            List<User> result = userDao.queryForEq("userAccessToken", userAccessToken);
            return result.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 构建服务器 OK 状态的用户 Json
     *
     * @param user 需要构建的用户对象
     * @return 构建好的 Json 对象
     * @see ServerResponse#ServerResponse(Status, String)
     */
    public static JsonObject buildUserJson(User user) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", "OK");
        jsonObject.add("info", new Gson().toJsonTree(user));

        return jsonObject;
    }

    /**
     * 给用户添加事件，（更新用户的事件 Token 字符串）
     *
     * @param userAccessToken 用户访问 Token
     * @param eventToken 要更新的事件 Token
     */
    public static void addEvent(String userAccessToken, String eventToken) {
        User user = UserManager.getUserByToken(userAccessToken);
        String eventTokens = user.getEventTokens();

        List<String> eventList = parseEventTokens(eventTokens);
        eventList.add(eventToken);
        user.setEventTokens(eventList.toString());

        Database.updateUser(user);
    }

    /**
     * 解析用户拥有的事件 Token 列表，String -> List<String>
     *
     * @param eventTokens 要解析的事件列表字符串
     * @return 解析出的事件列表
     */
    public static List<String> parseEventTokens(String eventTokens) {
        // 用户之前没有事件
        if (eventTokens.equals("[]")) {
            return new ArrayList<>();
        }
        // 用户之前有事件
        else {
            // https://stackoverflow.com/questions/2774142/convert-arraylist-tostring-back-to-arraylist-in-one-call
            List<String> rawEventList = Arrays.asList(eventTokens.substring(1, eventTokens.length() - 1).split(", "));
            return new ArrayList<>(rawEventList);
        }
    }

    /**
     * 移除用户的事件
     *
     * @param userAccessToken 用户访问 Token
     * @param eventToken 事件 Token
     */
    public static void removeEvent(String userAccessToken, String eventToken) {
        User user = UserManager.getUserByToken(userAccessToken);
        String eventTokens = user.getEventTokens();

        List<String> eventList = parseEventTokens(eventTokens);
        eventList.remove(eventToken);
        user.setEventTokens(eventList.toString());

        Database.updateUser(user);
    }
}
