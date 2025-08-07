package net.weesli.rclaim.config.lang;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.config.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@Setter
public class LangConfig extends OkaeriConfig {

        @CustomKey("IS_NOT_SUITABLE")
        private String isNotSuitable = "&cThis area already is a claim!";

        @CustomKey("SUCCESS_CLAIM_CREATED")
        private String successClaimCreated = "&aClaim created successfully!";

        @CustomKey("ENTER_A_PLAYER_NAME")
        private String enterAPlayerName = "&cPlease enter a player name!";

        @CustomKey("TARGET_NOT_FOUND")
        private String targetNotFound = "&cUser not found!";

        @CustomKey("TRUSTED_PLAYER")
        private String trustedPlayer = "&aPlayer added to your claims";

        @CustomKey("UNTRUSTED_PLAYER")
        private String untrustedPlayer = "&aPlayer removed from your claims";

        @CustomKey("NOT_TRUSTED_PLAYER")
        private String notTrustedPlayer = "&cThis player is already not trusted!";

        @CustomKey("ALREADY_TRUSTED_PLAYER")
        private String alreadyTrustedPlayer = "&cPlayer has already been trusted!";

        @CustomKey("PREVIEW_OPENED")
        private String previewOpened = "&aClaim is viewing preview if wish you buy this claim run /claim confirm";

        @CustomKey("HASNT_MONEY")
        private String hasntMoney = "&cHey! You haven't money for request!";

        @CustomKey("TIME_UPGRADE")
        private String timeUpgrade = "&aRequest deadline extended!";

        @CustomKey("HOME_SET")
        private String homeSet = "&aHome set successfully";

        @CustomKey("HASNT_HOME")
        private String hasntHome = "&cHey! You haven't a home!";

        @CustomKey("YOU_DONT_IN_CLAIM")
        private String youDontInClaim = "&cHey! You need a claim for that!";

        @CustomKey("CANNOT_CLAIM_MULTIPLE_CLAIMS")
        private String cannotClaimMultipleClaims = "&cYou can't claim more than one claim";

        @CustomKey("UNCLAIMED_CLAIM")
        private String unclaimedClaim = "&aClaim has been unclaimed";

        @CustomKey("NOT_YOUR_CLAIM")
        private String notYourClaim = "&cClaim needs to belong to you!";

        @CustomKey("CONFIRM_UNCLAIMED")
        private String confirmUnclaimed = "&cAre you sure you want to delete the claim /unclaim confirm";

        @CustomKey("YOU_CANT_PLACE_SPAWNER")
        private String youCantPlaceSpawner = "&cYou can only place spawners in areas you own.";

        @CustomKey("NOT_IN_CLAIMABLE_WORLD")
        private String notInClaimableWorld = "&cYou can't get a claim in this world!";

        @CustomKey("MAX_TRUSTED_PLAYERS")
        private String maxTrustedPlayers = "&cYou have reached the maximum member addition limit";

        @CustomKey("YOU_CANT_PLACE_MINION")
        private String youCantPlaceMinion = "&cYou need to be in your own territory to place minions!";

        @CustomKey("AREA_DISABLED")
        private String areaDisabled = "&cYou cannot buy a claim in this area!";

        @CustomKey("EFFECT_BOUGHT")
        private String effectBought = "&aThe Claim effect has been purchased!";

        @CustomKey("EFFECT_LEVEL_UP")
        private String effectLevelUp = "&aClaim effect increased to &b%level%&a!";

        @CustomKey("EFFECT_MAX_LEVEL")
        private String effectMaxLevel = "&cThis effect is currently max level!";

