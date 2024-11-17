package net.weesli.rClaim.utils;

import lombok.Getter;
import net.weesli.rClaim.RClaim;
import org.bukkit.NamespacedKey;

public class RClaimNameSpaceKey {

    private static final String KEY = "CLAIM_ID";

    @Getter
    private static final NamespacedKey key;

    static {
        key = new NamespacedKey(RClaim.getInstance(), KEY);
    }

}
