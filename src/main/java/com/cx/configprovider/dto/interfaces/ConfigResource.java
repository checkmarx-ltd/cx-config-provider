package com.cx.configprovider.dto.interfaces;

import com.typesafe.config.Config;

import javax.naming.ConfigurationException;

public interface ConfigResource {

    Config parse() throws ConfigurationException;
}
