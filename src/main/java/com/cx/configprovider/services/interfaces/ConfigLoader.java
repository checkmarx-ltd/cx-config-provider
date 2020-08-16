package com.cx.configprovider.services.interfaces;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;

public interface ConfigLoader {
    public RawConfigAsCode getConfigAsCode(ConfigLocation configLocation);
}
