package com.elmobachiadil.airbnb_clone_back.booking.mapper;

import com.elmobachiadil.airbnb_clone_back.booking.application.dto.BookedDateDTO;
import com.elmobachiadil.airbnb_clone_back.booking.application.dto.NewBookingDTO;
import com.elmobachiadil.airbnb_clone_back.booking.domain.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking newBookingToBooking(NewBookingDTO newBookingDTO);

    BookedDateDTO bookingToCheckAvailability(Booking booking);

}
