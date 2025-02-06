package com.capstone.authServer.service.github.update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.capstone.authServer.model.FindingState;
import com.capstone.authServer.model.ScanToolType;
import com.capstone.authServer.model.github.dismissedreason.GithubCodeScanDismissedReason;
import com.capstone.authServer.model.github.state.GithubCodeScanState;
import com.capstone.authServer.service.ElasticSearchService;
import com.capstone.authServer.service.mapper.state.GitHubStateMapper;

import reactor.core.publisher.Mono;

@Service
public class GitHubCodeScanFindingUpdateService implements GitHubFindingUpdateService {

    @Value("${github.pat}")
    private String PERSONAL_ACCESS_TOKEN;
    
    private final WebClient webClient;
    private final GitHubStateMapper<GithubCodeScanState, GithubCodeScanDismissedReason> codeScanMapper;
    private final ElasticSearchService esService;

    public GitHubCodeScanFindingUpdateService(
            WebClient.Builder webClientBuilder,
            GitHubStateMapper<GithubCodeScanState, GithubCodeScanDismissedReason> codeScanMapper,
            ElasticSearchService esService) {

        this.webClient = webClientBuilder
            .baseUrl("https://api.github.com")
            .build();
        this.codeScanMapper = codeScanMapper;
        this.esService = esService;
    }

    @Override
    public ScanToolType getToolType() {
        return ScanToolType.CODE_SCAN;
    }

    @Override
    public void updateFinding(String owner, String repo, Long alertNumber, FindingState findingState, String id) {
        // 1) Map to GitHub state/dismissedReason
        GithubCodeScanState state = codeScanMapper.mapState(findingState);
        Optional<GithubCodeScanDismissedReason> dismissedReasonOpt = codeScanMapper.mapDismissedReason(findingState);

        // 2) Prepare request body
        Map<String, Object> body = new HashMap<>();
        body.put("state", state.getValue());  // "open" or "dismissed"
        dismissedReasonOpt.ifPresent(reason -> body.put("dismissed_reason", reason.getValue()));

        // 3) PATCH request to GitHub Code Scanning endpoint
        webClient.patch()
            .uri("/repos/{owner}/{repo}/code-scanning/alerts/{alertNumber}", owner, repo, alertNumber)
            .header("Authorization", "Bearer " + PERSONAL_ACCESS_TOKEN)
            .bodyValue(body)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                clientResponse -> clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody ->
                        Mono.error(new RuntimeException("Failed to update Code Scanning Alert: " + errorBody))
                    )
            )
            .bodyToMono(Void.class)
            .block();

        esService.updateFindingStateByFindingId(id, findingState);
    }
}
