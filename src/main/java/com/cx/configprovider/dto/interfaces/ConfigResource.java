package com.cx.configprovider.dto.interfaces;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;

import javax.naming.ConfigurationException;

public interface ConfigResource {

    Config parse() throws ConfigurationException;
}
