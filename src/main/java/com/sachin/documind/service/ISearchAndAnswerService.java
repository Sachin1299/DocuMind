package com.sachin.documind.service;

import com.sachin.documind.dto.LlmResponse;
import com.sachin.documind.dto.QaRequest;

public interface ISearchAndAnswerService {
    LlmResponse searchAndAnswer(QaRequest request);
}
