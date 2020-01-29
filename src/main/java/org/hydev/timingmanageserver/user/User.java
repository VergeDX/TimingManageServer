package org.hydev.timingmanageserver.user;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hydev.timingmanageserver.database.Database;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class User {
    /**
     * 用户名，只能包含字母、数字和下划线
     *
     * @see UserHelper#isUsernameValid(String)
     */
    @DatabaseField(id = true)
    private String username;

    /**
     * 用户密码的 Md5
     */
    @DatabaseField
    private String userPasswordMd5;

    /**
     * 该用户的全部事件列表，格式即为 ArrayList.toString()
     *
     * @see ArrayList#toString()
     */
    @DatabaseField
    private String eventTokens = "[]";

    public User(String username, String userPasswordMd5) {
        this.username = username;
        this.userPasswordMd5 = userPasswordMd5;

        update();
    }

    private void update() {
        Database.updateUser(this);
    }

    public void setEventTokens(String eventTokens) {
        this.eventTokens = eventTokens;
        update();
    }
}
