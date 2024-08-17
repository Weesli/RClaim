package net.weesli.rClaim.StorageManager;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rozsLib.ConfigurationManager.YamlFileBuilder;

public class Loader {

    private RClaim plugin = RClaim.getInstance();

    private YamlFileBuilder claim_builder = RClaim.getClaim_builder();

    public Loader(){
        // Claim Loader
        StorageType type = RClaim.getInstance().getStorage().getStorageType();
        switch (type){
            case YAML:
                if (claim_builder.load().get("claims") != null){
                    for (String key : claim_builder.load().getConfigurationSection("claims").getKeys(false)){
                        Claim claim = plugin.getStorage().getClaim(key);
                        ClaimManager.addClaim(claim);
                        ClaimManager.getTasks().add(new ClaimTask(key, claim_builder.load().getInt("claims." + key + ".time")));
                    }
                }
                break;
            case MySQL:
                // MySQL Loader
                break;
            default:
                RClaim.getInstance().getLogger().severe("Unsupported storage type: " + type.name());
                return;
        }

    }

}
