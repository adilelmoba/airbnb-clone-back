package com.elmobachiadil.airbnb_clone_back.listing.application;

import com.elmobachiadil.airbnb_clone_back.booking.application.BookingService;
import com.elmobachiadil.airbnb_clone_back.listing.application.dto.DisplayCardListingDTO;
import com.elmobachiadil.airbnb_clone_back.listing.application.dto.DisplayListingDTO;
import com.elmobachiadil.airbnb_clone_back.listing.application.dto.SearchDTO;
import com.elmobachiadil.airbnb_clone_back.listing.application.dto.sub.LandlordListingDTO;
import com.elmobachiadil.airbnb_clone_back.listing.domain.BookingCategory;
import com.elmobachiadil.airbnb_clone_back.listing.domain.Listing;
import com.elmobachiadil.airbnb_clone_back.listing.mapper.ListingMapper;
import com.elmobachiadil.airbnb_clone_back.listing.repository.ListingRepository;
import com.elmobachiadil.airbnb_clone_back.sharedkernel.service.State;
import com.elmobachiadil.airbnb_clone_back.user.application.UserService;
import com.elmobachiadil.airbnb_clone_back.user.application.dto.ReadUserDTO;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantService {

    private final ListingRepository listingRepository;

    private final ListingMapper listingMapper;
    private final BookingService bookingService;

    private final UserService userService;

    public TenantService(ListingRepository listingRepository, ListingMapper listingMapper, UserService userService, BookingService bookingService) {
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    public Page<DisplayCardListingDTO> getAllByCategory(Pageable pageable, BookingCategory category) {
        Page<Listing> allOrBookingCategory;
        if (category == BookingCategory.ALL) {
            System.out.println("Fetching all categories...");
            allOrBookingCategory = listingRepository.findAllWithCoverOnly(pageable);
        } else {
            System.out.println("Fetching specific category: " + category);
            allOrBookingCategory = listingRepository.findAllByBookingCategoryWithCoverOnly(pageable, category);
        }

        // Explicitly initialize pictures to avoid LazyInitializationException
        allOrBookingCategory.forEach(listing -> Hibernate.initialize(listing.getPictures()));

        return allOrBookingCategory.map(listingMapper::listingToDisplayCardListingDTO);
    }

    @Transactional(readOnly = true)
    public State<DisplayListingDTO, String> getOne(UUID publicId) {
        Optional<Listing> listingByPublicIdOpt = listingRepository.findByPublicId(publicId);

        if(listingByPublicIdOpt.isEmpty()) {
            return State.<DisplayListingDTO, String>builder()
                    .forError(String.format("Listing doesn't exist for publicId: %s", publicId));
        }

        DisplayListingDTO displayListingDTO = listingMapper.listingToDisplayListingDTO(listingByPublicIdOpt.get());

        ReadUserDTO readUserDTO = userService.getBYPublicId(listingByPublicIdOpt.get().getLandlordPublicId()).orElseThrow();
        LandlordListingDTO landlordListingDTO = new LandlordListingDTO(readUserDTO.firstName(), readUserDTO.imageUrl());
        displayListingDTO.setLandlord(landlordListingDTO);

        return State.<DisplayListingDTO, String>builder().forSuccess(displayListingDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<DisplayCardListingDTO> search(Pageable pageable, SearchDTO newSearch) {
        Page<Listing> allMatchedListings = listingRepository.findAllByLocationAndBathroomsAndBedroomsAndGuestsAndBeds(
                pageable,
                newSearch.location(),
                newSearch.infos().baths().value(),
                newSearch.infos().bedrooms().value(),
                newSearch.infos().guests().value(),
                newSearch.infos().beds().value()
        );
        List<UUID> listingUUIDs = allMatchedListings.stream().map(Listing::getPublicId).toList();

        List<UUID> bookingUUIDs = bookingService.getBookingMatchByListingIdsAndBookedDate(listingUUIDs, newSearch.dates());

        List<DisplayCardListingDTO> listingsNotBooked = allMatchedListings.stream().filter(listing -> !bookingUUIDs.contains(listing.getPublicId()))
                .map(listingMapper::listingToDisplayCardListingDTO)
                .toList();

        return new PageImpl<>(listingsNotBooked, pageable, listingsNotBooked.size());

    }
}
