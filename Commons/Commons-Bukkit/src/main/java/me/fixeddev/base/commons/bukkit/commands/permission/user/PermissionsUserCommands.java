package me.fixeddev.base.commons.bukkit.commands.permission.user;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.fixeddev.base.api.permissions.group.GroupManager;
import me.fixeddev.base.api.user.UserManager;
import me.fixeddev.base.api.user.permissions.PermissionDataCalculator;
import me.fixeddev.base.commons.translations.TranslationManager;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Required;
import me.fixeddev.ebcm.parametric.annotation.SubCommandClasses;

@ACommand(names = "user")
@SubCommandClasses({UserGroupCommands.class})
@Required
public class PermissionsUserCommands implements CommandClass {

    @Inject
    private Injector injector;

    Injector getInjector() {
        return injector;
    }
}
