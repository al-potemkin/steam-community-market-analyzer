package org.bmarket.steam.service;

import org.bmarket.steam.entity.BundlePriceInfo;
import org.bmarket.steam.exception.ComparisonException;
import org.bmarket.steam.exception.ItemNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class CostAnalysisService {

    public static final String COMPARISON_EXCEPTION_MESSAGE = "Comparison tier not found";

    public BundlePriceInfo findLowestPriceBundle(List<BundlePriceInfo> bundlePriceInfo) {
        var lowestPriceAssembledBundle = bundlePriceInfo.stream()
                .filter(bpi -> Objects.nonNull(bpi.getAssembledBundle()))
                .min(Comparator.comparing(bpi -> bpi.getAssembledBundle().getLowestPrice()));

        var lowestPricePurchasedBundle = bundlePriceInfo.stream()
                .filter(bpi -> Objects.nonNull(bpi.getPurchasedBundle()))
                .min(Comparator.comparing(bpi -> bpi.getPurchasedBundle().getLowestPrice()));

        if (lowestPriceAssembledBundle.isPresent()
                && lowestPricePurchasedBundle.isPresent()) {
            var assembledLowestPrice = lowestPriceAssembledBundle.get()
                    .getAssembledBundle()
                    .getLowestPrice();
            var purchasedLowestPrice = lowestPricePurchasedBundle.get()
                    .getPurchasedBundle()
                    .getLowestPrice();
            if (assembledLowestPrice.compareTo(purchasedLowestPrice) < 0) {
                return lowestPriceAssembledBundle.get();
            } else {
                return lowestPricePurchasedBundle.get();
            }
        }

        if (lowestPriceAssembledBundle.isEmpty()
                && lowestPricePurchasedBundle.isPresent()) {
            return lowestPricePurchasedBundle.get();
        }

        if (lowestPricePurchasedBundle.isEmpty()
                && lowestPriceAssembledBundle.isPresent()) {
            return lowestPriceAssembledBundle.get();
        }
        throw new ItemNotFoundException("There are no sets matching the search criteria in the list");
    }

    public List<String> compareTiers(List<BundlePriceInfo> bundlePriceInfo,
                                     int numberOfItemsToRecycle) {
        var conclusion = new ArrayList<String>();
        var multiplicationNumber = new BigDecimal(numberOfItemsToRecycle);
        for (int i = bundlePriceInfo.size(); i > 1; i--) {
            int higherTier = i;
            int lowerTier = i - 1;

            var higherTierBundle = bundlePriceInfo.stream()
                    .filter(bpi -> higherTier == bpi.getTier())
                    .findFirst()
                    .orElseThrow(() -> new ComparisonException(COMPARISON_EXCEPTION_MESSAGE));
            var higherTierBundlePrice = higherTierBundle.getLowestBundlePrice();

            var lowerTierBundle = bundlePriceInfo.stream()
                    .filter(bpi -> lowerTier == bpi.getTier())
                    .findFirst()
                    .orElseThrow(() -> new ComparisonException(COMPARISON_EXCEPTION_MESSAGE));
            var lowerTierBundlePrice = lowerTierBundle.getLowestBundlePrice();

            var amountToConvert = lowerTierBundlePrice.multiply(multiplicationNumber);

            if (higherTierBundlePrice.compareTo(amountToConvert) > 0) {
                conclusion.add(String.format("Bundle [%s], |tier-%s| (price: %s) is profitable to purchase by conversion " +
                                "%s piece [%s] bundles, |tier-%s| (price: %s, total: 5 * price = %s)",
                        higherTierBundle.getName(), higherTierBundle.getTier(), higherTierBundlePrice,
                        numberOfItemsToRecycle, lowerTierBundle.getName(), lowerTierBundle.getTier(),
                        lowerTierBundlePrice, amountToConvert));
            } else {
                conclusion.add(String.format("Bundle [%s], |tier-%s| (price: %s) is cheaper than " +
                                "%s [%s] bundles, |tier-%s| (price: %s, total: 5 * price = %s)",
                        higherTierBundle.getName(), higherTierBundle.getTier(), higherTierBundlePrice,
                        numberOfItemsToRecycle, lowerTierBundle.getName(), lowerTierBundle.getTier(),
                        lowerTierBundlePrice, amountToConvert));
            }
        }
        return conclusion;
    }
}
