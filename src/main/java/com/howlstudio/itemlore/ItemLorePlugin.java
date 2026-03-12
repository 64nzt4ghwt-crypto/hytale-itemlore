package com.howlstudio.itemlore;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/** ItemLore — Tag items with custom lore text. Lore stored in a registry by item-name. */
public final class ItemLorePlugin extends JavaPlugin {
    private LoreManager mgr;
    public ItemLorePlugin(JavaPluginInit init){super(init);}
    @Override protected void setup(){
        System.out.println("[ItemLore] Loading...");
        mgr=new LoreManager(getDataDirectory());
        CommandManager.get().register(mgr.getLoreCommand());
        System.out.println("[ItemLore] Ready. "+mgr.getCount()+" lore entries.");
    }
    @Override protected void shutdown(){if(mgr!=null)mgr.save();System.out.println("[ItemLore] Stopped.");}
}
