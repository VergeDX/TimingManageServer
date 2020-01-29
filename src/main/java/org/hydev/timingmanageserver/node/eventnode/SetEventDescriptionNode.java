package org.hydev.timingmanageserver.node.eventnode;

import api.ApiAccess;
import api.ApiNode;
import com.google.gson.Gson;
import org.hydev.timingmanageserver.event.EventHelper;
import org.hydev.timingmanageserver.event.FinishedEvent;
import org.hydev.timingmanageserver.status.ServerResponse;
import org.hydev.timingmanageserver.status.Status;

import java.util.Objects;

public class SetEventDescriptionNode implements ApiNode {
    @Override
    public String path() {
        return "/setEventDescription";
    }

    /**
     * 设置事件说明，要求字段：eventToken 和 description，将返回事件对象 Json 的封装
     *
     * @return 错误信息或事件对象 Json 的封装
     */
    @Override
    public String process(ApiAccess access) {
        String eventToken = access.getHeaders().get("eventToken");
        String description = access.getHeaders().get("description");

        // 没传事件 Token/事件说明 或为空
        if (Objects.isNull(eventToken) || eventToken.isEmpty() || Objects.isNull(description) || description.isEmpty()) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "请求头（Header）中没有事件 Token（eventToken）或事件说明（description）参数"));
        }
        // 事件 Token 不存在或未结束
        if (EventHelper.isFinishedEventTokenNotExist(eventToken)) {
            return new Gson().toJson(new ServerResponse(Status.ERROR, "事件 Token 不存在，您可能没有结束该事件（/endEvent）"));
        }

        FinishedEvent finishedEvent = EventHelper.getFinishedEventByToken(eventToken);
        finishedEvent.setDescription(description);

        return EventHelper.buildEventJson(finishedEvent).toString();
    }
}
