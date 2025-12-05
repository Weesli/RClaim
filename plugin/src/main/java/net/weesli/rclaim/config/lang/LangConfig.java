package net.weesli.rclaim.config.lang;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozsconfig.annotations.ConfigKey;
import net.weesli.rozsconfig.model.RozsConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@Setter
public class LangConfig extends RozsConfig {

        @ConfigKey("IS_NOT_SUITABLE")
        private String isNotSuitable;

        @ConfigKey("SUCCESS_CLAIM_CREATED")
        private String successClaimCreated;

        @ConfigKey("ENTER_A_PLAYER_NAME")
        private String enterAPlayerName;

        @ConfigKey("ENTER_A_CLAIM_NAME")
        private String enterAClaimName;

        @ConfigKey("TARGET_NOT_FOUND")
        private String targetNotFound;

        @ConfigKey("TRUSTED_PLAYER")
        private String trustedPlayer;

        @ConfigKey("UNTRUSTED_PLAYER")
        private String untrustedPlayer;

        @ConfigKey("NOT_TRUSTED_PLAYER")
        private String notTrustedPlayer;

        @ConfigKey("ALREADY_TRUSTED_PLAYER")
        private String alreadyTrustedPlayer;

        @ConfigKey("PREVIEW_OPENED")
        private String previewOpened;

        @ConfigKey("HASNT_MONEY")
        private String hasntMoney;

        @ConfigKey("TIME_UPGRADE")
        private String timeUpgrade;

        @ConfigKey("HOME_SET")
        private String homeSet;

        @ConfigKey("HASNT_HOME")
        private String hasntHome;

        @ConfigKey("YOU_DONT_IN_CLAIM")
        private String youDontInClaim;

        @ConfigKey("CANNOT_CLAIM_MULTIPLE_CLAIMS")
        private String cannotClaimMultipleClaims;

        @ConfigKey("UNCLAIMED_CLAIM")
        private String unclaimedClaim;

        @ConfigKey("NOT_YOUR_CLAIM")
        private String notYourClaim;

        @ConfigKey("CONFIRM_UNCLAIMED")
        private String confirmUnclaimed;

        @ConfigKey("YOU_CANT_STACK_BLOCKS")
        private String youCantStackBlocks;

        @ConfigKey("YOU_CANT_UNSTACK_BLOCKS")
        private String youCantUnstackBlocks;

        @ConfigKey("YOU_CANT_STACK_SPAWNERS")
        private String youCantStackSpawners;

        @ConfigKey("YOU_CANT_UNSTACK_SPAWNERS")
        private String youCantUnstackSpawners;

        @ConfigKey("YOU_CANT_PLACE_SPAWNER")
        private String youCantPlaceSpawner;

        @ConfigKey("YOU_CANT_BREAK_SPAWNER")
        private String youCantBreakSpawner;

        @ConfigKey("NOT_IN_CLAIMABLE_WORLD")
        private String notInClaimableWorld;

        @ConfigKey("MAX_TRUSTED_PLAYERS")
        private String maxTrustedPlayers;

        @ConfigKey("YOU_CANT_PLACE_MINION")
        private String youCantPlaceMinion;

        @ConfigKey("AREA_DISABLED")
        private String areaDisabled;

        @ConfigKey("EFFECT_BOUGHT")
        private String effectBought;

        @ConfigKey("EFFECT_LEVEL_UP")
        private String effectLevelUp;

        @ConfigKey("EFFECT_MAX_LEVEL")
        private String effectMaxLevel;

