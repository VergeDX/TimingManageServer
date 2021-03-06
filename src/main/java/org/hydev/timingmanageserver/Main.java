package org.hydev.timingmanageserver;

import api.ApiServer;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.node.eventnode.*;
import org.hydev.timingmanageserver.node.usernode.LoginNode;
import org.hydev.timingmanageserver.node.usernode.RegisterNode;

public class Main {
    private static final String HELP_USAGE = "用法请参考 <a href=\"https://github.com/VergeDX/TimingManageServer/blob/master/README.md\">Github 上的文档</a>";

    public static void main(String[] args) {
        int port = 2333;

        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("端口号只能为整数");
                System.exit(-1);
            }
        }

        Database.initDatabase();

        ApiServer apiServer = new ApiServer(port);
        apiServer.setHelpUsage(HELP_USAGE);

        apiServer.register(new RegisterNode(), new LoginNode(),
                new StartEventNode(), new EndEventNode(), new RemoveEventNode(),
                new GetEventInfoNode(), new SetEventDescriptionNode());
        apiServer.start();
    }
}
