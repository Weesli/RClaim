package net.weesli.rclaim.model;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.model.SubClaim;
import org.bukkit.Location;

@Getter@Setter
public class SubClaimImpl implements SubClaim {

    private String mainClaim;
    private int x, z;

    public SubClaimImpl() {
    }
    public SubClaimImpl(String mainClaim, int x, int z) {
        this.mainClaim = mainClaim;
        this.x = x;
        this.z = z;
    }

    public boolean contains(Location location){
        return location.getX() >= (x*16) && location.getX() < (x*16) + 16 && location.getZ() >= (z*16) && location.getZ() < (z*16) + 16;
    }

}
