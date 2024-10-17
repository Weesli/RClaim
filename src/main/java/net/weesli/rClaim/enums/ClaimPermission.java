package net.weesli.rClaim.enums;

import net.weesli.rClaim.RClaim;

public enum ClaimPermission {
    BLOCK_BREAK,
    BLOCK_PLACE,
    PICKUP_ITEM,
    DROP_ITEM,
    CONTAINER_OPEN,
    INTERACT_ENTITY,
    ATTACK_ANIMAL,
    ATTACK_MONSTER,
    BREAK_CONTAINER,
    USE_DOOR,
    USE_PORTAL,
    USE_POTION;

    public String getDisplayName() {
        return RClaim.getInstance().getUiManager().getConfig().getString("tag-permissions-menu.permissions-name." + name());
    }

}
