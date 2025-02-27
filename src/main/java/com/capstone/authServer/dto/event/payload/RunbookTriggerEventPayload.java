package com.capstone.authServer.dto.event.payload;

import java.util.List;

import com.capstone.authServer.model.KafkaTopic;
import com.capstone.authServer.model.Tool;
import com.capstone.authServer.model.runbook.RunbookTriggerType;

public class RunbookTriggerEventPayload {
    Tool tool;
    Long tenantId;
    List<String> findingIds;
    KafkaTopic destTopic;
    RunbookTriggerType triggerType;

    public RunbookTriggerEventPayload(Tool tool, Long tenantId, List<String> findingIds, KafkaTopic destTopic,
            RunbookTriggerType triggerType) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.findingIds = findingIds;
        this.destTopic = destTopic;
        this.triggerType = triggerType;
    }

    public RunbookTriggerEventPayload() {
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public List<String> getFindingIds() {
        return findingIds;
    }

    public void setFindingIds(List<String> findingIds) {
        this.findingIds = findingIds;
    }

    public KafkaTopic getDestTopic() {
        return destTopic;
    }

    public void setDestTopic(KafkaTopic destTopic) {
        this.destTopic = destTopic;
    }

    public RunbookTriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(RunbookTriggerType triggerType) {
        this.triggerType = triggerType;
    }
    

}
