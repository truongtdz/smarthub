package com.smarthub.smarthub.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusDTO {
    private String status;
    private String message;
    private String orderId;
    private String transactionId;
    private String amount;
    private String bankCode;
    private String payDate;
}