package com.spms.userservice.controller;

import com.spms.userservice.dto.BookingRequest;
import com.spms.userservice.dto.BookingResponse;
import com.spms.userservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> addBooking(@PathVariable Long userId,
                                                        @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.addBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /users/{userId}/bookings
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookingHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingHistory(userId));
    }
}
