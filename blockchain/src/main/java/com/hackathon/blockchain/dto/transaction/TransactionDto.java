package com.hackathon.blockchain.dto.transaction;

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
    private Date timestamp;
    private String status;
    private Double fee;
    private Long senderWalletId;
    private Long receiverWalletId;
}
