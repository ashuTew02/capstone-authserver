package com.capstone.authServer.service.github.update;

import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;

public interface GitHubFindingUpdateService {
    ScanToolType getToolType();
    void updateFinding(String owner, String repo, Long alertNumber, FindingState findingState, String id);
}
