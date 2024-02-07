package org.bmarket.steam.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Application {
    // https://developer.valvesoftware.com/wiki/Steam_Application_IDs
    DOTA_2("570");

    private final String applicationId;
}
