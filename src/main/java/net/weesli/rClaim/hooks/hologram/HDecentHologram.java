package net.weesli.rClaim.hooks.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.HologramModule;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class HDecentHologram implements ClaimHologram {

    @Override
    public void createHologram(String ID) {
        Optional<Claim> claim = ClaimManager.getClaim(ID);
        if (claim.isPresent()){
            Location spawn_location = new Location(claim.get().getCenter().getWorld(), claim.get().getCenter().getX() + 0.5f, claim.get().getCenter().getY() + 3.5f, claim.get().getCenter().getZ() + 0.5f);
            Hologram hologram = DHAPI.createHologram(ID, spawn_location);
            for (String line : RClaim.getInstance().getConfig().getStringList("options.hologram.hologram-settings.hologram-lines")){
                DHAPI.addHologramLine(hologram, ColorBuilder.convertColors(line.replaceAll("<id>", claim.get().getID()).replaceAll("%player%", Bukkit.getOfflinePlayer(claim.get().getOwner()).getName())));
            }
        }
    }

    @Override
    public void updateHologram(String ID) {
        Optional<Claim> claim = ClaimManager.getClaim(ID);
        if (claim.isPresent()){
            Hologram hologram = DHAPI.getHologram(ID);
            for (Player player : Bukkit.getOnlinePlayers()){
                if (!player.getUniqueId().equals(claim.get().getOwner())){
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
