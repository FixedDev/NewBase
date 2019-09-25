package me.fixeddev.base.api.bukkit;

import com.google.inject.Inject;
import me.fixeddev.base.api.bukkit.listeners.PermissibleInjectUserListener;
import me.fixeddev.base.api.bukkit.listeners.UserLoadListener;
import me.fixeddev.base.api.service.ServiceManager;
import me.fixeddev.inject.ProtectedBinder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BaseBukkitPlugin extends JavaPlugin {

    @Inject
    private ServiceManager serviceManager;

    @Inject
    private PermissibleInjectUserListener permissibleInjectUserListener;
    @Inject
    private UserLoadListener userLoadListener;

    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new ApiBukkitModule(getConfig()));
    }

    @Override
    public void onEnable() {
        try {
            serviceManager.start();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE,"Failed to start services, stopping server", e);
            Bukkit.getServer().shutdown();
        }

        this.getServer().getPluginManager().registerEvents(permissibleInjectUserListener, this);
        this.getServer().getPluginManager().registerEvents(userLoadListener, this);
    }
}
