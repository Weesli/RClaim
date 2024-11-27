package net.weesli.rClaim.utils;

import lombok.Getter;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.Effect;
import net.weesli.rClaim.enums.ExplodeCause;
import net.weesli.rClaim.api.events.ClaimCreateEvent;
import net.weesli.rClaim.api.events.ClaimDeleteEvent;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.modal.ClaimPlayer;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClaimManager {

    @Getter private static List<Claim> claims = new ArrayList<>();
    @Getter private static Map<UUID, ClaimPlayer> playerData = new HashMap<>();
    @Getter private static List<ClaimTask> tasks = new ArrayList<>();

    public static void addClaim(Claim claim) {
        claims.add(claim);
    }

    public static void removeClaim(Claim claim) {
        claims.remove(claim);
    }

    public static Optional<Claim> getClaim(String ID) {
        return claims.stream().filter(claim -> claim.getID().equals(ID)).findFirst();
    }

    public static void TransferClaim(Claim claim, UUID new_owner){
        claim.setOwner(new_owner);
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

    public static void createClaim(Chunk chunk, Player owner, boolean isCenter, String centerId) {
        String id = IDCreator();
        List<UUID> members = new ArrayList<>();
        List<ClaimStatus> claimStatuses = new ArrayList<>();
        Map<UUID, List<ClaimPermission>> permissions = new HashMap<>();

        Optional<Claim> center = getPlayerData(owner.getUniqueId())
                .getClaims()
                .stream()
                .filter(Claim::isCenter)
                .findFirst();

        if (center.isPresent()) {
            Claim centerClaim = center.get();
            members.addAll(centerClaim.getMembers());
            claimStatuses.addAll(centerClaim.getClaimStatuses());
            permissions.putAll(centerClaim.getClaimPermissions());
        }

        Claim claim = new Claim(id, owner.getUniqueId(), members, claimStatuses, chunk, isCenter);

        getTasks().add(new ClaimTask(
                id,
                getSec(RClaim.getInstance().getConfig().getInt("claim-settings.claim-duration")),
                isCenter
        ));

        if (isCenter) {
            Block block = claim.getChunk().getWorld().getBlockAt(new Location(
                    claim.getCenter().getWorld(),
                    claim.getCenter().getX(),
                    claim.getCenter().getY() + 1,
                    claim.getCenter().getZ()
            ));
            block.setType(Material.BEDROCK);
            RClaim.getInstance()
                    .getConfig()
                    .getConfigurationSection("claim-settings.default-claim-status")
                    .getKeys(false)
                    .forEach(defaultPermissionValue -> {
                        if (RClaim.getInstance()
                                .getConfig()
                                .getBoolean("claim-settings.default-claim-status." + defaultPermissionValue)) {
                            ClaimStatus status = ClaimStatus.valueOf(defaultPermissionValue);
                            claim.addClaimStatus(status);
                        }
                    });
        }

        if (!centerId.isEmpty()) {
            claim.setCenterId(centerId);
        }

        claim.setClaimPermissions(permissions);

        ClaimCreateEvent event = new ClaimCreateEvent(owner, claim);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            addClaim(claim);
            RClaim.getInstance().getStorage().insertClaim(claim);
        }

        chunk.getPersistentDataContainer().set(
                RClaimNameSpaceKey.getKey(),
                PersistentDataType.STRING,
                claim.getID()
        );
    }


    public static void viewClaimRadius(Player player, Chunk chunk) {
        PreviewViewer(player,chunk);
    }
    
    
    private static void PreviewViewer(Player player, Chunk chunk){
        String viewerMode = RClaim.getInstance().getConfig().getString("options.viewer-mode");
        int x = chunk.getX() * 16;
        int z = chunk.getZ() * 16;
        int size = 16;
        World world = player.getWorld();
        if (viewerMode.equalsIgnoreCase("particle")){
            new BukkitRunnable() {
                int time = 20;

                @Override
                public void run() {
                    if (time == 0) {
                        this.cancel();
                        return;
                    }

                    for (int i = 0; i < size * 4 - 4; i++) {
                        int dx = i < size ? i : i < size * 2 - 1 ? size - 1 : i < size * 3 - 2 ? size * 3 - 3 - i : 0;
                        int dz = i < size ? 0 : i < size * 2 - 1 ? i - size + 1 : i < size * 3 - 2 ? size - 1 : size * 4 - 4 - i;

                        Block highestBlock = world.getHighestBlockAt(x + dx, z + dz);
                        Location particleLocation = highestBlock.getLocation().add(0.5, 1, 0.5);
                        world.spawnParticle(getParticle(), particleLocation, 10, new Particle.DustOptions(Color.RED, 1));
                    }

                    time--;
                }
            }.runTaskTimerAsynchronously(RClaim.getInstance(), 0, 5);
        } else if (viewerMode.equalsIgnoreCase("border")) {
            WorldBorder border = Bukkit.createWorldBorder();
            border.setCenter(x+8,z+8);
            border.setSize(16);
            player.setWorldBorder(border);
            border.setSize(16,5);
            new BukkitRunnable() {
                @Override
                public void run() {
                    border.reset();
                    this.cancel();
                }
            }.runTaskLater(RClaim.getInstance(),60);
        }
    }


    public static boolean isSuitable(Chunk chunk){
        Claim claim = ClaimUtils.getClaim(chunk);
        return claim != null;
    }

    public static ClaimPlayer getPlayerData(UUID uuid) {
        return new ClaimPlayer(uuid,Bukkit.getOfflinePlayer(uuid).getName());
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

    public static boolean checkWorld(String name){
        return RClaim.getInstance().getConfig().getStringList("options.active-worlds").stream().anyMatch(s -> s.equals(name));
    }

    private static Particle getParticle(){
        String currentVersion = Bukkit.getVersion();
        if (currentVersion.equals("1.21") || currentVersion.equals("1.21.1")){
            return Particle.valueOf("DUST");
        } else {
            return Particle.valueOf("REDSTONE");
        }
    }

    public static PotionEffectType getEffectType(String type){
        String currentVersion = Bukkit.getVersion();
        if (currentVersion.equals("1.21") || currentVersion.equals("1.21.1")){
            switch (type){
                case "jump" -> {
                    return PotionEffectType.JUMP_BOOST;
                }
                case "haste" -> {
                    return PotionEffectType.HASTE;
                }
                case "speed" -> {
                    return PotionEffectType.SPEED;
                }
            }
        } else {
            switch (type){
                case "jump" -> {
                    return PotionEffectType.getByName("JUMP");
                }
                case "haste" -> {
                    return PotionEffectType.getByName("FAST_DIGGING");
                }
                case "speed" -> {
                    return PotionEffectType.SPEED;
                }
            }
        }
        return null;
    }

    public static String getStatus(boolean status){
        return status? RClaim.getInstance().getConfig().getString("options.status.active") : RClaim.getInstance().getConfig().getString("options.status.non-active");
    }

    public static int getCost(Effect effect, Claim claim){
        boolean isActive = claim.hasEffect(effect);
        int cost;
        if (isActive){
            cost = RClaim.getInstance().getConfig().getInt("options.effects." + effect.name().toLowerCase() + ".upgrade-cost");
        } else {
            cost = RClaim.getInstance().getConfig().getInt("options.effects." + effect.name().toLowerCase() + ".buy-cost");
        }
        return cost;
    }

    public static void changeBlockMaterial(Player player, Claim claim, Material material){
        claim.setBlock(material);
        player.playEffect(claim.getCenter(), org.bukkit.Effect.SMOKE, 25);
    }
}
