package me.Bentipa.BungeeSignsFree.bungeeconfig;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import me.Bentipa.BungeeSignsFree.Core;
import org.bukkit.ChatColor;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Bentipa(Benjamin)
 * @year 2016
 *
 */
public class BungeeCordConfigGetter {

    private Core c;
    private YamlConfiguration bconfig;
    private BungeeConfig bc;

    private boolean error = false;

    public boolean isError(){
    	return error; 
    }
    
    public BungeeCordConfigGetter(me.Bentipa.BungeeSignsFree.Core bSignsMain) {
        this.c = bSignsMain;

        File f = new File(bSignsMain.getDataFolder() + "/bungeeconfig.yml");
        if (f.exists()) {
            c.getLogger().info("BungeeConfig File found!");
        } else {
            try {
                f.createNewFile();
                c.getLogger().info("Created BungeeConfig File - Copy now yours and replace the created one.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                c.getLogger().info("Could not create BungeeConfig File.");
            }
        }
        bconfig = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection servers = bconfig.getConfigurationSection("servers");
        HashMap<String, InetSocketAddress> servershash = new HashMap<>();
        if (servers == null) {
            error = true;
            Core.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error in loading bungeeconfig.yml! Be sure you copied the config.yml of BungeeCord into bungeeconfig.yml! Disabeling!");
            Core.getInstance().getPluginLoader().disablePlugin(Core.getInstance());
            return;
        }
        Set<String> servernames = servers.getKeys(false);
        c.getLogger().info("################################");
        for (String serv : servernames) {
            c.getLogger().log(Level.INFO, "Found Server: {0}", serv);
            String val = servers.getString(serv + ".address");
            String[] vals = val.split(":");
            servershash.put(serv, new InetSocketAddress(vals[0], Integer.valueOf(vals[1])));
            c.getLogger().log(Level.INFO, "With Address:  {0}", val);
            c.getLogger().info("################################");
        }
        bc = new BungeeConfig(servershash);
    }

    public BungeeConfig getConfig() {
        return bc;
    }

    public class BungeeConfig {

        private HashMap<String, InetSocketAddress> servers;

        public BungeeConfig(HashMap<String, InetSocketAddress> servers) {
            this.setServers(servers);
        }

        public HashMap<String, InetSocketAddress> getServers() {
            return servers;
        }

        public void setServers(HashMap<String, InetSocketAddress> servers) {
            this.servers = servers;
        }
    }
}
