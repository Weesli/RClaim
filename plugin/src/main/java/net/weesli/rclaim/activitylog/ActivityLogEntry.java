package net.weesli.rclaim.activitylog;

import java.util.UUID;

public record ActivityLogEntry(UUID playerId, ActivityAction action, String detail, long timestamp) {
}
