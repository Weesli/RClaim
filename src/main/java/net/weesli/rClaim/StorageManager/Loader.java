package net.weesli.rClaim.StorageManager;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rozsLib.ConfigurationManager.YamlFileBuilder;

public class Loader {

    private RClaim plugin = RClaim.getInstance();

    private YamlFileBuilder claim_builder = RClaim.getInstance().getClaim_builder();

    public Loader(){
        // Claim Loader
        StorageType type = RClaim.getInstance().getStorage().getStorageType();
        switch (type){
            case YAML:
                if (claim_builder.load().get("claims") == null || claim_builder.load().getString("claims").equals("{}")){
                    return;
                }
                for (String key : claim_builder.load().getConfigurationSection("claims").getKeys(false)){
                    if (claim_builder.load().get("claims." + key) != null){
                        Claim claim = plugin.getStorage().getClaim(key);
                        ClaimManager.addClaim(claim);
                        ClaimManager.getTasks().add(new ClaimTask(key, claim_builder.load().getInt("claims." + key + ".time"), claim.isCenter()));
                    }
                }
                break;
            case MySQL:
                break;
            default:
                RClaim.getInstance().getLogger().severe("Unsupported storage type: " + type.name());
                return;
        }

    }

}
