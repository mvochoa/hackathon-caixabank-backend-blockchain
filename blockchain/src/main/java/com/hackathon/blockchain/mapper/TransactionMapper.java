package com.hackathon.blockchain.mapper;

import com.hackathon.blockchain.dto.transaction.TransactionDto;
import com.hackathon.blockchain.model.Transaction;

public class TransactionMapper {
    public static TransactionDto entityToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .assetSymbol(transaction.getAssetSymbol())
                .amount(transaction.getAmount())
                .pricePerUnit(transaction.getPricePerUnit())
                .type(transaction.getType())
                .timestamp(transaction.getTimestamp())
                .fee(transaction.getFee())
                .senderWalletId(transaction.getSenderWallet().getId())
                .receiverWalletId(transaction.getReceiverWallet().getId())
                .build();
    }
}
