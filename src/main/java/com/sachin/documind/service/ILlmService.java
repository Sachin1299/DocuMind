package com.sachin.documind.service;

import com.sachin.documind.dto.LlmResponse;
import java.util.List;

public interface ILlmService {
    List<Double> generateEmbedding(String text);
    List<List<Double>> generateEmbeddings(List<String> texts);
    LlmResponse generateAnswer(String context, String question);
}
