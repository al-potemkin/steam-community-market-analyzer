package org.bmarket.steam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.bmarket.steam.entity.*;
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

    public Item getLowestPriceBundle(List<BundlePriceInfo> bundles) {
        return bundles.stream()
                .map(BundlePriceInfo::getAssembledBundle)
                .min(Comparator.comparing(Item::getLowestPrice))
                .orElseThrow(() -> new ItemNotFoundException("Bundle not found"));
    }

    public List<BundlePriceInfo> calculateBundlePrices(List<Bundle> items,
                                                       Currency currency,
                                                       Application application) {
//        var items = itemParser.parseJsonWithItems();
        var itemPriceList = new ArrayList<BundlePriceInfo>();

        for (var bundle : items) {
            itemPriceList.add(collectBundlePriceInfo(bundle, currency, application));
        }
        return itemPriceList;
    }

    private BundlePriceInfo collectBundlePriceInfo(Bundle bundle,
                                                   Currency currency,
                                                   Application application) {
        var bundlePriceInfo = requestDefaultBundlePriceInfo(bundle, currency, application);

        if (bundle.isBundle()) {
            var lowestPricePresent = true;
            var medianPricePresent = true;

            for (var itemName : bundle.getItems()) {
                var prices = marketService.getItemPriceInformation(itemName, currency, application);

                var item = Item.builder().name(itemName).build();
                if (Objects.nonNull(prices.getLowestPrice())) {
                    var lowestPrice = convertStringPriceToBigDecimal(prices.getLowestPrice());
                    item.setLowestPrice(lowestPrice);
                    if (lowestPricePresent) {
                        bundlePriceInfo.increaseAssembledBundleLowestPrice(lowestPrice);
                    }
                } else {
                    lowestPricePresent = false;
                    bundlePriceInfo.resetAssembledBundleLowestPrice();
                }

                if (Objects.nonNull(prices.getMedianPrice())) {
                    var medianPrice = convertStringPriceToBigDecimal(prices.getMedianPrice());
                    item.setMedianPrice(medianPrice);
                    if (medianPricePresent) {
                        bundlePriceInfo.increaseAssembledBundleMedianPrice(medianPrice);
                    }
                } else {
                    medianPricePresent = false;
                    bundlePriceInfo.resetAssembledBundleMedianPrice();
                }
                bundlePriceInfo.addItem(item);
            }
        }
        return bundlePriceInfo;
    }

    private BundlePriceInfo requestDefaultBundlePriceInfo(Bundle bundle, Currency currency, Application application) {
        var bundlePriceInfo = BundlePriceInfo.builder()
                .name(bundle.getName())
                .build();

        var bundlePrice = marketService.getItemPriceInformation(bundle.getName(), currency, application);
        if (Objects.nonNull(bundlePrice.getLowestPrice())) {
            var lowestPrice = convertStringPriceToBigDecimal(bundlePrice.getLowestPrice());
            bundlePriceInfo.setPurchasedBundleLowestPrice(lowestPrice);
        }
        if (Objects.nonNull(bundlePrice.getMedianPrice())) {
            var medianPrice = convertStringPriceToBigDecimal(bundlePrice.getMedianPrice());
            bundlePriceInfo.setPurchasedBundleMedianPrice(medianPrice);
        }
        return bundlePriceInfo;
    }

    private BigDecimal convertStringPriceToBigDecimal(String price) {
        return new BigDecimal(price
                .replace(" ", Strings.EMPTY)
                .replace("₴", Strings.EMPTY)
                .replace(",", "."));
    }
}
