package com.hackathon.blockchain.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private String assetSymbol;
    private Double amount;
    private Double pricePerUnit;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date timestamp;
    private String status;
    private Double fee;
    private Long senderWalletId;
    private Long receiverWalletId;
}
