package com.covoiturage.common.response;

public record BookingResponse(
        boolean success,
        String bookingId,
        String message
) {
    public static BookingResponse success(String bookingId, String message) {
        return new BookingResponse(true, bookingId, message);
    }

    public static BookingResponse failed(String message) {
        return new BookingResponse(false, null, message);
    }
}