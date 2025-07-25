package com.warterpl.minecartannoucer;

import com.warterpl.minecartannoucer.rails.FileHandler;
import com.warterpl.minecartannoucer.rails.MessageDisplayer;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import com.warterpl.helper.Pair;

public class MinecartAnnouncer extends JavaPlugin implements Listener {

    public static JavaPlugin plugin;
    private final MessageDisplayer messageDisplayer;
    public static Map<Block, List<String>> railMessages = new HashMap<>();

    public MinecartAnnouncer()
    {
        plugin = this;
        messageDisplayer = new MessageDisplayer();
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Minecart minecart) {
            if (!minecart.getPassengers().isEmpty()) {
                messageDisplayer.SendMessage(minecart);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.WRITTEN_BOOK) {
                ItemStack item = event.getItem();
                BookMeta meta = (BookMeta) item.getItemMeta();

                if (meta != null && meta.hasPages()) {
                    List<String> rawMessages = meta.getPages();
                    List<String> formatedMessages = new ArrayList<>();
                    for(String s : rawMessages) if(!s.isEmpty()) formatedMessages.add(TextFormater.formatText(s));

                    Block block = event.getClickedBlock();
                    if (block != null && block.getType() == Material.RAIL
                            && block.getRelative(0, -1, 0).getType() == Material.IRON_BLOCK) {

                        railMessages.put(block.getRelative(0, 0, 0), formatedMessages);
                        event.getPlayer().sendMessage("Message has been assigned to rail!");

                        event.getItem().setAmount(0);

                        block.getWorld().spawnParticle(Particle.COMPOSTER, block.getLocation().add(0.5, 0, 0.5), 20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Block railBlock = block.getType() == Material.RAIL ? block : block.getRelative(0, 1, 0);

        if (railMessages.containsKey(railBlock)) {
            List<String> formatedMessages = railMessages.get(railBlock);
            List<String> rawMessages = new ArrayList<>();
            for(String s : formatedMessages) rawMessages.add(TextFormater.unformatText(s));

            ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();

            if (meta != null) {
                meta.setPages(rawMessages);
                book.setItemMeta(meta);
            }

            railMessages.remove(railBlock);
            railBlock.getWorld().dropItemNaturally(railBlock.getLocation(), book);

            event.getPlayer().sendMessage("Message has been deleted!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("showRailMessages")) {
            if (sender instanceof Player player && player.isOp()) {

                if (railMessages.isEmpty()) {
                    player.sendMessage("There aren't any asigned messages.");
                } else {
                    player.sendMessage("Assigned messages:");
                    for (Map.Entry<Block, List<String>> entry : railMessages.entrySet()) {
                        Block block = entry.getKey();
                        List<String> messages = entry.getValue();
                        player.sendMessage("Rail: " + block.getLocation() + " - Message: " + messages);
                    }
                }
            } else {
                sender.sendMessage("Only players can execute that command.");
            }
            return true;
        }
        return false;
    }
    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        getServer().getPluginManager().registerEvents(this, this);

        FileHandler.SetupFile();
        railMessages = FileHandler.loadRailMessages();
    }

    @Override
    public void onDisable() {
        FileHandler.saveRailMessages(railMessages);
    }
}
