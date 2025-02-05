package com.capstone.authServer.service.github.update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.model.github.dismissedreason.GithubSecretScanDismissedReason;
import com.capstone.authServer.model.github.state.GithubSecretScanState;
import com.capstone.authServer.service.mapper.state.GitHubStateMapper;

import reactor.core.publisher.Mono;

@Service
public class GitHubSecretScanFindingUpdateService implements GitHubFindingUpdateService {

    private static final String PERSONAL_ACCESS_TOKEN = "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";
    private final WebClient webClient;
    private final GitHubStateMapper<GithubSecretScanState, GithubSecretScanDismissedReason> secretScanMapper;

    public GitHubSecretScanFindingUpdateService(
            WebClient.Builder webClientBuilder,
            GitHubStateMapper<GithubSecretScanState, GithubSecretScanDismissedReason> secretScanMapper) {

        this.webClient = webClientBuilder
            .baseUrl("https://api.github.com")
            .build();
        this.secretScanMapper = secretScanMapper;
    }

    @Override
    public ScanToolType getToolType() {
        return ScanToolType.SECRET_SCAN;
    }

    @Override
    public void updateFinding(String owner, String repo, Long alertNumber, FindingState findingState) {
        // 1) Map to GitHub state (no dismissed reason for secrets)
        GithubSecretScanState state = secretScanMapper.mapState(findingState);
        Optional<GithubSecretScanDismissedReason> dismissedReasonOpt = secretScanMapper.mapDismissedReason(findingState);
        // 2) Prepare request body
        Map<String, Object> body = new HashMap<>();
        body.put("state", state.getValue());  // "open" or "resolved"
        dismissedReasonOpt.ifPresent(reason -> body.put("resolution", reason.getValue()));
        // 3) PATCH request to GitHub Secret Scanning endpoint
        webClient.patch()
            .uri("/repos/{owner}/{repo}/secret-scanning/alerts/{alertNumber}", owner, repo, alertNumber)
            .header("Authorization", "Bearer " + PERSONAL_ACCESS_TOKEN)
            .bodyValue(body)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                clientResponse -> clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody ->
                        Mono.error(new RuntimeException("Failed to update Secret Scanning Alert: " + errorBody))
                    )
            )
            .bodyToMono(Void.class)
            .block();
    }
}
