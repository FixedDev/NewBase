package me.fixeddev.base.api.bukkit;

import com.google.inject.Inject;
import me.fixeddev.base.api.service.ServiceManager;
import me.fixeddev.inject.ProtectedBinder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BaseBukkitPlugin extends JavaPlugin {

    @Inject
    private ServiceManager serviceManager;


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
    }
}
