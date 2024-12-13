package com.elmobachiadil.airbnb_clone_back.listing.application.dto.sub;

import com.elmobachiadil.airbnb_clone_back.listing.application.dto.vo.DescriptionVO;
import com.elmobachiadil.airbnb_clone_back.listing.application.dto.vo.TitleVO;
import jakarta.validation.constraints.NotNull;

public record DescriptionDTO(
        @NotNull TitleVO title,
        @NotNull DescriptionVO description
) {

}
