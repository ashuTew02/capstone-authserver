package com.capstone.authServer.service.github.update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.model.github.dismissedreason.GithubDependabotDismissedReason;
import com.capstone.authServer.model.github.state.GithubDependabotState;
import com.capstone.authServer.service.mapper.state.GitHubStateMapper;

import reactor.core.publisher.Mono;

@Service
public class GitHubDependabotFindingUpdateService implements GitHubFindingUpdateService {

    private static final String PERSONAL_ACCESS_TOKEN = "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";
    private final WebClient webClient;
    private final GitHubStateMapper<GithubDependabotState, GithubDependabotDismissedReason> dependabotMapper;

    public GitHubDependabotFindingUpdateService(
            WebClient.Builder webClientBuilder,
            GitHubStateMapper<GithubDependabotState, GithubDependabotDismissedReason> dependabotMapper) {

        this.webClient = webClientBuilder
            .baseUrl("https://api.github.com")
            .build();
        this.dependabotMapper = dependabotMapper;
    }

    @Override
    public ScanToolType getToolType() {
        return ScanToolType.DEPENDABOT;
    }

    @Override
    public void updateFinding(String owner, String repo, Long alertNumber, FindingState findingState) {
        // 1) Map to GitHub state/dismissedReason
        GithubDependabotState state = dependabotMapper.mapState(findingState);
        Optional<GithubDependabotDismissedReason> dismissedReasonOpt =
                dependabotMapper.mapDismissedReason(findingState);

        // 2) Prepare request body
        Map<String, Object> body = new HashMap<>();
        body.put("state", state.getValue()); // "open", "dismissed", "fixed", etc.
        dismissedReasonOpt.ifPresent(reason -> body.put("dismissed_reason", reason.getValue()));

        // 3) PATCH request to GitHub Dependabot endpoint
        webClient.patch()
            .uri("/repos/{owner}/{repo}/dependabot/alerts/{alertNumber}", owner, repo, alertNumber)
            .header("Authorization", "Bearer " + PERSONAL_ACCESS_TOKEN)
            .bodyValue(body)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                clientResponse -> clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody ->
                        Mono.error(new RuntimeException("Failed to update Dependabot Alert: " + errorBody))
                    )
            )
            .bodyToMono(Void.class)
            .block();
    }
}
