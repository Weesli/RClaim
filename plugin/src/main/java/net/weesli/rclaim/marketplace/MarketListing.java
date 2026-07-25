package net.weesli.rclaim.marketplace;

import java.util.UUID;

public record MarketListing(String claimId, UUID sellerId, ListingType type, double price, int rentDurationSeconds) {
}
