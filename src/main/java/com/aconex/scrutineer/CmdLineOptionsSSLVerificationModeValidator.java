package com.aconex.scrutineer;

import java.util.Arrays;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

public class CmdLineOptionsSSLVerificationModeValidator implements IValueValidator {
    private final String[] sslVerificationModes = {"none", "certificate", "full"};

    @Override
    public void validate(String name, Object value) throws ParameterException {
        if (!Arrays.asList(sslVerificationModes).contains(value)) {
            throw new ParameterException(String.format("Invalid SSL Verification mode '%s' - can be one of: %s", value, Arrays.toString(sslVerificationModes)));
        }
    }
}
