package net.weesli.rclaim.api.status;

import java.util.Map;

public interface ClaimStatusService {
    void registerStatus(ClaimStatusRegistry statusRegistry);

    Map<String, ClaimStatusRegistry> getRegistries();
}
