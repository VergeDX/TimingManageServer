package org.hydev.timingmanageserver;

import api.ApiServer;
import org.hydev.timingmanageserver.database.Database;
import org.hydev.timingmanageserver.node.eventnode.*;
import org.hydev.timingmanageserver.node.usernode.LoginNode;
import org.hydev.timingmanageserver.node.usernode.RegisterNode;

// TODO: 2020/1/25 0025
//  1. 多线程访问
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

        ApiServer apiServer = new ApiServer(2333);
        apiServer.setHelpUsage(HELP_USAGE);

        apiServer.register(new RegisterNode(), new LoginNode(),
                new StartEventNode(), new EndEventNode(), new RemoveEventNode(),
                new GetEventInfoNode(), new SetDescriptionNode());
        apiServer.start();
    }
}
