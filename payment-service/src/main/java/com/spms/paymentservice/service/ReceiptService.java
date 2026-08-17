package com.spms.paymentservice.service;

import com.spms.paymentservice.client.*;
import com.spms.paymentservice.dto.ReceiptResponse;
import com.spms.paymentservice.entity.Payment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Builds a full itemized receipt for a payment by calling out to User,
 * Vehicle, and Parking Service (over Eureka) and merging their data with
 * the Payment record. Each downstream call is independently optional —
 * if one service is down, its fields simply come back null on the
 * receipt rather than the whole receipt failing.
 */
@Service
public class ReceiptService {

    private final PaymentService paymentService;
    private final UserServiceClient userServiceClient;
    private final VehicleServiceClient vehicleServiceClient;
    private final ParkingServiceClient parkingServiceClient;

    public ReceiptService(PaymentService paymentService,
                           UserServiceClient userServiceClient,
                           VehicleServiceClient vehicleServiceClient,
                           ParkingServiceClient parkingServiceClient) {
        this.paymentService = paymentService;
        this.userServiceClient = userServiceClient;
        this.vehicleServiceClient = vehicleServiceClient;
        this.parkingServiceClient = parkingServiceClient;
    }

    public ReceiptResponse generateReceipt(Long paymentId) {
        Payment payment = paymentService.findPaymentOrThrow(paymentId);

        ReceiptResponse receipt = new ReceiptResponse();
        receipt.setPaymentId(payment.getId());
        receipt.setTransactionRef(payment.getTransactionRef());
        receipt.setStatus(payment.getStatus());
        receipt.setAmount(payment.getAmount());
        receipt.setMaskedCardNumber(payment.getMaskedCardNumber());
        receipt.setUserId(payment.getUserId());
        receipt.setVehicleId(payment.getVehicleId());
        receipt.setParkingSpaceId(payment.getParkingSpaceId());
        receipt.setGeneratedAt(LocalDateTime.now());

        userServiceClient.getUser(payment.getUserId()).ifPresent(user -> {
            receipt.setUserName(user.getName());
            receipt.setUserEmail(user.getEmail());
        });

        vehicleServiceClient.getVehicle(payment.getVehicleId()).ifPresent(vehicle -> {
            receipt.setVehiclePlateNumber(vehicle.getPlateNumber());
            receipt.setVehicleModel(vehicle.getModel());
        });

        vehicleServiceClient.getLatestCompletedLog(payment.getVehicleId())
                .ifPresent(log -> receipt.setDurationMinutes(log.getDurationMinutes()));

        parkingServiceClient.getSpace(payment.getParkingSpaceId()).ifPresent(space -> {
            receipt.setParkingLocation(space.getLocation());
            receipt.setParkingZone(space.getZone());
        });

        return receipt;
    }
}
