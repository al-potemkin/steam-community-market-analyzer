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
public class Item {
    private String name;

    //Price of set if you buy it entirely
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Item purchasedBundle;
    //The price of set if you buy things separately and then put them together in one set
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Item assembledBundle;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal lowestPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal medianPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Item> items;

    public void addItem(Item item) {
        if (Objects.isNull(items)) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    public void increaseLowestPrice(BigDecimal itemLowestPrice) {
        if (Objects.isNull(lowestPrice)) {
            lowestPrice = BigDecimal.ZERO;
        }
        lowestPrice = lowestPrice.add(itemLowestPrice);
    }

    public void increaseMedianPrice(BigDecimal itemMedianPrice) {
        if (Objects.isNull(medianPrice)) {
            medianPrice = BigDecimal.ZERO;
        }
        medianPrice = medianPrice.add(itemMedianPrice);
    }

    public void resetLowestPrice() {
        lowestPrice = BigDecimal.ZERO;
    }

    public void resetMedianPrice() {
        medianPrice = BigDecimal.ZERO;
    }
}
