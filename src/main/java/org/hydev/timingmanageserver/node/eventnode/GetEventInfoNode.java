package org.hydev.timingmanageserver.node.eventnode;

import api.ApiAccess;
import api.ApiNode;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.event.EventHelper;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;

import java.util.Objects;

public class GetEventInfoNode implements ApiNode {
    @Override
    public String path() {
        return "/getEventInfo";
    }

    /**
     * 获取事件信息，要求字段：eventToken，将返回事件对象 Json 的封装
     *
     * @return 错误信息或事件对象 Json 的封装
     */
    @Override
    public String process(ApiAccess access) {
        String eventToken = access.getHeaders().get("eventToken");

        // 没传事件 Token 或为空
        if (Objects.isNull(eventToken) || eventToken.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有事件 Token（eventToken）参数"));
        }
        // 事件 Token 不存在或未结束
        if (EventHelper.isFinishedEventTokenNotExist(eventToken)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "事件 Token 未找到，您可能没有结束该事件（/endEvent）"));
        }

        return EventHelper.buildEventJson(EventHelper.getFinishedEventByToken(eventToken)).toString();
    }
}
