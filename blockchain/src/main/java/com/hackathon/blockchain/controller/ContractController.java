package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.SmartContractDto;
import com.hackathon.blockchain.service.SmartContractEvaluationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/contracts")
public class ContractController {

    private final SmartContractEvaluationService smartContractEvaluationService;

    @PostMapping("/create")
    public ResponseEntity<SmartContractDto> create(@Valid @RequestBody SmartContractDto smartContractDto) {
        return new ResponseEntity<>(
                smartContractEvaluationService.create(smartContractDto),
                HttpStatus.OK);
    }

}