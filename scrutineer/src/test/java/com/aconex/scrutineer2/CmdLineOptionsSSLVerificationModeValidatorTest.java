package com.aconex.scrutineer2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.beust.jcommander.ParameterException;
import org.junit.Before;
import org.junit.Test;

public class CmdLineOptionsSSLVerificationModeValidatorTest {

    private CmdLineOptionsSSLVerificationModeValidator testInstance;

    @Before
    public void setUp() {
        this.testInstance = new CmdLineOptionsSSLVerificationModeValidator();
    }

    @Test
    public void shouldRaiseExceptionWhenSslVerificationModeIsInvalid() {
        String mode = "invalid";

        try {
            this.testInstance.validate("sslVerificationModes", mode);
            fail();
        } catch (ParameterException e) {
            assertEquals("Invalid SSL Verification mode 'invalid' - can be one of: [none, certificate, full]", e.getMessage());
        }
    }

    @Test
    public void shouldSslVerificationModeIsValid() {
        String mode = "certificate";

        this.testInstance.validate("sslVerificationModes", mode);
    }
}
