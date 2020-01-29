package org.hydev.timingmanageserver.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import org.hydev.timingmanageserver.event.Event;
import org.hydev.timingmanageserver.event.FinishedEvent;
import org.hydev.timingmanageserver.event.PendingEvent;
import org.hydev.timingmanageserver.user.User;

import java.io.IOException;
import java.sql.SQLException;

public class Database {
    public static Dao<PendingEvent, String> pendingEventDao;
    @Getter
    private static Dao<User, String> userDao;
    @Getter
    private static Dao<FinishedEvent, String> finishedEventDao;

    /**
     * 初始化数据库（建表，给 Dao 赋值）
     */
    public static void initDatabase() {
        // TODO: 2020/1/25 0025 改用 MySQL
        try (ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:Database.db")) {
            TableUtils.createTableIfNotExists(connectionSource, PendingEvent.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, FinishedEvent.class);

            pendingEventDao = DaoManager.createDao(connectionSource, PendingEvent.class);
            userDao = DaoManager.createDao(connectionSource, User.class);
            finishedEventDao = DaoManager.createDao(connectionSource, FinishedEvent.class);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeEvent(Event event, Class<? extends Event> T) {
        try {
            if (T == PendingEvent.class) {
                pendingEventDao.deleteById(event.getCreator());
            } else if (T == FinishedEvent.class) {
                finishedEventDao.deleteById(event.getEventToken());
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertPendingEvent(PendingEvent pendingEvent) {
        try {
            pendingEventDao.create(pendingEvent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertOrUpdateFinishedEvent(FinishedEvent finishedEvent) {
        try {
            finishedEventDao.createOrUpdate(finishedEvent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(User user) {
        try {
            userDao.createOrUpdate(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PendingEvent getPendingEventByUsername(String username) {
        PendingEvent pendingEvent = null;

        try {
            pendingEvent = pendingEventDao.queryForId(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingEvent;
    }

    public static User getUserByUsername(String username) {
        User user = null;
        try {
            user = userDao.queryForId(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
