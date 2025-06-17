package net.weesli.rclaim.manager;

import lombok.Getter;
import net.weesli.rclaim.api.manager.CacheManager;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.cache.CacheImpl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class CacheManagerImpl implements CacheManager {

    private final ClaimCache claims;

    public CacheManagerImpl(){
        this.claims = new ClaimCache();
    }

    public static class ClaimCache extends CacheImpl<String, Claim> {

        public ClaimCache() {
            super(new HashMap<>());
        }

        public void addClaim(Claim claim){
            getCache().put(claim.getID(), claim);
        }

        public List<Claim> getAllClaims(UUID owner){
            return getCache().values().stream().filter(c -> c.isOwner(owner)).collect(Collectors.toList());
        }
    }
}
