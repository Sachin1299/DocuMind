package com.sachin.documind.service;

import java.util.List;

public interface ILlmService {
    List<Double> generateEmbedding(String text);
    List<List<Double>> generateEmbeddings(List<String> texts);
    String generateAnswer(String context, String question);
}
