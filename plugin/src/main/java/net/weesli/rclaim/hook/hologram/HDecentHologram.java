package net.weesli.rclaim.hook.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimHologram;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.HologramModule;
import net.weesli.rclaim.util.PlayerUtil;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HDecentHologram implements ClaimHologram {



    @Override
    public void createHologram(String ID) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(ID);
        if (claim == null)return;
        Location hologramLocation = claim.getBlockLocation().clone().add(0.5f,
                ConfigLoader.getConfig().getHologram().getHologramSettings().getHologramHeight(), 0.5f);
        Hologram hologram;
        try {
            hologram = DHAPI.createHologram(ID, hologramLocation);
        }catch (Exception ex){
            hologram = DHAPI.getHologram(ID);
        }
        if (hologram == null)return;
        for (String line : ConfigLoader.getConfig().getHologram().getHologramSettings().getHologramLines()){
            if (line == null)continue;
            Component message = ColorBuilder.convertColors(line, Placeholder.parsed("player", Bukkit.getOfflinePlayer(claim.getOwner()).getName()), Placeholder.parsed("id", claim.getID()));
            DHAPI.addHologramLine(hologram, LegacyComponentSerializer.legacySection().serialize(message));
        }
    }

    @Override
    public void updateHologram(String ID) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(ID);
        if (claim == null)return;
        Hologram hologram = DHAPI.getHologram(ID);
        if (hologram == null){
            createHologram(ID);
        }
        if (hologram == null)return;
        for (Player player : Bukkit.getOnlinePlayers()){
            if (!player.getUniqueId().equals(claim.getOwner())){
                if (!hologram.isHideState(player)){
                    hologram.setHidePlayer(player);
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
        return HologramModule.DecentHolograms;
    }
}
