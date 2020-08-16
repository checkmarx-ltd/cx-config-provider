package com.cx.configprovider.services.interfaces;

import com.cx.configprovider.dto.ConfigObject;
import com.cx.configprovider.dto.interfaces.ConfigProperties;

public interface ConfigProvider {

    public void init(String uid, ConfigSource configSource);

    public ConfigObject getConfigObject(String uid, ConfigProperties propertiesToMap);

    }
