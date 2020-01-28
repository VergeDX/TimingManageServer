package org.hydev.timingmanageserver.user;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "users")
public class User {
    /**
     * 用户名，只能包含字母、数字和下划线
     *
     * @see UserManager#isUsernameValid(String)
     */
    @DatabaseField(id = true)
    private String username;

    /**
     * 用户密码的 Md5
     */
    @DatabaseField
    private String userPasswordMd5;

    /**
     * 用户访问 token，格式：md5($username + $userPasswordMd5 + Instant.now().getEpochSecond())
     *
     * @see UserManager#updateUserAccessToken(User)
     */
    @DatabaseField
    private String userAccessToken;

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

        // 赋值用户访问 Token
        UserManager.updateUserAccessToken(this);
    }
}
