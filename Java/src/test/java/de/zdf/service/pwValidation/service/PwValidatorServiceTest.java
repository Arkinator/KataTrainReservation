package de.zdf.service.pwValidation.service;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PwValidatorServiceTest {
    private PwValidatorService pwValidatorService;

    @Before
    public void initializePwService() {
        pwValidatorService = new PwValidatorService();
        pwValidatorService.buildPattern();
    }

    @Test
    public void shouldReturnTrueForPassword() {
        assertThat(pwValidatorService.validatePassword("Pa$$w0rd"))
                .isEqualTo(true);
    }

    @Test
    public void shouldReturnFalseIfTooLong() {
        assertThat(pwValidatorService.validatePassword("123456789012345678901234567890aA#"))
                .isEqualTo(false);
    }

    @Test
    public void shouldReturnTrueForSimplePassword() {
        assertThat(pwValidatorService.validatePassword("Pa##w0rd"))
                .isEqualTo(true);
    }
}