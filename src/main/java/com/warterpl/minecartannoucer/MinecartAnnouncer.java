package com.warterpl.minecartannoucer;

import com.warterpl.minecartannoucer.Commands.DevCmdexec;
import com.warterpl.minecartannoucer.Messages.MessageAssigner;
import com.warterpl.minecartannoucer.Messages.MessageDisplayer;
import com.warterpl.minecartannoucer.VehicleHanlders.BoatHandler;
import com.warterpl.minecartannoucer.VehicleHanlders.MinecartHandler;

import com.warterpl.minecartannoucer.VehicleHanlders.VehicleHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.cache.*;

import java.util.List;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinecartAnnouncer extends JavaPlugin implements Listener {

    public static JavaPlugin plugin;
    private static final MessageDisplayer messageDisplayer = new MessageDisplayer();
    public static final DatabaseHandler dbHandler = new DatabaseHandler();
    private final Map<UUID, VehicleHandler> vehicleHandlers = new HashMap<>();
    public static Cache<String, List<String>> messageCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    public static HashSet<Block> msgBlocks = new HashSet<>();
    public MinecartAnnouncer()
    {
        plugin = this;
    }
    public static MessageDisplayer getMessageDisplayer()
    { return messageDisplayer; }

    public static void ConsoleLog(String msg)
    {
        Bukkit.getLogger().info(msg);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        UUID vId = vehicle.getUniqueId();

        if(!VehicleHandler.ContainsPlayer(vehicle))
        {
            vehicleHandlers.remove(vId);
            return;
        }
        if(!vehicleHandlers.containsKey(vId))
        {
            if(vehicle instanceof Boat boat)
                vehicleHandlers.put(vId, new BoatHandler(boat, vId));
            if(vehicle instanceof Minecart minecart)
                vehicleHandlers.put(vId, new MinecartHandler(minecart, vId));
        }
        vehicleHandlers.get(vId).Handle();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        if(!isRightSetup(event.getClickedBlock()))
            return;

        MessageAssigner.AssignMessage(event);
    }
    private boolean isRightSetup(Block block) {
        if(block == null) return false;

        if(block.getType() == Config.Rail &&
                block.getRelative(0, -1, 0).getType() == Config.RailSetupMat)
            return true;

        return block.getType() == Config.IceActivator &&
                Config.IceSetupMats.isTagged(block.getRelative(0, -1, 0).getType());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if(block.getType() == Config.Rail || block.getRelative(0, 1, 0).getType() == Config.Rail)
            MessageAssigner.RemoveMessage(block, event);
        if(block.getType() == Config.IceActivator ||
            block.getRelative(0, 1, 0).getType() == Config.IceActivator)
            MessageAssigner.RemoveMessage(block, event);
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        vehicleHandlers.remove(event.getVehicle().getUniqueId());
    }

    public static List<String> GetMessages(Block block)
    {
        List<String> cached = messageCache.getIfPresent(getBlockKey(block));
        if (cached != null) return cached;

        List<String> fromDb = dbHandler.GetMessagesAt(block);
        if (!fromDb.isEmpty()) messageCache.put(getBlockKey(block), fromDb);
        return fromDb;
    }
    public static String getBlockKey(Block block) {
        return block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";" + block.getZ();
    }

    @Override
    public void onEnable() {
        dbHandler.Connect();
        dbHandler.SetupTable();
        msgBlocks = dbHandler.GetAllAssignedBlocks();

        this.getCommand("dev_showRailMessages").setExecutor(new DevCmdexec());

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        dbHandler.Disconnect();
    }
}
