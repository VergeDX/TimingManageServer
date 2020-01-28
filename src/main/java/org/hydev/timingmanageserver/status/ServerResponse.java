package org.hydev.timingmanageserver.status;

import lombok.Data;

@Data
public class ServerResponse {
    private Status status;
    private String info;

    public ServerResponse(Status status, String info) {
        this.status = status;
        this.info = info;
    }
}