        @ConfigKey("ALREADY_ADDED_USER")
        private String alreadyAddedUser;
        @ConfigKey("ENABLE_CLAIM_BLOCK_EDIT_MODE")
        private String enableEditMode;
        @ConfigKey("DISABLE_CLAIM_BLOCK_EDIT_MODE")
        private String disableEditMode;
        @ConfigKey("CLAIM_LIMIT")
        private String claimLimit;
        @ConfigKey("HASNT_TP_PERMISSION")
        private String hasntTpPermission;
        @ConfigKey("IS_NOT_ADJACENT")
        private String isNotAdjacent;
        @ConfigKey("BLOCK_ENABLED")
        private String blockEnabled;
        @ConfigKey("BLOCK_DISABLED")
        private String blockDisabled;
        // claim tags messages
        @ConfigKey("ENTER_TAG_NAME")
        private String enterTagName;
        @ConfigKey("ENTER_A_TAG_NAME")
        private String enterATagName;
        @ConfigKey("TAG_CREATED")
        private String tagCreated;
        @ConfigKey("ALREADY_CREATED_TAG")
        private String alreadyCreatedTag;
        @ConfigKey("ADDED_USER_TO_TAG")
        private String addedUserToTag;
        @ConfigKey("VERY_CLOSE_TO_ANOTHER_CLAIM")
        private String veryCloseToAnotherClaim;
        @ConfigKey("ALREADY_MAX_DAY")
        private String alreadyMaxDay;
        @ConfigKey("RENAME_SUCCESS")
        private String renameSuccess;
        @ConfigKey("PVP_STATUS_NOT_CHANGEABLE")
        private String pvpStatusNotChangeable;
        @ConfigKey("HASN'T_PERMISSION_TO_CHANGE_CLAIM_BLOCK")
        private String hasntPermissionToChangeClaimBlock;
        // Hook messages

        @ConfigKey("COMBAT_SYSTEM_MESSAGE")
        private String combatSystemMessage;

        // Permission messages

        @ConfigKey("PERMISSION_BLOCK_PLACE")
        private String permissionBlockPlace;

        @ConfigKey("PERMISSION_BLOCK_BREAK")
        private String permissionBlockBreak;

        @ConfigKey("PERMISSION_CONTAINER_OPEN")
        private String permissionContainerOpen;

        @ConfigKey("PERMISSION_PICKUP_ITEM")
        private String permissionPickupItem;

        @ConfigKey("PERMISSION_DROP_ITEM")
        private String permissionDropItem;

        @ConfigKey("PERMISSION_ATTACK_ANIMAL")
        private String permissionAttackAnimal;

        @ConfigKey("PERMISSION_ATTACK_MONSTER")
        private String permissionAttackMonster;

        @ConfigKey("PERMISSION_ENTITY_INTERACT")
        private String permissionEntityInteract;

        @ConfigKey("PERMISSION_BREAK_CONTAINER")
        private String permissionBreakContainer;

        @ConfigKey("PERMISSION_DOOR_OPEN")
        private String permissionDoorOpen;

        @ConfigKey("PERMISSION_ENTER_PORTAL")
        private String permissionEnterPortal;

        @ConfigKey("PERMISSION_USE_POTION")
        private String permissionUsePotion;

        // Status messages

        @ConfigKey("STATUS_PVP")
        private String statusPvp;

        // Admin messages

        @ConfigKey("DELETED_CLAIMS")
        private String deletedClaims;

        public static void sendMessageToPlayer(String path, Player player, TagResolver... tagResolvers) {
                String rawMessage = ConfigLoader.getConfig().getPrefix()
                        + ConfigLoader.getLangConfig().get(path, String.class);

                MiniMessage mm = MiniMessage.miniMessage();

                Component legacy = LegacyComponentSerializer.builder()
                        .hexColors()
                        .character('&')
                        .build()
                        .deserialize(rawMessage);

                String minimessage = mm.serialize(legacy).replace("\\", "");

                player.sendMessage(
                        mm.deserialize(minimessage, tagResolvers)
                                .decoration(TextDecoration.ITALIC, false)
                );
        }


        public static void sendMessageToConsole(String path, TagResolver... tagResolvers) {
                String rawMessage = ConfigLoader.getConfig().getPrefix()
                        + ConfigLoader.getLangConfig().get(path, String.class);

                MiniMessage mm = MiniMessage.miniMessage();

                Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage);
                String minimessage = mm.serialize(legacy).replace("\\", "");

                Bukkit.getConsoleSender().sendMessage(
                        mm.deserialize(minimessage, tagResolvers)
                                .decoration(TextDecoration.ITALIC, false)
                );
        }



}
