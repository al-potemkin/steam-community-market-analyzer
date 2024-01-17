package org.bmarket.steam.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    private BigDecimal lowestPrice;
    private BigDecimal medianPrice;

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
