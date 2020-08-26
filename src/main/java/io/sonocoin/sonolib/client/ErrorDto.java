package io.sonocoin.sonolib.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDto {

    public Boolean errorFlag;
    public String errorString;
    public String errorCode;

}
