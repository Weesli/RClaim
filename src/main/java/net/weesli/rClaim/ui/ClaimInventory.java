package net.weesli.rClaim.ui;

import net.weesli.rClaim.modal.Claim;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public interface ClaimInventory {

    void openInventory(Player player, Claim claim, FileConfiguration config);

}
