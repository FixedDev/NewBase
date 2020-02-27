package me.fixeddev.base.commons.bukkit.commands;

import com.google.inject.Inject;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.permissions.Permission;
import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.commons.translations.TranslationManager;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Default;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@ACommand(names = "group")
public class PermissionsGroupCommands implements CommandClass {

    @Inject
    private GroupManager groupManager;
    @Inject
    private TranslationManager translationManager;

    @ACommand(names = "list")
    public boolean listGroups(@Injected(true) CommandSender sender) {
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

    @ACommand(names = "create")
    public boolean createGroup(@Injected(true) CommandSender sender, String groupName, @Default("1") Integer weight) {
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

    @ACommand(names = "delete")
    public boolean deleteGroup(@Injected(true) CommandSender sender, String groupName) {
        FutureUtils.addCallback(groupManager.deleteGroupByName(groupName), group -> {
            if (group == null) {
                translationManager.getMessage("commons.permissions.groups.not-exists").ifPresent(message -> {
                    message.setVariableValue("group", groupName);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });
                return;
            }

            translationManager.getMessage("commons.permissions.groups.deleted").ifPresent(message -> {
                message.setVariableValue("group", groupName);

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
            });
        });

        return true;
    }

    @ACommand(names = {"permission-add", "perm-add"})
    public boolean permissionAdd(@Injected(true) CommandSender sender, String groupName, String permission, @Default("false") Boolean denied, @Default("1") Integer weight) {
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

    @ACommand(names = {"remove-permission", "remove-perm"})
    public boolean permissionRemove(@Injected(true) CommandSender sender, String groupName, String permission) {
        FutureUtils.addCallback(groupManager.getGroupByName(groupName), group -> {
            if (group == null) {
                translationManager.getMessage("commons.permissions.groups.not-exists").ifPresent(message -> {
                    message.setVariableValue("group", groupName);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });
                return;
            }

            Permission removedPermission = group.removePermission(permission);

            if (removedPermission == null) {
                translationManager.getMessage("commons.permissions.groups.failed-removed-permission").ifPresent(message -> {
                    message.setVariableValue("group", groupName)
                            .setVariableValue("permission", permission);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });

                return;
            }

            groupManager.saveGroup(group);

            translationManager.getMessage("commons.permissions.groups.removed-permission").ifPresent(message -> {
                message.setVariableValue("group", groupName)
                        .setVariableValue("permission", permission)
                        .setVariableValue("denied", removedPermission.isDenied() + "")
                        .setVariableValue("weight", removedPermission.getWeight() + "");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
            });
        });

        return true;
    }

    @ACommand(names = {"permission-list", "perm-list"})
    public boolean permissionRemove(@Injected(true) CommandSender sender, String groupName) {
        FutureUtils.addCallback(groupManager.getGroupByName(groupName), group -> {
            if (group == null) {
                translationManager.getMessage("commons.permissions.groups.not-exists").ifPresent(message -> {
                    message.setVariableValue("group", groupName);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });
                return;
            }

            List<Permission> groupPermissions = group.getEffectivePermissions(sender);

            if (groupPermissions.isEmpty()) {
                translationManager.getMessage("commons.permissions.groups.list-permission-none").ifPresent(message -> {
                    message.setVariableValue("group", groupName);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });

                return;
            }


            StringJoiner permissionList = new StringJoiner("\n");

            group.getEffectivePermissions(sender).forEach(permission -> {
                translationManager.getMessage("commons.permissions.groups.list-permission-line")
                        .ifPresent(translatableMessage -> {
                            translatableMessage.setVariableValue("permission", permission.getName());
                            translatableMessage.setVariableValue("value", permission.isDenied() ? "&cFalse" : "&aTrue");

                            permissionList.add(translatableMessage.getMessageForLang("en"));
                        });
            });

            translationManager.getMessage("commons.permissions.groups.list-permission").ifPresent(message -> {
                message.setVariableValue("group", groupName);
                message.setVariableValue("permissions", permissionList.toString());

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
            });


        });

        return true;
    }
}
