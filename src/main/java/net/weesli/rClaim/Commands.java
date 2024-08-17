package net.weesli.rClaim;

import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPlayer;
import net.weesli.rozsLib.CommandBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Commands {

    private final RClaim plugin;

    public Commands(RClaim plugin){
        this.plugin = plugin;
        new ClaimCommand(plugin).setCommand("claim").build();
    }

    class ClaimCommand extends CommandBuilder{

        public ClaimCommand(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected boolean Command(CommandSender commandSender, Command command, String s, String[] strings) {
            if (commandSender instanceof Player){
                Player player = (Player) commandSender;

                if(strings.length == 0){
                    if (ClaimManager.isSuitable(player.getChunk())){
                        player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
                        return false;
                    }
                    ClaimManager.viewClaimRadius(player,player.getChunk());
                    player.sendMessage(RClaim.getInstance().getMessage("PREVIEW_OPENED"));
                } else if (strings.length == 1 && strings[0].equals("confirm")) {
                    if (RClaim.getInstance().getEconomy().isActive()){
                        if (!RClaim.getInstance().getEconomy().hasEnough(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"))){
                            player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                            return false;
                        }
                        RClaim.getInstance().getEconomy().withdraw(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"));
                    }
                    if (ClaimManager.isSuitable(player.getChunk())){
                        player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
                        return false;
                    }
                    ClaimManager.createClaim(player.getChunk(), player, true);
                    player.sendMessage(RClaim.getInstance().getMessage("SUCCESS_CLAIM_CREATED"));
                } else if (strings[0].equals("trust")) {
                    if (strings.length == 1){
                        player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
                        return false;
                    }
                    if (strings.length == 2){
                        Player target = plugin.getServer().getOfflinePlayer(strings[1]).getPlayer();
                        if(target != null){
                            ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
                            if (player_data.getClaims().get(0).getMembers().contains(target.getUniqueId())){
                                player.sendMessage(RClaim.getInstance().getMessage("ALREADY_TRUSTED_PLAYER"));
                                return false;
                            }
                            player_data.getClaims().forEach(claim -> {
                                claim.addMember(target.getUniqueId());
                                RClaim.getInstance().getStorage().updateClaim(claim);
                            });
                            ClaimManager.getPlayerData().put(player.getUniqueId(),player_data);
                            player.sendMessage(RClaim.getInstance().getMessage("TRUSTED_PLAYER"));
                        } else {
                            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                        }
                    }
                } else if (strings[0].equals("untrust")) {
                    if (strings.length == 1){
                        player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
                        return false;
                    }
                    if (strings.length == 2){
                        Player target = plugin.getServer().getOfflinePlayer(strings[1]).getPlayer();
                        if(target != null){
                            ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
                            if (!player_data.getClaims().get(0).getMembers().contains(target.getUniqueId())){
                                player.sendMessage(RClaim.getInstance().getMessage("NOT_TRUSTED_PLAYER"));
                                return false;
                            }
                            player_data.getClaims().forEach(claim -> {
                                claim.removeMember(target.getUniqueId());
                                RClaim.getInstance().getStorage().updateClaim(claim);
                            });
                            ClaimManager.getPlayerData().put(player.getUniqueId(),player_data);
                            player.sendMessage(RClaim.getInstance().getMessage("UNTRUSTED_PLAYER"));
                        } else {
                            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("sethome")) {
                    Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(player.getLocation())).findFirst();
                    if (claim.isEmpty()){
                        player.sendMessage(RClaim.getInstance().getMessage("YOU_DONT_IN_CLAIM"));
                    }
                    claim.ifPresent(c -> {
                        if (c.isOwner(player.getUniqueId())){
                            List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
                            claims.stream().forEach(c1 -> {
                                c1.setHomeLocation(null);
                            });
                            c.setHomeLocation(player.getLocation());
                            RClaim.getInstance().getStorage().updateClaim(c);
                            player.sendMessage(RClaim.getInstance().getMessage("HOME_SET"));
                        }
                    });
                } else if (strings[0].equalsIgnoreCase("home")) {
                    ClaimManager.getClaims().forEach(claim -> {
                        if (claim.isMember(player.getUniqueId()) || claim.isOwner(player.getUniqueId())){
                            if (claim.getHomeLocation()!= null){
                                player.teleport(claim.getHomeLocation());
                                return;
                            }
                        }
                    });
                    player.sendMessage(RClaim.getInstance().getMessage("NO_HOME_SET"));
                }else if(strings[0].equalsIgnoreCase("list")){
                    player.sendMessage(RClaim.getInstance().getMessage("CLAIM_LIST"));
                    ClaimManager.getPlayerData(player.getUniqueId()).getClaims().forEach(claim -> {
                        player.sendMessage(RClaim.getInstance().getMessage("CLAIM_LIST_ENTRY").replace("%claim%", claim.getChunk().getWorld().getName() +  ": x : " + claim.getX() + " : z : " + claim.getZ()));
                    });
                }
            }
            return false;
        }

        @Override
        protected List<String> TabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            if (strings.length == 2 && strings[0].equalsIgnoreCase("trust")){
                return plugin.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            if (strings.length == 2 && strings[0].equalsIgnoreCase("untrust")){
                return plugin.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return List.of("confirm", "sethome", "home", "trust", "untrust", "list");
        }
    }

    class AdminClaimCommands extends CommandBuilder{

        public AdminClaimCommands(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected boolean Command(CommandSender commandSender, Command command, String s, String[] strings) {
            return false;
        }

        @Override
        protected List<String> TabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return List.of();
        }
    }
}

