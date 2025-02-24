package com.hackathon.blockchain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateSmartContractDto {
    @NotBlank
    private String name;

    @NotBlank
    private String conditionExpression;

    @NotBlank
    private String action;

    @NotNull
    private Double actionValue;

    @NotNull
    @Positive
    private Long issuerWalletId;
}
