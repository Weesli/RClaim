package net.weesli.rclaim.model;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.api.model.ClaimTag;

import java.util.List;
import java.util.UUID;

@Getter@Setter
public class ClaimTagImpl implements ClaimTag {

    private String claimId;

    private String id;
    private String displayName;

    private List<UUID> users;
    private List<ClaimPermission> permissions;

    public ClaimTagImpl() {
    }
    public ClaimTagImpl(String claimId, String id, String displayName, List<UUID> users, List<ClaimPermission> permissions) {
        this.claimId = claimId;
        this.id = id;
        this.displayName = displayName;
        this.users = users;
        this.permissions = permissions;
    }


    public boolean hasPermission(ClaimPermission permission) {
        return permissions.contains(permission);
    }

    public void addPermission(ClaimPermission permission) {
        permissions.add(permission);
    }

    public void removePermission(ClaimPermission permission) {
        permissions.remove(permission);
    }

    public boolean hasUser(UUID uuid) {
        return users.contains(uuid);
    }

    public void addUser(UUID uuid) {
        users.add(uuid);
    }

    public void removeUser(UUID uuid) {
        users.remove(uuid);
    }

}
