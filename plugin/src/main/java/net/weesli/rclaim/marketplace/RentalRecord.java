package net.weesli.rclaim.marketplace;

import java.util.UUID;

public record RentalRecord(String claimId, UUID originalOwnerId, UUID renterId, long rentEndMillis) {
}
