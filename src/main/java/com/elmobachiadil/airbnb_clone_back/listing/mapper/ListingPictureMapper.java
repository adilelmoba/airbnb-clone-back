package com.elmobachiadil.airbnb_clone_back.listing.mapper;

import com.elmobachiadil.airbnb_clone_back.listing.application.dto.sub.PictureDTO;
import com.elmobachiadil.airbnb_clone_back.listing.domain.ListingPicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ListingPictureMapper {


    Set<ListingPicture> pictureDTOsToListingPictures(List<PictureDTO> pictureDTOs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "listing", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "cover", source = "isCover")
    ListingPicture pictureDTOToListingPicture(PictureDTO pictureDTO);

    List<PictureDTO> listingPictureToPictureDTO(List<ListingPicture> listingPictures);

    @Mapping(target = "isCover", source = "cover")
    PictureDTO convertToPictureDTO(ListingPicture listingPicture);

    @Named("extract-cover")
    default PictureDTO extractCover(Set<ListingPicture> pictures) {
        if (pictures == null || pictures.isEmpty()) {
            return null; // Return null or default if no pictures are found
        }

        return pictures.stream()
                .filter(ListingPicture::isCover)
                .findFirst()
                .map(this::convertToPictureDTO)
                .orElseThrow(() -> new RuntimeException("Cover picture not found"));
    }


}
