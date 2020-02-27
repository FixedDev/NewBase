package me.fixeddev.base.commons.bukkit.commands;

import com.google.inject.Inject;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.api.user.User;
import me.fixeddev.base.api.user.UserManager;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.commons.translations.TranslationManager;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import static me.fixeddev.base.api.future.FutureUtils.addCallback;

public class PermissionsUserCommands implements CommandClass {

    @Inject
    private PermissionDataCalculator dataCalculator;
    @Inject
    private UserManager userLocalCache;

    @Inject
    private GroupManager groupManager;

    @Inject
    private TranslationManager translationManager;

    @ACommand(names = "grant")
    public boolean grantCommand(@Injected(true) CommandSender sender, String rank, OfflinePlayer target) {
        addCallback(userLocalCache.getUserById(target.getUniqueId().toString()), optionalUser -> {
            if (!optionalUser.isPresent()) {
                translationManager.getMessage("user.not-exists").ifPresent(message -> {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });

                return;
            }

            User user = optionalUser.get();

            addCallback(groupManager.getGroupByName(rank), group -> {
                if (group == null) {
                    translationManager.getMessage("commons.permissions.groups.not-exists").ifPresent(message -> {
                        message.setVariableValue("group", rank);

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                    });

                    return;
                }

                user.setPrimaryGroup(group.getName());
                user.calculatePermissionsData(dataCalculator);

                translationManager.getMessage("commons.permissions.user.set-group").ifPresent(message -> {
                    message.setVariableValue("group", rank);
                    message.setVariableValue("player", target.getName());

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });

                userLocalCache.save(user);
            });
        });
        return true;
    }

    @ACommand(names = "groupof")
    public boolean groupOfCommand(@Injected(true) CommandSender sender, OfflinePlayer target) {
        addCallback(userLocalCache.getUserById(target.getUniqueId().toString()), optionalUser -> {
            if (!optionalUser.isPresent()) {
                translationManager.getMessage("user.not-exists").ifPresent(message ->
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")))
                );

                return;
            }

            User user = optionalUser.get();

            translationManager.getMessage("commons.user.permissions.group-of").ifPresent(message -> {
                message.setVariableValue("player", user.getLastName().orElse(target.getName()));
                message.setVariableValue("group", user.getPrimaryGroup());

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
            });
        });
        return true;
    }
}
