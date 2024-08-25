package net.weesli.rClaim;

import net.weesli.rClaim.api.RClaimAPI;
import net.weesli.rClaim.api.events.ClaimDeleteEvent;
import net.weesli.rClaim.api.events.TrustedPlayerEvent;
import net.weesli.rClaim.api.events.UnTrustedPlayerEvent;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.management.ExplodeCause;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPlayer;
import net.weesli.rozsLib.ColorManager.ColorBuilder;
import net.weesli.rozsLib.CommandBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class Commands {

    private final RClaim plugin;

    public Commands(RClaim plugin){
        this.plugin = plugin;
        new ClaimCommand(plugin).setCommand("claim").build();
        new UnClaimCommand(plugin).setCommand("unclaim").build();
        new AdminClaimCommands(plugin).setCommand("adminclaim").build();
        new ClaimHomeCommands(plugin).setCommand("chome").build();
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
                    if(!ClaimManager.checkWorld(player.getWorld().getName())){
                        player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
                        return false;
                    }

                    if (ClaimManager.isSuitable(player.getChunk())){
                        player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
                        return false;
                    }
                    ClaimManager.viewClaimRadius(player,player.getChunk());
                    player.sendMessage(RClaim.getInstance().getMessage("PREVIEW_OPENED"));
                } else if (strings.length == 1 && strings[0].equals("confirm")) {
                    if(!ClaimManager.checkWorld(player.getWorld().getName())){
                        player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
                        return false;
                    }
                    if (!ClaimManager.getPlayerData(player.getUniqueId()).getClaims().isEmpty()){
                        player.sendMessage(RClaim.getInstance().getMessage("CANNOT_CLAIM_MULTIPLE_CLAIMS"));
                        return false;
                    }
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
                        if (player.getName().equalsIgnoreCase(strings[1])){
                            return false;
                        }
                        if(isCheckPlayer(strings[1])){
                            OfflinePlayer target = Bukkit.getOfflinePlayer(strings[1]);
                            TrustedPlayerEvent event = new TrustedPlayerEvent(player,target.getPlayer());
                            plugin.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
                                List<Claim> claims = player_data.getClaims();
                                if (claims.get(0).getMembers().size() >= RClaim.getInstance().getConfig().getInt("options.max-trusted-player")){
                                    player.sendMessage(RClaim.getInstance().getMessage("MAX_TRUSTED_PLAYERS"));
                                    return false;
                                }
                                if (claims.get(0).getMembers().contains(target.getUniqueId())){
                                    player.sendMessage(RClaim.getInstance().getMessage("ALREADY_TRUSTED_PLAYER"));
                                    return false;
                                }
                                for (Claim claim : claims){
                                    claim.addMember(target.getUniqueId());
                                    RClaim.getInstance().getStorage().updateClaim(claim);
                                }
                                ClaimManager.getPlayerData().put(player.getUniqueId(),player_data);
                                player.sendMessage(RClaim.getInstance().getMessage("TRUSTED_PLAYER"));
                            }
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
                        if (player.getName().equalsIgnoreCase(strings[1])){
                            return false;
                        }
                        if(isCheckPlayer(strings[1])){
                            OfflinePlayer target = plugin.getServer().getOfflinePlayer(strings[1]);
                            ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
                            List<Claim> claims = player_data.getClaims();
                            if (!claims.get(0).getMembers().contains(target.getUniqueId())){
                                player.sendMessage(RClaim.getInstance().getMessage("NOT_TRUSTED_PLAYER"));
                                return false;
                            }
                            UnTrustedPlayerEvent event = new UnTrustedPlayerEvent(player,target.getPlayer());
                            plugin.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                for (Claim claim : claims){
                                    claim.removeMember(target.getUniqueId());
                                    claim.getClaimPermissions().remove(target.getUniqueId());
                                    RClaim.getInstance().getStorage().updateClaim(claim);
                                }
                                ClaimManager.getPlayerData().put(player.getUniqueId(),player_data);
                                player.sendMessage(RClaim.getInstance().getMessage("UNTRUSTED_PLAYER"));
                            }
                        } else {
                            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("sethome")) {
                    Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(player.getLocation())).findFirst();
                    if (!claim.isPresent()){
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
            return Arrays.asList("confirm", "sethome", "trust", "untrust");
        }
    }

    class ClaimHomeCommands extends CommandBuilder{

        public ClaimHomeCommands(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected boolean Command(CommandSender commandSender, Command command, String s, String[] strings) {
            if (commandSender instanceof Player){
                Player player = (Player) commandSender;
                for (Claim claim : ClaimManager.getClaims()){
                    if (claim.isMember(player.getUniqueId()) || claim.isOwner(player.getUniqueId())){
                        if (claim.getHomeLocation() != null){
                            player.teleport(claim.getHomeLocation());
                            return true;
                        }
                    }
                }
                player.sendMessage(RClaim.getInstance().getMessage("NO_HOME_SET"));

            }
            return false;
        }

        @Override
        protected List<String> TabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            return Collections.singletonList("");
        }
    }

    class AdminClaimCommands extends CommandBuilder{

        public AdminClaimCommands(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected boolean Command(CommandSender commandSender, Command command, String s, String[] strings) {
            if (strings.length == 0){
                commandSender.sendMessage(ColorBuilder.convertColors("&bRunning RClaims by Weesli"));
            } else if (strings[0].equalsIgnoreCase("clearclaim")) {
                if (strings.length != 2){
                    commandSender.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
                    return false;
                }
                OfflinePlayer player = Bukkit.getOfflinePlayer(strings[1]);
                if (player == null){
                    commandSender.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                    return false;
                }
                List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
                claims.forEach(claim -> {
                    RClaim.getInstance().getStorage().deleteClaim(claim.getID());
                    ClaimManager.removeClaim(claim);
                });
                commandSender.sendMessage(RClaim.getInstance().getMessage("DELETED_CLAIMS").replaceAll("%player%", player.getName()));
            } else if (strings[0].equals("reload")) {
                if (!commandSender.isOp()){return false;}
                RClaim.getInstance().reloadConfig();
                RClaim.getInstance().getMenusFile().reload();
                RClaim.getInstance().getMessagesFile().reload();
                commandSender.sendMessage(ColorBuilder.convertColors("&aAll files reloaded!"));
            }
            return false;
        }

        @Override
        protected List<String> TabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
            if (commandSender.hasPermission("rclaim.admin.clearclaim") && commandSender.isOp()){
                return Arrays.asList("clearclaim", "reload");
            }
            if (strings.length == 1 && strings[0].equalsIgnoreCase("clearclaim")){
                return Arrays.stream(plugin.getServer().getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
            }
            return Collections.singletonList("reload");
        }
    }

    class UnClaimCommand extends CommandBuilder{
        public UnClaimCommand(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected boolean Command(CommandSender commandSender, Command command, String s, String[] args) {
            if (commandSender instanceof Player){
                Player player = (Player) commandSender;
                Claim claim = RClaimAPI.getInstance().getClaim(player.getChunk());
                if (claim == null){
                    player.sendMessage(RClaim.getInstance().getMessage("YOU_DONT_IN_CLAIM"));
                    return false;
                }
                if (!claim.isOwner(player.getUniqueId())){
                    player.sendMessage(RClaim.getInstance().getMessage("NOT_YOUR_CLAIM"));
                    return false;
                }
                if (args.length == 0){
                    player.sendMessage(RClaim.getInstance().getMessage("CONFIRM_UNCLAIMED"));
                }
                else if (args.length == 1 && args[0].equalsIgnoreCase("confirm")){
                    boolean isCenter = ClaimManager.getPlayerData(player.getUniqueId()).getClaims().get(0).getID().equals(claim.getID());
                    ClaimDeleteEvent event = new ClaimDeleteEvent(claim,ExplodeCause.UNCLAIM, isCenter);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()){
                        ClaimManager.removeClaim(claim);
                        RClaim.getInstance().getStorage().deleteClaim(claim.getID());
                    }
                    player.sendMessage(RClaim.getInstance().getMessage("UNCLAIMED_CLAIM"));
                    if (isCenter){
                        List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
                        for (Claim c : claims){
                            ClaimManager.removeClaim(c);
                            RClaim.getInstance().getStorage().deleteClaim(c.getID());
                        }
                    }
                }
            }
            return false;
        }

        @Override
        protected List<String> TabComplete(CommandSender commandSender, Command command, String s, String[] args) {
            return Collections.singletonList("confirm");
        }
    }


    private static boolean isCheckPlayer(String name){
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()){
            if (player.getName().equalsIgnoreCase(name)){
                return true;
            } else if (player.getUniqueId().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }
}

