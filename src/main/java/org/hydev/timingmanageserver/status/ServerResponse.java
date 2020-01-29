package org.hydev.timingmanageserver.status;

import lombok.Data;

@Data
public class ServerResponse {
    /**
     * 服务器的响应状态，只有 OK 和 ERROR
     */
    private Status status;
    /**
     * 服务器返回的附属信息，可能会为 Json
     */
    private String info;

    public ServerResponse(Status status, String info) {
        this.status = status;
        this.info = info;
    }
}