        @CustomKey("ALREADY_ADDED_USER")
        private String alreadyAddedUser = "&cThis player has already been added to the tag!";
        @CustomKey("ENABLE_CLAIM_BLOCK_EDIT_MODE")
        private String enableEditMode = "&aClaim block move mode enabled!";
        @CustomKey("DISABLE_CLAIM_BLOCK_EDIT_MODE")
        private String disableEditMode = "&aClaim block move mode disabled!";
        @CustomKey("CLAIM_LIMIT")
        private String claimLimit = "&cYou have reached the maximum limit of claims!";
        @CustomKey("HASNT_TP_PERMISSION")
        private String hasntTpPermission = "&cYou don't have permission to teleport!";
        @CustomKey("IS_NOT_ADJACENT")
        private String isNotAdjacent = "&cThis area is not adjacent to the claim!";
        @CustomKey("BLOCK_ENABLED")
        private String blockEnabled = "&aClaim block is now enabled!";
        @CustomKey("BLOCK_DISABLED")
        private String blockDisabled = "&aClaim block is now disabled!";
        // claim tags messages
        @CustomKey("ENTER_TAG_NAME")
        private String enterTagName = "&cEnter tag name!";
        @CustomKey("ENTER_A_TAG_NAME")
        private String enterATagName = "&cYou must enter a tag name!";
        @CustomKey("TAG_CREATED")
        private String tagCreated = "&aTag created successfully!";
        @CustomKey("ALREADY_CREATED_TAG")
        private String alreadyCreatedTag = "&cThis tag has already been created!";
        @CustomKey("ADDED_USER_TO_TAG")
        private String addedUserToTag = "&aPlayer added to tag successfully!";
        @CustomKey("VERY_CLOSE_TO_ANOTHER_CLAIM")
        private String veryCloseToAnotherClaim = "&cYou are very close to another claim!";
        @CustomKey("ALREADY_MAX_DAY")
        private String alreadyMaxDay = "&cYou have reached the maximum day limit!";
        @CustomKey("RENAME_SUCCESS")
        private String renameSuccess = "&aClaim renamed successfully!";
        @CustomKey("PVP_STATUS_NOT_CHANGEABLE")
        private String pvpStatusNotChangeable = "&cYou can't change pvp status in this area because a enemy is in your claim!";
        @CustomKey("HASN'T_PERMISSION_TO_CHANGE_CLAIM_BLOCK")
        private String hasntPermissionToChangeClaimBlock = "&cYou don't have permission for active this block!";
        // Hook messages

        @CustomKey("COMBAT_SYSTEM_MESSAGE")
        private String combatSystemMessage = "&cYou cannot enter a claim while in combat!";

        // Permission messages

        @CustomKey("PERMISSION_BLOCK_PLACE")
        private String permissionBlockPlace = "&cYou can't place a block this area!";

        @CustomKey("PERMISSION_BLOCK_BREAK")
        private String permissionBlockBreak = "&cYou can't break block from this area!";

        @CustomKey("PERMISSION_CONTAINER_OPEN")
        private String permissionContainerOpen = "&cYou can't access this container!";

        @CustomKey("PERMISSION_PICKUP_ITEM")
        private String permissionPickupItem = "&cYou can't pick up an item from this area!";

        @CustomKey("PERMISSION_DROP_ITEM")
        private String permissionDropItem = "&cYou can't drop an item to this area!";

        @CustomKey("PERMISSION_ATTACK_ANIMAL")
        private String permissionAttackAnimal = "&cYou can't attack an animal in this area";

        @CustomKey("PERMISSION_ATTACK_MONSTER")
        private String permissionAttackMonster = "&cYou can't attack an monster in this area";

        @CustomKey("PERMISSION_ENTITY_INTERACT")
        private String permissionEntityInteract = "&cYou can't interact any entity in this area";

        @CustomKey("PERMISSION_BREAK_CONTAINER")
        private String permissionBreakContainer = "&cYou can't break a container in this area";

        @CustomKey("PERMISSION_DOOR_OPEN")
        private String permissionDoorOpen = "&cYou can't interact with the door in this area!";

        @CustomKey("PERMISSION_ENTER_PORTAL")
        private String permissionEnterPortal = "&cYou can't enter a portal in this area";

        @CustomKey("PERMISSION_USE_POTION")
        private String permissionUsePotion = "&cYou can't use the potion in this area'";

        // Status messages

        @CustomKey("STATUS_PVP")
        private String statusPvp = "&cYou can't hit any player in this area!";

        // Admin messages

        @CustomKey("DELETED_CLAIMS")
        private String deletedClaims = "&cThe claims of %player% have been successfully deleted!";

        public static void sendMessageToPlayer(String path, Player player, TagResolver... tagResolvers) {
                String rawMessage =  ConfigLoader.getConfig().getPrefix() +  ConfigLoader.getLangConfig().get(path, String.class);
                MiniMessage mm = MiniMessage.miniMessage();
                Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage);
                String minimessage = mm.serialize(legacy).replace("\\", "");
                player.sendMessage(mm.deserialize(minimessage,tagResolvers));
        }

        public static void sendMessageToConsole(String path, TagResolver... tagResolvers) {
                String rawMessage =  ConfigLoader.getConfig().getPrefix() +  ConfigLoader.getLangConfig().get(path, String.class);
                MiniMessage mm = MiniMessage.miniMessage();
                Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage);
                String minimessage = mm.serialize(legacy).replace("\\", "");
                Bukkit.getConsoleSender().sendMessage(mm.deserialize(minimessage,tagResolvers));
        }


}
