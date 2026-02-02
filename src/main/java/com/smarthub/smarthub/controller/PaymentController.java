package com.smarthub.smarthub.controller;

import com.smarthub.smarthub.domain.dto.PaymentDTO;
import com.smarthub.smarthub.domain.dto.TransactionStatusDTO;
import com.smarthub.smarthub.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentDTO> createPayment(
            HttpServletRequest request,
            @RequestParam("amount") long amount,
            @RequestParam("orderInfo") String orderInfo) {

        // Generate unique order ID
        String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        PaymentDTO paymentDTO = paymentService.createVnPayPayment(request, amount, orderInfo, orderId);
        return ResponseEntity.status(HttpStatus.OK).body(paymentDTO);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<TransactionStatusDTO> vnPayReturn(HttpServletRequest request) {
        int paymentStatus = paymentService.orderReturn(request);

        String orderId = request.getParameter("vnp_TxnRef");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        String bankCode = request.getParameter("vnp_BankCode");
        String payDate = request.getParameter("vnp_PayDate");

        TransactionStatusDTO transactionStatusDTO = TransactionStatusDTO.builder()
                .orderId(orderId)
                .transactionId(transactionId)
                .amount(String.valueOf(Long.parseLong(totalPrice) / 100))
                .bankCode(bankCode)
                .payDate(payDate)
                .build();

        if (paymentStatus == 1) {
            transactionStatusDTO.setStatus("SUCCESS");
            transactionStatusDTO.setMessage("Giao dịch thành công");
        } else if (paymentStatus == 0) {
            transactionStatusDTO.setStatus("FAILED");
            transactionStatusDTO.setMessage("Giao dịch thất bại");
        } else {
            transactionStatusDTO.setStatus("INVALID");
            transactionStatusDTO.setMessage("Chữ ký không hợp lệ");
        }

        return ResponseEntity.status(HttpStatus.OK).body(transactionStatusDTO);
    }
}
