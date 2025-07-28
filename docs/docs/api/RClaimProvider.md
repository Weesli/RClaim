## Provider Class

```java
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
```