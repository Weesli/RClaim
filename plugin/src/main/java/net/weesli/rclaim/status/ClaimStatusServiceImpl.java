package net.weesli.rclaim.status;

import lombok.Getter;
import net.weesli.rclaim.api.status.ClaimStatusRegistry;
import net.weesli.rclaim.api.status.ClaimStatusService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
@Getter
public class ClaimStatusServiceImpl implements ClaimStatusService {

    private final Plugin plugin;
    @Getter private final Map<String, ClaimStatusRegistry> statuses = new HashMap<>();
    public ClaimStatusServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerStatus(ClaimStatusRegistry statusRegistry) {
        statuses.put(statusRegistry.key(), statusRegistry);
        plugin.getServer().getPluginManager().registerEvents(statusRegistry, plugin);
    }

    @Override
    public Map<String, ClaimStatusRegistry> getRegistries() {
        return statuses;
    }
}
