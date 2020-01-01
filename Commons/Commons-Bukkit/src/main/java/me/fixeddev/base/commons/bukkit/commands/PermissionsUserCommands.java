package me.fixeddev.base.commons.bukkit.commands;

import com.google.inject.Inject;
import me.fixeddev.base.api.datamanager.ObjectLocalCache;
import me.fixeddev.base.api.datamanager.ObjectRepository;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.api.user.User;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.commons.translations.TranslationManager;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import static me.fixeddev.base.api.future.FutureUtils.addCallback;

public class PermissionsUserCommands implements CommandClass {

    @Inject
    private PermissionDataCalculator dataCalculator;
    @Inject
    private ObjectLocalCache<User> userLocalCache;
    @Inject
    private ObjectRepository<User> userObjectRepository;

    @Inject
    private GroupManager groupManager;

    @Inject
    private TranslationManager translationManager;

    @ACommand(names = "grant")
    public boolean grantCommand(CommandSender sender, String rank, OfflinePlayer target) {
        addCallback(userLocalCache.getOrFind(target.getUniqueId().toString()), optionalUser -> {
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

                translationManager.getMessage("commons.permissions.user.set-group").ifPresent(message -> {
                    message.setVariableValue("group", rank);
                    message.setVariableValue("player", target.getName());

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.getMessageForLang("en")));
                });

                userLocalCache.cacheObject(user);
                userObjectRepository.save(user);
            });
        });
        return true;
    }
}
