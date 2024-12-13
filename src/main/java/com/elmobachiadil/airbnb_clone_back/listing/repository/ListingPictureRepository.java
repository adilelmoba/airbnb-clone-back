package com.elmobachiadil.airbnb_clone_back.listing.repository;

import com.elmobachiadil.airbnb_clone_back.listing.domain.ListingPicture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingPictureRepository extends JpaRepository<ListingPicture, Long> {
}
