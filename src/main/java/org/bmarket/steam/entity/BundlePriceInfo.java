package org.bmarket.steam.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundlePriceInfo {
    private String name;
    //Price of set if you buy it entirely. Can be one item or a set for persona
    private Item purchasedBundle;
    //The price of set if you buy things separately and then put them together in one set
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Item assembledBundle;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Item> items;

    public void setPurchasedBundleLowestPrice(BigDecimal lowestPrice) {
        if (Objects.isNull(purchasedBundle)) {
            purchasedBundle = Item.builder()
                    .lowestPrice(lowestPrice)
                    .medianPrice(BigDecimal.ZERO)
                    .build();
        } else {
            purchasedBundle.setLowestPrice(lowestPrice);
        }
    }

    public void setPurchasedBundleMedianPrice(BigDecimal medianPrice) {
        if (Objects.isNull(purchasedBundle)) {
            purchasedBundle = Item.builder()
                    .lowestPrice(BigDecimal.ZERO)
                    .medianPrice(medianPrice)
                    .build();
        } else {
            purchasedBundle.setMedianPrice(medianPrice);
        }
    }

    public void increaseAssembledBundleLowestPrice(BigDecimal lowestPrice) {
        if (Objects.isNull(assembledBundle)) {
            assembledBundle = Item.builder()
                    .lowestPrice(BigDecimal.ZERO)
                    .medianPrice(BigDecimal.ZERO)
                    .build();
        }
        assembledBundle.increaseLowestPrice(lowestPrice);
    }

    public void increaseAssembledBundleMedianPrice(BigDecimal medianPrice) {
        if (Objects.isNull(assembledBundle)) {
            assembledBundle = Item.builder()
                    .lowestPrice(BigDecimal.ZERO)
                    .medianPrice(BigDecimal.ZERO)
                    .build();
        }
        assembledBundle.increaseMedianPrice(medianPrice);
    }

    public void resetAssembledBundleLowestPrice() {
        assembledBundle.resetLowestPrice();
    }

    public void resetAssembledBundleMedianPrice() {
        assembledBundle.resetMedianPrice();
    }

    public void addItem(Item item) {
        if (Objects.isNull(items)) {
            items = new ArrayList<>();
        }
        items.add(item);
    }
}
