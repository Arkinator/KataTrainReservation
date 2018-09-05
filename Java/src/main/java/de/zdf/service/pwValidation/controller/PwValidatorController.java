package de.zdf.service.pwValidation.controller;

import de.zdf.service.pwValidation.data.PwValidationResult;
import de.zdf.service.pwValidation.service.PwValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PwValidatorController {
    @Autowired
    private PwValidatorService pwValidatorService;

    @RequestMapping(value = "/checkPassword",method = RequestMethod.POST)
    public PwValidationResult sdfghjk(@RequestParam(value="password", defaultValue="foobar") String password) {
        if (pwValidatorService.validatePassword(password)) {
            return PwValidationResult.builder()
                    .pwValid(true)
                    .build();
        } else {
            PwValidationResult pwValidationResult = new PwValidationResult();
            pwValidationResult.setPwValid(false);
            return pwValidationResult;
        }
    }

}
