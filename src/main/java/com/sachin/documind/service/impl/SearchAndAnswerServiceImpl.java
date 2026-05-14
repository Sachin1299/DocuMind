package com.sachin.documind.service.impl;

import com.sachin.documind.dto.QaRequest;
import com.sachin.documind.dto.QdrantSearchResponse;
import com.sachin.documind.dto.SearchResult;
import com.sachin.documind.dto.UserQueryPayload;
import com.sachin.documind.repository.IVectorDbRepository;
import com.sachin.documind.service.ILlmService;
import com.sachin.documind.service.ISearchAndAnswerService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchAndAnswerServiceImpl implements ISearchAndAnswerService {
    private final ILlmService llmService;
    private final IVectorDbRepository vectorDbRepository;

    public SearchAndAnswerServiceImpl(ILlmService llmService, IVectorDbRepository vectorDbRepository) {
        this.llmService = llmService;
        this.vectorDbRepository = vectorDbRepository;
    }

    @Override
    public String searchAndAnswer(QaRequest request) {
        List<Double> questionEmbedding = llmService.generateEmbedding(request.question());
        UserQueryPayload payload = new UserQueryPayload(questionEmbedding, 3, true);
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

        return llmService.generateAnswer(contextBuilder.toString(), request.question());
    }
}
