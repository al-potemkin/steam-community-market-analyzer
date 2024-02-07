package org.bmarket.steam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.bmarket.steam.entity.Bundle;
import org.bmarket.steam.entity.BundlePriceInfo;
import org.bmarket.steam.entity.Item;
import org.bmarket.steam.entity.enums.Application;
import org.bmarket.steam.entity.enums.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class PriceCalculator {
    private final MarketService marketService;

    public List<BundlePriceInfo> calculateBundlePrices(List<Bundle> items,
                                                       Currency currency,
                                                       Application application) {
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

        // Some sets are sold together with personalities (it is impossible to buy a separate personality for a character),
        // which makes it difficult to compare the price of the entire set with the sum of the items separately
        //
        // It also allows you to find out the price of an individual item,
        // although for this it is better to use a separate method PriceController#getItemPriceInformation

        if (bundle.isBundle()) { // bundle with a personality is not a bundle && separate item is not a bundle
            findPricesForEachItem(bundle, currency, application, bundlePriceInfo);
        }
        return bundlePriceInfo;
    }

    /**
     * Calculating the total cost of bundle if you buy each item separately
     */
    private void findPricesForEachItem(Bundle bundle, Currency currency, Application application, BundlePriceInfo bundlePriceInfo) {
        var lowestPricePresent = true;
        var medianPricePresent = true; // median price is not always present in response, so if there is no price, then total will no longer count for the bundle

        for (var itemName : bundle.getItems()) {
            var prices = marketService.getItemPriceInformation(itemName, currency, application);
            var item = Item.builder()
                    .name(itemName)
                    .build();

            if (Objects.nonNull(prices.getLowestPrice())) {
                var lowestPrice = convertStringPriceToBigDecimal(prices.getLowestPrice(), currency.getSymbol());
                item.setLowestPrice(lowestPrice);
                if (lowestPricePresent) {
                    bundlePriceInfo.increaseAssembledBundleLowestPrice(lowestPrice);
                }
            } else {
                lowestPricePresent = false;
                bundlePriceInfo.resetAssembledBundleLowestPrice();
            }

            if (Objects.nonNull(prices.getMedianPrice())) {
                var medianPrice = convertStringPriceToBigDecimal(prices.getMedianPrice(), currency.getSymbol());
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

    /**
     * Request for prices if buy the whole set
     */
    private BundlePriceInfo requestDefaultBundlePriceInfo(Bundle bundle, Currency currency, Application application) {
        var bundlePrice = marketService.getItemPriceInformation(bundle.getName(), currency, application);
        var bundlePriceInfo = BundlePriceInfo.builder()
                .name(bundle.getName())
                .build();

        if (Objects.nonNull(bundlePrice.getLowestPrice())) {
            var lowestPrice = convertStringPriceToBigDecimal(bundlePrice.getLowestPrice(), currency.getSymbol());
            bundlePriceInfo.setPurchasedBundleLowestPrice(lowestPrice);
        }
        if (Objects.nonNull(bundlePrice.getMedianPrice())) {
            var medianPrice = convertStringPriceToBigDecimal(bundlePrice.getMedianPrice(), currency.getSymbol());
            bundlePriceInfo.setPurchasedBundleMedianPrice(medianPrice);
        }
        return bundlePriceInfo;
    }

    /**
     * Formats and converts price to BigDecimal type.
     * <p>
     * Example of data to be converted: "$0.87" | "£0.70" | "0,81€" | "CHF 0.76"
     */
    private BigDecimal convertStringPriceToBigDecimal(String price, String currencySymbol) {
        return new BigDecimal(price
                .replace(" ", Strings.EMPTY)
                .replace(currencySymbol, Strings.EMPTY)
                .replace(",", "."));
    }
}
