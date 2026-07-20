package com.stray.animal.rescue.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 特征向量工具类
 */
public class FeatureVectorUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 计算余弦相似度
     */
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    /**
     * 归一化向量
     */
    public static double[] normalize(double[] vector) {
        double max = Arrays.stream(vector).max().orElse(1.0);
        double min = Arrays.stream(vector).min().orElse(0.0);
        
        if (max == min) {
            return vector;
        }
        
        double[] normalized = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = (vector[i] - min) / (max - min);
        }
        
        return normalized;
    }
    
    /**
     * One-Hot编码
     */
    public static double[] oneHotEncode(String value, List<String> categories) {
        double[] encoded = new double[categories.size()];
        int index = categories.indexOf(value);
        if (index >= 0) {
            encoded[index] = 1.0;
        }
        return encoded;
    }
    
    /**
     * 多标签编码
     */
    public static double[] multiLabelEncode(List<String> values, List<String> allLabels) {
        double[] encoded = new double[allLabels.size()];
        for (String value : values) {
            int index = allLabels.indexOf(value);
            if (index >= 0) {
                encoded[index] = 1.0;
            }
        }
        return encoded;
    }
    
    /**
     * 解析JSON为List
     */
    public static List<String> parseJsonArray(String json) {
        if (json == null || json.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    
    /**
     * 解析JSON为Map
     */
    public static Map<String, Integer> parseJsonMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    /**
     * 计算加权Jaccard相似度
     */
    public static double weightedJaccardSimilarity(
            Map<Long, Double> vec1, 
            Map<Long, Double> vec2) {
        
        if (vec1.isEmpty() || vec2.isEmpty()) {
            return 0.0;
        }
        
        double minSum = 0.0;
        double maxSum = 0.0;
        
        // 合并所有键
        for (Long key : vec1.keySet()) {
            double v1 = vec1.get(key);
            double v2 = vec2.getOrDefault(key, 0.0);
            minSum += Math.min(v1, v2);
            maxSum += Math.max(v1, v2);
        }
        
        for (Long key : vec2.keySet()) {
            if (!vec1.containsKey(key)) {
                maxSum += vec2.get(key);
            }
        }
        
        return maxSum > 0 ? minSum / maxSum : 0.0;
    }
    
    /**
     * 计算时间衰减因子
     * 使用指数衰减：e^(-λt)
     */
    public static double calculateTimeDecay(long daysPassed) {
        // λ = 0.1，表示每10天衰减到原来的约37%
        double lambda = 0.1;
        return Math.exp(-lambda * daysPassed);
    }
}
