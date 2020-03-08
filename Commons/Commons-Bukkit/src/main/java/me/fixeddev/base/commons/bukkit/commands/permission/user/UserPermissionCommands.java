package me.fixeddev.base.commons.bukkit.commands.permission.user;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.permissions.Permission;
import me.fixeddev.base.api.user.User;
import me.fixeddev.base.api.user.UserManager;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.commons.translations.TranslationManager;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Default;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import me.fixeddev.ebcm.parametric.annotation.Required;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.StringJoiner;

@ACommand(names = "permission")
@Required
public class UserPermissionCommands implements CommandClass {

    @Inject
    private UserManager userManager;
    @Inject
    private TranslationManager translationManager;
    @Inject
    private PermissionDataCalculator dataCalculator;

    private Injector injector;

    public UserPermissionCommands(PermissionsUserCommands parent) {
        parent.getInjector().injectMembers(this);

        this.injector = parent.getInjector();
    }

    @ACommand(names = {"add"})
    public boolean permissionAdd(@Injected(true) CommandSender sender, OfflinePlayer target, String permission, @Default("false") Boolean denied, @Default("1") Integer weight) {
        FutureUtils.addCallback(userManager.getUserById(target.getUniqueId()), optionalUser -> {
            if (!optionalUser.isPresent()) {
                translationManager.getMessage("user.not-exists").ifPresent(message -> {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });

                return;
            }

            User user = optionalUser.get();

            Permission permissionObject = Permission.of(permission, denied, weight);

            FutureUtils.addCallback(user.calculatePermissionsData(dataCalculator), permissionData -> {
                permissionData.setPermission(permissionObject);

                dataCalculator.save(permissionData);

                translationManager.getMessage("commons.permissions.user.added-permission").ifPresent(message -> {
                    message.setVariableValue("player", target.getName())
                            .setVariableValue("permission", permission)
                            .setVariableValue("denied", denied + "")
                            .setVariableValue("weight", weight + "");

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });
            });
        });

        return true;
    }

    @ACommand(names = {"remove"})
    public boolean permissionRemove(@Injected(true) CommandSender sender, OfflinePlayer target, String permission) {
        FutureUtils.addCallback(userManager.getUserById(target.getUniqueId()), optionalUser -> {
            if (!optionalUser.isPresent()) {
                translationManager.getMessage("user.not-exists").ifPresent(message ->
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en"))));

                return;
            }

            User user = optionalUser.get();

            FutureUtils.addCallback(user.calculatePermissionsData(dataCalculator), permissionData -> {
                Permission removedPermission = permissionData.removePermission(permission);

                if (removedPermission == null) {
                    translationManager.getMessage("commons.permissions.user.failed-removed-permission").ifPresent(message -> {
                        message.setVariableValue("group", target.getName())
                                .setVariableValue("permission", permission);

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                    });

                    return;
                }

                dataCalculator.save(permissionData);

                translationManager.getMessage("commons.permissions.user.removed-permission").ifPresent(message -> {
                    message.setVariableValue("player", target.getName())
                            .setVariableValue("permission", permission)
                            .setVariableValue("denied", removedPermission.isDenied() + "")
                            .setVariableValue("weight", removedPermission.getWeight() + "");

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });
            });
        });

        return true;
    }

    @ACommand(names = {"list"})
    public boolean permissionRemove(@Injected(true) CommandSender sender, OfflinePlayer target) {
        FutureUtils.addCallback(userManager.getUserById(target.getUniqueId()), optionalUser -> {
            if (!optionalUser.isPresent()) {
                translationManager.getMessage("user.not-exists").ifPresent(message ->
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en"))));

                return;
            }

            User user = optionalUser.get();

            FutureUtils.addCallback(user.calculatePermissionsData(dataCalculator), permissionData -> {
                List<Permission> userPermissions = permissionData.getEffectivePermissions(target);

                if (userPermissions.isEmpty()) {
                    translationManager.getMessage("commons.permissions.user.list-permission-none").ifPresent(message -> {
                        message.setVariableValue("player", target.getName());

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                    });

                    return;
                }

                StringJoiner permissionList = new StringJoiner("\n");

                userPermissions.forEach(permission ->
                        translationManager.getMessage("commons.permissions.user.list-permission-line")
                                .ifPresent(translatableMessage -> {
                                    translatableMessage.setVariableValue("permission", permission.getName());
                                    translatableMessage.setVariableValue("value", permission.isDenied() ? "&cFalse" : "&aTrue");

                                    permissionList.add(translatableMessage.getMessageForLang("en"));
                                })
                );

                translationManager.getMessage("commons.permissions.user.list-permission").ifPresent(message -> {
                    message.setVariableValue("player", target.getName());
                    message.setVariableValue("permissions", "\n" + permissionList.toString());

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });

            });
        });

        return true;
    }
}
