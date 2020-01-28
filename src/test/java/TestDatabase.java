import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.hydev.timingmanageserver.user.User;

import java.io.IOException;
import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {
        try (ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:test.db")) {
            TableUtils.createTableIfNotExists(connectionSource, User.class);
        } catch (SQLException | IOException e) {
            System.out.println("Error in initDatabase()");
            e.printStackTrace();
        }
    }
}
