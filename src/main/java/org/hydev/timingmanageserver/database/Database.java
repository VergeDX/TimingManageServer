package org.hydev.timingmanageserver.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import org.hydev.timingmanageserver.event.Event;
import org.hydev.timingmanageserver.event.PendingEvent;
import org.hydev.timingmanageserver.user.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class Database {
    public static Dao<PendingEvent, String> pendingEventDao;
    @Getter
    private static Dao<User, String> userDao;
    @Getter
    private static Dao<Event, String> eventDao;

    /**
     * 初始化数据库（建表，给 Dao 赋值）
     */
    public static void initDatabase() {
        // TODO: 2020/1/25 0025 改用 MySQL
        try (ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:Database.db")) {
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            TableUtils.createTableIfNotExists(connectionSource, PendingEvent.class);

            userDao = DaoManager.createDao(connectionSource, User.class);
            eventDao = DaoManager.createDao(connectionSource, Event.class);
            pendingEventDao = DaoManager.createDao(connectionSource, PendingEvent.class);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接插入一个用户
     *
     * @param user 要插入的用户对象
     */
    public static void insertUser(User user) {
        try {
            userDao.create(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接查询用户信息
     *
     * @param username 用户名
     * @return 查询到的用户
     */
    public static User queryUser(String username) {
        try {
            return userDao.queryForId(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    /**
     * 直接更新用户信息
     *
     * @param user 用户对象
     */
    public static void updateUser(User user) {
        try {
            userDao.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接插入一个事件
     *
     * @param event 要插入的事件对象
     */
    public static void insertEvent(Event event) {
        try {
            eventDao.create(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接更新一个事件
     *
     * @param event 要更新的事件
     */
    public static void updateEvent(Event event) {
        try {
            eventDao.update(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接根据事件 Token 移除一个事件
     *
     * @param eventToken 要移除事件的事件 Token
     */
    public static void removeEvent(String eventToken) {
        try {
            eventDao.deleteById(eventToken);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过用户名查询是否有未完成的事件
     *
     * @param username 用户名
     * @return true（有），false（没有）
     */
    public static boolean hasPendingEvent(String username) {
        PendingEvent pendingEvents = null;
        try {
            pendingEvents = pendingEventDao.queryForId(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return !Objects.isNull(pendingEvents);
    }

    /**
     * 直接插入一个未完成事件
     *
     * @param pendingEvent 要插入的未完成事件
     */
    public static void insertPendingEvent(PendingEvent pendingEvent) {
        try {
            pendingEventDao.create(pendingEvent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过用户名移除未完成事件，并返回它
     *
     * @param username 用户名
     * @return 用户对应的未完成事件
     */
    public static PendingEvent removePendingEvent(String username) {
        PendingEvent pendingEvent = null;

        try {
            pendingEvent = pendingEventDao.queryForId(username);
            pendingEventDao.deleteById(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 取回的未完成事件不能为空
        if (Objects.isNull(pendingEvent)) {
            throw new RuntimeException("取回的未完成事件为空");
        }

        return pendingEvent;
    }
}
