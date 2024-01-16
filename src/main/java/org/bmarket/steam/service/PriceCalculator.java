package org.bmarket.steam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bmarket.steam.entity.Application;
import org.bmarket.steam.entity.Bundle;
import org.bmarket.steam.entity.Currency;
import org.bmarket.steam.entity.Item;
import org.bmarket.steam.exception.ItemNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class PriceCalculator {
    private final ItemParser itemParser;
    private final MarketService marketService;

    public Item getLowestPriceBundle(List<Item> bundles) {
        return bundles.stream()
                .min(Comparator.comparing(Item::getLowestPrice))
                .orElseThrow(() -> new ItemNotFoundException("Bundle not found"));
    }

    public List<Item> calculateBundlePrices(List<Bundle> items,
                                            Currency currency,
                                            Application application) {
//        var items = itemParser.parseJsonWithItems();
        var itemPriceList = new ArrayList<Item>();

        for (var bundle : items) {
            itemPriceList.add(collectBundlePriceInfo(bundle, currency, application));
        }
        return itemPriceList;
    }

    private Item collectBundlePriceInfo(Bundle bundle,
                                        Currency currency,
                                        Application application) {
        var bundlePriceInfo = Item.builder()
                .name(bundle.getName())
                .build();

        var lowestPricePresent = true;
        var medianPricePresent = true;

        for (var itemName : bundle.getItems()) {
            var prices = marketService.getItemPriceInformation(itemName, currency, application);

            var item = Item.builder().name(itemName).build();
            if (Objects.nonNull(prices.getLowestPrice())) {
                var lowestPrice = convertStringPriceToBigDecimal(prices.getLowestPrice());
                item.setLowestPrice(lowestPrice);
                if (lowestPricePresent) {
                    bundlePriceInfo.increaseLowestPrice(lowestPrice);
                }
            } else {
                lowestPricePresent = false;
                bundlePriceInfo.resetLowestPrice();
            }

            if (Objects.nonNull(prices.getMedianPrice())) {
                var medianPrice = convertStringPriceToBigDecimal(prices.getMedianPrice());
                item.setMedianPrice(medianPrice);
                if (medianPricePresent) {
                    bundlePriceInfo.increaseMedianPrice(medianPrice);
                }
            } else {
                medianPricePresent = false;
                bundlePriceInfo.resetMedianPrice();
            }
            bundlePriceInfo.addItem(item);
        }
        return bundlePriceInfo;
    }

    private BigDecimal convertStringPriceToBigDecimal(String price) {
        return new BigDecimal(price
                .replace("₴", "")
                .replace(",", "."));
    }
}
