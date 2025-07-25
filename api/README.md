# RClaim API

## Information

The new API was released with **RClaim V2.3.0**. The old API is now deprecated.
Please make sure to use the new API in your projects.
Below you will find information about the new API.

---

## Installation

### Maven

```xml
<dependency>
    <groupId>com.github.Weesli.RClaim</groupId>
    <artifactId>RClaim-api</artifactId>
    <version>{version}</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```groovy
compileOnly("com.github.Weesli.RClaim:RClaim-api:{version}")
```

---

## Usage

Make sure to add the following to your **plugin.yml** file:

```yaml
softdepend: [RClaim]
```

---

### Example Code

```java
import net.weesli.api.RClaimAPI;
import net.weesli.api.cache.CacheProvider;
import net.weesli.api.manager.ClaimManager;
import net.weesli.api.manager.TagManager;
import net.weesli.api.manager.UserManager;
import net.weesli.api.model.Claim;
import org.bukkit.Chunk;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    // Obtain API managers and cache provider
    private final CacheProvider cacheProvider = RClaimAPI.getCacheProvider();
    private final ClaimManager claimManager = RClaimAPI.getClaimManager();
    private final TagManager tagManager = RClaimAPI.getTagManager();
    private final UserManager userManager = RClaimAPI.getUserManager();

    @Override
    public void onEnable() {
        getLogger().info("ExamplePlugin enabled. RClaim API is ready to use.");
    }

    // Example method to get claim info for a chunk
    private Claim getClaim(Chunk chunk) {
        return claimManager.getClaim(chunk);
    }
}
```