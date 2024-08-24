package net.weesli.rClaim.management;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.StorageManager.StorageType;
import net.weesli.rClaim.api.events.ClaimCreateEvent;
import net.weesli.rClaim.api.events.ClaimDeleteEvent;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPermission;
import net.weesli.rClaim.utils.ClaimPlayer;
import net.weesli.rClaim.utils.ClaimStatus;
import net.weesli.rozsLib.ColorManager.ColorBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClaimManager {

    private static List<Claim> claims = new ArrayList<>();
    private static Map<UUID, ClaimPlayer> playerData = new HashMap<>();
    private static List<ClaimTask> tasks = new ArrayList<>();

    public static void addClaim(Claim claim) {
        claims.add(claim);
    }

    public static void removeClaim(Claim claim) {
        claims.remove(claim);
    }

    public static Optional<Claim> getClaim(String ID) {
        if (RClaim.getInstance().getStorage().getStorageType().equals(StorageType.MySQL)){
            return Optional.ofNullable(RClaim.getInstance().getStorage().getClaim(ID));
        }
        return claims.stream().filter(claim -> claim.getID().equals(ID)).findFirst();
    }

    public static void TransferClaim(Claim claim, UUID new_owner){
        claim.setOwner(new_owner);
    }

    public static List<Claim> getClaims() {
        if (RClaim.getInstance().getStorage().getStorageType().equals(StorageType.MySQL)){
            return RClaim.getInstance().getStorage().getClaims();
        }else {
            return claims;
        }
    }

    public static void ExplodeClaim(String ID, ExplodeCause cause, boolean isCenter){
        Optional<Claim> claim = getClaim(ID);
        if (!claim.isPresent()){return;}
        ClaimDeleteEvent event = new ClaimDeleteEvent(claim.get(),cause, isCenter);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            removeClaim(claim.get());
            RClaim.getInstance().getStorage().deleteClaim(ID);
            if (RClaim.getInstance().getConfig().getBoolean("options.claim-timeout-message.enabled")){
                RClaim.getInstance().getConfig().getStringList("options.claim-timeout-message.text").stream().map(line-> ColorBuilder.convertColors(line).replaceAll("%player%", Bukkit.getPlayer(claim.get().getOwner()).getName()).replaceAll("%x%", String.valueOf(claim.get().getX())).replaceAll("%z%", String.valueOf(claim.get().getZ()))).forEach(Bukkit::broadcastMessage);
            }
        }
        if (isCenter){
            getPlayerData(claim.get().getOwner()).getClaims().forEach(claim1 -> {
                ExplodeClaim(claim1.getID(),ExplodeCause.UNCLAIM, false);
            });
        }
    }

    public static void createClaim(Chunk chunk, Player owner, boolean isCenter){
        String id = IDCreator();
        List<UUID> members= new ArrayList<>();
        List<ClaimStatus> claimStatuses = new ArrayList<>();
        Map<UUID, List<ClaimPermission>> permissions = new HashMap<>();
        Optional<Claim> center = getPlayerData(owner.getUniqueId()).getClaims().stream().filter(Claim::isCenter).findFirst();
        if (center.isPresent()){
            members.addAll(center.get().getMembers());
            claimStatuses.addAll(center.get().getClaimStatuses());
            permissions.putAll(center.get().getClaimPermissions());
        }
        Claim claim = new Claim(id, owner.getUniqueId(), members, claimStatuses, chunk, isCenter);
        getTasks().add(new ClaimTask(id, getSec(RClaim.getInstance().getConfig().getInt("claim-settings.claim-duration")), isCenter));
        if (isCenter){
            setupBlock(claim);
            for (String default_permission_value : RClaim.getInstance().getConfig().getConfigurationSection("claim-settings.default-claim-status").getKeys(false)){
                if (RClaim.getInstance().getConfig().getBoolean("claim-settings.default-claim-status." + default_permission_value)){
                    ClaimStatus status = ClaimStatus.valueOf(default_permission_value);
                    claim.addClaimStatus(status);
                }
            }
        }
        claim.setClaimPermissions(permissions);
        ClaimCreateEvent event = new ClaimCreateEvent(owner, claim);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            addClaim(claim);
            RClaim.getInstance().getStorage().insertClaim(claim);
        }
    }

    public static void viewClaimRadius(Player player, Chunk chunk) {
        int x = chunk.getX() * 16;
        int z = chunk.getZ() * 16;
        int size = 16;
        World world = player.getWorld();

        new BukkitRunnable() {
            int time = 20;

            @Override
            public void run() {
                if (time == 0) {
                    this.cancel();
                    return;
                }

                for (int i = x; i < x + size; i++) {
                    for (int j = z; j < z + size; j++) {
                        if (i == x || i == x + size - 1 || j == z || j == z + size - 1) {
                            Block highestBlock = world.getHighestBlockAt(i, j);
                            Location particleLocation = highestBlock.getLocation().add(0.5, 1, 0.5);
                            world.spawnParticle(Particle.REDSTONE, particleLocation, 10, new Particle.DustOptions(Color.RED, 1));
                        }
                    }
                }

                time--;
            }
        }.runTaskTimerAsynchronously(RClaim.getInstance(), 0, 5);
    }


    public static boolean isSuitable(Chunk chunk){
        Optional<Claim> claim = getClaims().stream().filter(c -> c.getChunk().getX() == chunk.getX() && c.getChunk().getZ() == chunk.getZ()).findFirst();
        return claim.isPresent();
    }

    private static void setupBlock(Claim claim){
        Block block = claim.getChunk().getWorld().getBlockAt(new Location(claim.getCenter().getWorld(), claim.getCenter().getX(), claim.getCenter().getY() + 1, claim.getCenter().getZ()));
        block.setType(Material.BEDROCK);
    }

    public static ClaimPlayer getPlayerData(UUID uuid) {
        return new ClaimPlayer(uuid,Bukkit.getOfflinePlayer(uuid).getName());
    }

    public static  Map<UUID, ClaimPlayer> getPlayerData() {
        return playerData;
    }

    public static String IDCreator(){
        String alphabet = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    public static int getSec(int day){
        return day * 24 * 60 * 60;
    }

    public static String getTimeFormat(String claimId){
        Optional<ClaimTask> task = getTasks().stream().filter(task1 -> task1.getClaimId().equals(claimId)).findFirst();
        if (task.isPresent()){
            int totalSeconds = task.get().getTime();
            long secondsInWeek = 7 * 24 * 60 * 60;
            long secondsInDay = 24 * 60 * 60;
            long secondsInHour = 60 * 60;
            long secondsInMinute = 60;

            long weeks = totalSeconds / secondsInWeek;
            totalSeconds %= secondsInWeek;

            long days = totalSeconds / secondsInDay;
            totalSeconds %= secondsInDay;

            long hours = totalSeconds / secondsInHour;
            totalSeconds %= secondsInHour;

            long minutes = totalSeconds / secondsInMinute;
            long seconds = totalSeconds % secondsInMinute;
            return RClaim.getInstance().getConfig().getString("options.time-format").replaceAll("%week%", String.valueOf(weeks)).replaceAll("%day%", String.valueOf(days)).replaceAll("%hour%", String.valueOf(hours)).replaceAll("%minute%", String.valueOf(minutes)).replaceAll("%second%", String.valueOf(seconds));
        }
        return "";
    }

    public static List<ClaimTask> getTasks() {
        return tasks;
    }
}
