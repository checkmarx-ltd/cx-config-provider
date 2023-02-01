package com.checkmarx.configprovider.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GeneralRepoDto {
    private String srcUrl;
    private String srcUserName;
    private String srcPass;
    private String srcRef;
    private String srcPrivateKey;
    private ProtocolType protocolType;
}
