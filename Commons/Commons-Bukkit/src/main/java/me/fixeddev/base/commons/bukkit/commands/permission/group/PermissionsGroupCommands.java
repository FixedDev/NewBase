package me.fixeddev.base.commons.bukkit.commands.permission.group;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.fixeddev.base.api.future.FutureUtils;
import me.fixeddev.base.api.permissions.Permission;
import me.fixeddev.base.api.permissions.group.Group;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.commons.translations.TranslationManager;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Default;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import me.fixeddev.ebcm.parametric.annotation.Required;
import me.fixeddev.ebcm.parametric.annotation.SubCommandClasses;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

@ACommand(names = "group")
@Required
@SubCommandClasses(GroupPermissionCommands.class)
public class PermissionsGroupCommands implements CommandClass {

    @Inject
    private GroupManager groupManager;
    @Inject
    private TranslationManager translationManager;
    @Inject
    private Injector injector;

    Injector getInjector() {
        return injector;
    }

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

}
