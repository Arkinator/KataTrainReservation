package de.zdf.service.pwValidation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PwValidationResult {
    private boolean pwValid;
    private String validationMessage;
}
