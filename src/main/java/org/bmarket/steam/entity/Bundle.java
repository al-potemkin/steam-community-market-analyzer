package org.bmarket.steam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bundle {
    private String name;
    private int tier;
    private boolean bundle;
    private List<String> items;
}
