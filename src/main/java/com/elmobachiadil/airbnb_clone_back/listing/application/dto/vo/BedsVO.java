package com.elmobachiadil.airbnb_clone_back.listing.application.dto.vo;

import jakarta.validation.constraints.NotNull;

public record BedsVO(@NotNull(message = "Beds value must be present") int value) {

}
