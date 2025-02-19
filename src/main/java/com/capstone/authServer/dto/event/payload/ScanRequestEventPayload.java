package com.capstone.authServer.dto.event.payload;

import com.capstone.authServer.model.KafkaTopic;
import com.capstone.authServer.model.Tool;

public final class ScanRequestEventPayload {
    private Tool tool;
    private String owner;
    private String repository;
    private Long tenantId;
    KafkaTopic destTopic;

    public KafkaTopic getDestTopic() {
        return destTopic;
    }

    public void setDestTopic(KafkaTopic destTopic) {
        this.destTopic = destTopic;
    }

    public ScanRequestEventPayload(Tool tool, Long tenantId, String owner, String repository, KafkaTopic destTopic) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.owner = owner;
        this.repository = repository;
        this.destTopic = destTopic;
    }

    public ScanRequestEventPayload() {
    }

    public Tool getTool() {
        return tool;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepository() {
        return repository;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
