package com.elmobachiadil.airbnb_clone_back.listing.application.dto;

import com.elmobachiadil.airbnb_clone_back.listing.application.dto.sub.PictureDTO;
import com.elmobachiadil.airbnb_clone_back.listing.application.dto.vo.PriceVO;
import com.elmobachiadil.airbnb_clone_back.listing.domain.BookingCategory;

import java.util.UUID;

public record DisplayCardListingDTO(PriceVO price, String location, PictureDTO cover, BookingCategory bookingCategory, UUID publicId) {

}
