package de.zdf.service.pwValidation.controller;

import de.zdf.service.pwValidation.data.ReservationResponse;
import de.zdf.service.pwValidation.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerServiceController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/reservationRequest", method = RequestMethod.GET)
    public ReservationResponse reservationResponse(@RequestParam(value = "trainId", required = true) String trainId,
                                                  @RequestParam(value = "numberOfDesiredSeats", required = true) int numberOfDesiredSeats) {
        return customerService.makeReservation(trainId, numberOfDesiredSeats);
    }
}
