package com.hackathon.blockchain.model;

import com.hackathon.blockchain.dto.ResponseMessageDto;

public class Constants {
    public static final ResponseMessageDto responseInvalidCredential = ResponseMessageDto.builder()
            .message("❌ Invalid credentials")
            .build();
}
