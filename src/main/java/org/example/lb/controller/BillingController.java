package org.example.lb.controller;


import org.example.lb.service.AuthService;
import org.example.lb.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/billing")
public class BillingController {
    @Autowired
    private BillingService billingService;
    @GetMapping("/commissions/pdf")
    public ResponseEntity<byte[]> generateCommissionReport(@RequestParam("login") String login,
                                                           @RequestParam("password") String password) {
        byte[] pdfBytes =billingService.generatePdf(login,password);
        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "commission_report.pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
