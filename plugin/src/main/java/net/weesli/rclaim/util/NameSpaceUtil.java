package net.weesli.rclaim.util;

import lombok.Getter;
import net.weesli.rclaim.RClaim;
import org.bukkit.NamespacedKey;


/**
 * Util class for managing plugin namespace keys.
 */
public class NameSpaceUtil {

    private static final String KEY = "CLAIM_ID";

    @Getter
    private static final NamespacedKey key;

    static {
        key = new NamespacedKey(RClaim.getInstance(), KEY);
    }

}
