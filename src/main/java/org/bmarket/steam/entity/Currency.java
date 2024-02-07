package org.bmarket.steam.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    USD("1", "$"),
    GBP("2", "£"),
    EUR("3", "€"),
    CHF("4", "CHF "),
    RUB("5", " pуб."),
    PLN("6", "zł"),
    BRL("7", "R$ "),
    JPY("8", "¥ "),
    NOK("9", " kr"),
    IDR("10", "Rp "),
    MYR("11", "RM"),
    PHP("12", "P"),
    SGD("13", "S$"),
    THB("14", "฿"),
    VND("15", "₫"),
    KRW("16", "₩ "),
    TRY("17", " TL"),
    UAH("18", "₴"),
    MXN("19", "Mex$ "),
    CAD("20", "CDN$ "),
    AUD("21", "A$ "),
    NZD("22", "NZ$ "),
    CNY("23", "¥ "),
    INR("24", "₹ "),
    CLP("25", "CLP$ "),
    PEN("26", "S/."),
    COP("27", "COL$ "),
    ZAR("28", "R "),
    HKD("29", "HK$ "),
    TWD("30", "NT$ "),
    SAR("31", " SR"),
    AED("32", " AED"),
//    SEK("33", null),
    ARS("34", "ARS$ "),
    ILS("35", "₪"),
//    BYN("36", null),
    KZT("37", "₸"),
    KWD("38", " KD"),
    QAR("39", " QR"),
    CRC("40", "₡"),
    UYU("41", "$");
//    BGN("42", null),
//    HRK("43", null),
//    CZK("44", null),
//    DKK("45", null),
//    HUF("46", null),
//    RON("47", null);

    private final String code;
    private final String symbol;
}
