package org.hydev.timingmanageserver.user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserHelper {
    /**
     * 查询用户名是否已存在
     *
     * @param username 要查询的用户名
     * @return true（存在），false（不存在）
     */
    public static boolean isUsernameTaken(String username) {
        try {
            return Database.getUserDao().idExists(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 检查用户信息是否正确
     *
     * @param username 用户名，必须存在（先检查）
     * @param userPasswordMd5 Md5 后的用户密码
     * @return true（正确），false（错误）
     * @see UserHelper#isUsernameTaken(String)
     */
    public static boolean isPasswordWrong(String username, String userPasswordMd5) {
        // 没有该用户时
        if (!isUsernameTaken(username)) {
            return true;
        } else {
            User user = Database.getUserByUsername(username);
            return !user.getUserPasswordMd5().equals(userPasswordMd5);
        }
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
     * 给用户添加事件（更新用户的事件 Token 字符串）
     *
     * @param username 用户名
     * @param eventToken 要更新的事件 Token
     */
    public static void addEvent(String username, String eventToken) {
        User user = Database.getUserByUsername(username);

        List<String> eventList = parseEventTokens(user.getEventTokens());
        eventList.add(eventToken);
        user.setEventTokens(eventList.toString());
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
     * @param username 用户访问 Token
     * @param eventToken 事件 Token
     */
    public static void removeEvent(String username, String eventToken) {
        User user = Database.getUserByUsername(username);

        String eventTokens = user.getEventTokens();
        List<String> eventList = parseEventTokens(eventTokens);
        eventList.remove(eventToken);

        user.setEventTokens(eventList.toString());
    }
}
