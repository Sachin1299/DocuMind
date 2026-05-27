package com.sachin.documind.service.impl;

import com.sachin.documind.dto.LlmResponse;
import com.sachin.documind.dto.QaRequest;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.SearchResult;
import com.sachin.documind.dto.UserQueryPayload;
import com.sachin.documind.entity.ChatHistory;
import com.sachin.documind.entity.User;
import com.sachin.documind.repository.ChatHistoryRepository;
import com.sachin.documind.repository.IVectorDbRepository;
import com.sachin.documind.repository.UserRepository;
import com.sachin.documind.service.ILlmService;
import com.sachin.documind.service.ISearchAndAnswerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchAndAnswerServiceImpl implements ISearchAndAnswerService {
    private final ILlmService llmService;
    private final IVectorDbRepository vectorDbRepository;
    private final UserRepository userRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    public SearchAndAnswerServiceImpl(ILlmService llmService, IVectorDbRepository vectorDbRepository, UserRepository userRepository, ChatHistoryRepository chatHistoryRepository) {
        this.llmService = llmService;
        this.vectorDbRepository = vectorDbRepository;
        this.userRepository = userRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    @Override
    public LlmResponse searchAndAnswer(QaRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Long userId = user.getId();

        List<Double> questionEmbedding = llmService.generateEmbedding(request.question());

        List<Map<String, Object>> mustConditions = new ArrayList<>();
        mustConditions.add(Map.of(
                "key", "userId",
                "match", Map.of("value", userId)
        ));

        if (request.documentIds() != null && !request.documentIds().isEmpty()) {
            mustConditions.add(Map.of(
                    "key", "documentId",
                    "match", Map.of("any", request.documentIds())
            ));
        }

        Map<String, Object> filter = Map.of("must", mustConditions);

        UserQueryPayload payload = new UserQueryPayload(questionEmbedding, 10, true, filter);
        QdrantSearchResponse searchResponse = vectorDbRepository.searchData(payload);
        
        StringBuilder contextBuilder = new StringBuilder();
        if (searchResponse != null && searchResponse.result() != null) {
            for (SearchResult result : searchResponse.result()) {
                if (result.payload() != null && result.payload().content() != null) {
                    contextBuilder.append(result.payload().content()).append("\n");
                }
            }
        }
        
        if (request.documentText() != null && !request.documentText().isBlank()) {
            contextBuilder.append("\nAdditional Document Context:\n").append(request.documentText());
        }
        
        
        LlmResponse llmResponse = llmService.generateAnswer(contextBuilder.toString(), request.question());
        ChatHistory chatHistory = new ChatHistory();
        //user.getChatHistory().getHistory();
        
        return llmResponse;
    }
}
