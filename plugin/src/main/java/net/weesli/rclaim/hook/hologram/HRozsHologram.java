package net.weesli.rclaim.hook.hologram;

import net.kyori.adventure.text.Component;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.HologramModule;
import net.weesli.rclaim.api.hook.ClaimHologram;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import net.weesli.rozslib.format.FormatterUtil;
import net.weesli.rozslib.format.model.Placeholder;
import net.weesli.rozslib.hologram.Hologram;
import net.weesli.rozslib.hologram.RozsHologramService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HRozsHologram implements ClaimHologram {

    private final RozsHologramService service;

    public HRozsHologram(){
        service = new RozsHologramService(RClaim.getInstance());
    }

    @Override
    public void createHologram(String ID) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(ID);
        Location location = claim.getBlockLocation().clone().add(0.5f, ConfigLoader.getConfig().getHologram().getHologramSettings().getHologramHeight(),0.5f);
        Hologram hologram = service.createHologram(ID,location);
        for (String x : ConfigLoader.getConfig().getHologram().getHologramSettings().getHologramLines()){
            hologram.addLine(FormatterUtil.format(x, new Placeholder("player", Bukkit.getOfflinePlayer(claim.getOwner()).getName()), new Placeholder("id", claim.getID())));
            hologram.addLine(x.replaceAll("<player>",Bukkit.getOfflinePlayer(claim.getOwner()).getName()).replaceAll("<id>", claim.getID()));
        }
        viewForOwner(claim);
    }

    @Override
    public void updateHologram(String ID) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(ID);
        viewForOwner(claim);
    }

    @Override
    public void deleteHologram(String ID) {
        service.deleteHologram(ID);
    }

    @Override
    public boolean hasHologram(String ID) {
        return service.getHologram(ID) != null;
    }

    @Override
    public HologramModule Type() {
        return HologramModule.RozsHologram;
    }

    private void viewForOwner(Claim claim){
        UUID owner = claim.getOwner();
        Player player = Bukkit.getPlayer(owner);
        if (player != null){
            Hologram hologram = service.getHologram(claim.getID());
            if (hologram != null){
                hologram.addViewer(player);
            }
        }
    }
}
