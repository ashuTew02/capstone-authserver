package com.capstone.authServer.service.github.update;

import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;

public interface GitHubFindingUpdateService {
    /**
     * @return which GitHub tool (CODE_SCAN, DEPENDABOT, SECRET_SCAN, etc.)
     */
    ScanToolType getToolType();

    /**
     * Update a GitHub finding for the given tool.
     *
     * @param owner        The GitHub repo owner
     * @param repo         The GitHub repository name
     * @param personalAccessToken The GitHub PAT to authenticate
     * @param alertNumber  The alert ID in GitHub
     * @param findingState The new state to set
     * @param esFindingId  The ID used in Elasticsearch (or DB)
     */
    void updateFinding(String owner,
                       String repo,
                       String personalAccessToken,
                       Long alertNumber,
                       FindingState findingState,
                       String esFindingId,
                       Long tenantId);
}
