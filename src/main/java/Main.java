import api.ApiServer;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.node.eventnode.*;
import org.hydev.timingmanageserver.node.usernode.GetUserInfoNode;
import org.hydev.timingmanageserver.node.usernode.LoginNode;
import org.hydev.timingmanageserver.node.usernode.RegisterNode;

// TODO: 2020/1/25 0025
//  1. 多线程访问
//  2. 设置 Help Usage 等.
public class Main {
    private static final String HELP_USAGE = "超链接 -> GitHub";

    public static void main(String[] args) {
        Database.initDatabase();

        ApiServer apiServer = new ApiServer(2333);
        apiServer.setHelpUsage(HELP_USAGE);

        apiServer.register(new RegisterNode(), new LoginNode(), new GetUserInfoNode(),
                new StartEventNode(), new EndEventNode(), new RemoveEventNode(),
                new GetEventInfoNode(), new SetDescriptionNode());
        apiServer.start();
    }
}
