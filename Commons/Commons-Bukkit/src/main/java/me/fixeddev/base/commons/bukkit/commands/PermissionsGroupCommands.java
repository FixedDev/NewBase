package me.fixeddev.base.commons.bukkit.commands;

import com.google.inject.Inject;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.permissions.Permission;
import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.commons.translations.TranslationManager;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class PermissionsGroupCommands implements CommandClass {

    @Inject
    private GroupManager groupManager;
    @Inject
    private TranslationManager translationManager;

    @Command(names = "listgroups", min = 0, max = 0)
    public boolean listGroups(CommandSender sender) {
        FutureUtils.addCallback(groupManager.getAllGroups(), groups -> {
            if (groups.isEmpty()) {
                translationManager.getMessage("commons.permissions.groups.none-available").ifPresent(message ->
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")))
                );

                return;
            }

            translationManager.getMessage("commons.permissions.groups.available").ifPresent(message -> {
                message.setVariableValue("groups", groups.stream().map(Group::getName).collect(Collectors.joining(", ")));

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
            });
        });

        return true;
    }

    @Command(names = "createGroup", min = 1, max = 2, usage = "/<command> <group> [weight]")
    public boolean createGroup(CommandSender sender, String groupName, @Optional("1") int weight) {
        FutureUtils.addCallback(groupManager.createGroup(groupName, weight), group -> {
            if (group == null) {
                translationManager.getMessage("commons.permissions.groups.already-exists").ifPresent(message -> {
                    message.setVariableValue("group", groupName);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });
                return;
            }

            translationManager.getMessage("commons.permissions.groups.created").ifPresent(message -> {
                message.setVariableValue("group", groupName)
                        .setVariableValue("weight", weight + "");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
            });
        });

        return true;
    }

    @Command(names = "permission add", min = 2, max = 4, usage = "/<command> <group> <permission> [denied] [weight]")
    public boolean permissionAdd(CommandSender sender, String groupName, String permission, @Optional("false") boolean denied, @Optional("1") int weight) {
        FutureUtils.addCallback(groupManager.getGroupByName(groupName), group -> {
            if (group == null) {
                translationManager.getMessage("commons.permissions.groups.not-exists").ifPresent(message -> {
                    message.setVariableValue("group", groupName);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });
                return;
            }

            Permission permissionObject = Permission.of(permission, denied, weight);

            group.setPermission(permissionObject);
            groupManager.saveGroup(group);

            translationManager.getMessage("commons.permissions.groups.added-permission").ifPresent(message -> {
                message.setVariableValue("group", groupName)
                        .setVariableValue("permission", permission)
                        .setVariableValue("denied", denied + "")
                        .setVariableValue("weight", weight + "");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
            });
        });

        return true;
    }
}
