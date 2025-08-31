package net.weesli.rclaim.hook.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramShowEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.HologramModule;
import net.weesli.rclaim.api.hook.ClaimHologram;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.util.PlayerUtil;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

import static net.weesli.rclaim.util.ChatUtil.createTagResolver;

public class HFancyHologram implements ClaimHologram, Listener {



    public HFancyHologram() {
        RClaim.getInstance().getServer().getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void onLoadHologramForAPlayer(HologramShowEvent e){
        Hologram hologram = e.getHologram();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(hologram.getName());
        if (claim == null) return; // this is a not a claim hologram
        if (claim.isOwner(e.getPlayer().getUniqueId()))return; // ignore for owner
        e.setCancelled(true);
    }

    private static final HologramManager hologramManager = FancyHologramsPlugin.get().getHologramManager();

    @Override
    public void createHologram(String ID) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(ID);
        if (claim == null)return;
        Location hologramLocation = claim.getBlockLocation().clone().add(0.5f,
                ConfigLoader.getConfig().getHologram().getHologramSettings().getHologramHeight(), 0.5f);
        TextHologramData hologramData = new TextHologramData(ID, hologramLocation);
        hologramData.removeLine(0);
        hologramData.setBackground(Color.fromARGB(0,0,0,0));
        for (String line : ConfigLoader.getConfig().getHologram().getHologramSettings().getHologramLines()){
            if (line == null)continue;
            Component message = ColorBuilder.convertColors(line, createTagResolver("player", PlayerUtil.getPlayer(claim.getOwner()).getName()), createTagResolver("id", claim.getID()));
            hologramData.addLine(LegacyComponentSerializer.legacySection().serialize(message));
        }
        hologramData.setPersistent(false);
        Hologram hologram = hologramManager.create(hologramData);
        Bukkit.getOnlinePlayers().stream().filter(player -> !player.getUniqueId().equals(claim.getOwner())).forEach(
                hologram::forceHideHologram
        );
        hologramManager.addHologram(hologram);
    }

    @Override
    public void updateHologram(String ID) {
        Optional<Hologram> hologram = hologramManager.getHologram(ID);
        if (hologram.isEmpty())return;
        hologram.get().refreshForViewers();
    }

    @Override
    public void deleteHologram(String ID) {
        Optional<Hologram> hologram = hologramManager.getHologram(ID);
        if (hologram.isEmpty())return;
        hologram.get().deleteHologram();
    }

    @Override
    public boolean hasHologram(String ID) {
        Hologram hologram = hologramManager.getHologram(ID).orElse(null);
        return hologram != null;
    }

    @Override
    public HologramModule Type() {
        return HologramModule.FancyHolograms;
    }
}
