package org.bmarket.steam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bmarket.steam.entity.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Parse Json with item list from resource folder
 */
@Service
public class ItemParser {
//    @Value("classpath:items.json")
    private Resource resource;

    public List<Bundle> parseJsonWithItems() {
        try {
            return new ObjectMapper().readValue(resource.getFile(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Json parsing error");
        }
    }
}
