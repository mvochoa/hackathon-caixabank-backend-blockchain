package com.hackathon.blockchain.controller;

import com.hackathon.blockchain.dto.BlockchainDto;
import com.hackathon.blockchain.dto.ResponseMessageDto;
import com.hackathon.blockchain.service.BlockchainService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/blockchain")
public class BlockchainController {

    private final BlockchainService blockchainService;

    @PostMapping("/mine")
    public ResponseEntity<ResponseMessageDto> mine() {
        return new ResponseEntity<>(
                ResponseMessageDto.builder()
                        .message(blockchainService.mine())
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BlockchainDto>> history() {
        return new ResponseEntity<>(
                blockchainService.history(),
                HttpStatus.OK);
    }

}