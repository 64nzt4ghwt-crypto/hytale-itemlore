package com.howlstudio.itemlore;
import com.hypixel.hytale.component.Ref; import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.*; import java.util.*;
public class LoreManager {
    private final Path dataDir;
    private final Map<String,List<String>> loreRegistry=new LinkedHashMap<>(); // itemName → lines
    public LoreManager(Path d){this.dataDir=d;try{Files.createDirectories(d);}catch(Exception e){}load();}
    public int getCount(){return loreRegistry.size();}
    public void save(){try{StringBuilder sb=new StringBuilder();for(var e:loreRegistry.entrySet())for(String l:e.getValue())sb.append(e.getKey()+"\t"+l+"\n");Files.writeString(dataDir.resolve("lore.txt"),sb.toString());}catch(Exception e){}}
    private void load(){try{Path f=dataDir.resolve("lore.txt");if(!Files.exists(f))return;for(String l:Files.readAllLines(f)){String[]p=l.split("\t",2);if(p.length==2)loreRegistry.computeIfAbsent(p[0].toLowerCase(),k->new ArrayList<>()).add(p[1]);}}catch(Exception e){}}
    public AbstractPlayerCommand getLoreCommand(){
        return new AbstractPlayerCommand("lore","Manage item lore. /lore <item> | /lore <item> add <text> | /lore <item> del <n> | /lore list"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                String[]args=ctx.getInputString().trim().split("\\s+",3);String sub=args.length>0?args[0].toLowerCase():"list";
                if(sub.equals("list")){
                    if(loreRegistry.isEmpty()){playerRef.sendMessage(Message.raw("[Lore] No lore entries."));return;}
                    playerRef.sendMessage(Message.raw("[Lore] Items with lore ("+loreRegistry.size()+"):"));
                    for(String k:loreRegistry.keySet())playerRef.sendMessage(Message.raw("  §6"+k+"§r ("+loreRegistry.get(k).size()+" lines)"));
                    return;
                }
                if(args.length==1){// view lore for item
                    List<String> ls=loreRegistry.get(sub);if(ls==null||ls.isEmpty()){playerRef.sendMessage(Message.raw("[Lore] No lore for: "+sub));return;}
                    playerRef.sendMessage(Message.raw("§6[Lore] "+sub+":"));for(int i=0;i<ls.size();i++)playerRef.sendMessage(Message.raw("  §7"+(i+1)+"§r. "+ls.get(i)));
                    return;
                }
                String op=args[1].toLowerCase();
                if(op.equals("add")&&args.length==3){loreRegistry.computeIfAbsent(sub,k->new ArrayList<>()).add(args[2]);save();playerRef.sendMessage(Message.raw("[Lore] Added lore to §6"+sub));return;}
                if(op.equals("del")&&args.length==3){try{int n=Integer.parseInt(args[2])-1;List<String> ls=loreRegistry.get(sub);if(ls!=null&&n>=0&&n<ls.size()){ls.remove(n);if(ls.isEmpty())loreRegistry.remove(sub);save();playerRef.sendMessage(Message.raw("[Lore] Deleted line "+(n+1)+" from §6"+sub));}}catch(Exception e){}return;}
                if(op.equals("clear")){loreRegistry.remove(sub);save();playerRef.sendMessage(Message.raw("[Lore] Cleared lore for §6"+sub));return;}
                playerRef.sendMessage(Message.raw("Usage: /lore <item> [add <text>|del <n>|clear] | /lore list"));
            }
        };
    }
}
