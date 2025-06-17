package net.weesli.rclaim.hook.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.HologramModule;
import net.weesli.rclaim.util.PlayerUtil;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HDecentHologram implements IClaimHologram {

    @Override
    public void createHologram(String ID) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(ID);
        Location hologramLocation = claim.getBlockLocation().clone().add(0.5,3.5,0.5);
        Hologram hologram = DHAPI.createHologram(ID, hologramLocation);
        for (String line : ConfigLoader.getConfig().getHologram().getHologramSettings().getHologramLines()){
            DHAPI.addHologramLine(hologram, ColorBuilder.convertColors(line
                    .replaceAll("<id>", claim.getID())
                    .replaceAll("%player%", PlayerUtil.getPlayer(claim.getOwner()).getName())));
        }
    }

    @Override
    public void updateHologram(String ID) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(ID);
        if (claim != null){
            Hologram hologram = DHAPI.getHologram(ID);
            for (Player player : Bukkit.getOnlinePlayers()){
                if (!player.getUniqueId().equals(claim.getOwner())){
                    if (!hologram.isHideState(player)){
                        hologram.setHidePlayer(player);
                    }
                }
            }
        }
    }

    @Override
    public void deleteHologram(String ID) {
        DHAPI.removeHologram(ID);
    }

    @Override
    public boolean hasHologram(String ID) {
        return DHAPI.getHologram(ID) != null;
    }

    @Override
    public HologramModule Type() {
        return HologramModule.DecentHologram;
    }
}
