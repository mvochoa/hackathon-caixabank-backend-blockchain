package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.CreateSmartContractDto;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.model.SmartContract;
import com.hackathon.blockchain.service.SmartContractEvaluationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<SmartContract> create(@Valid @RequestBody CreateSmartContractDto createSmartContractDto) {
        return new ResponseEntity<>(
                smartContractEvaluationService.create(createSmartContractDto),
                HttpStatus.OK);
    }

    @GetMapping("/validate/{id}")
    public ResponseEntity<ResponseMessageDto> validate(@PathVariable Long id) {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(smartContractEvaluationService.verifyContractSignature(id))
                        .build(),
                HttpStatus.OK);
    }

}