package com.warterpl.minecartannoucer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class MinecartAnnouncer extends JavaPlugin implements Listener {

    public static JavaPlugin plugin;
    private final MessageDisplayer messageDisplayer;
    public static Map<Block, List<String>> messages = new HashMap<>();
    private final Map<Player, Block> lastPlayerRail = new HashMap<>();
    private final Map<Boat, Set<Block>> lastBlocksUnderBoat = new HashMap<>();
    private final Map<Boat, Location> prevBoatLocation = new HashMap<>();

    public MinecartAnnouncer()
    {
        plugin = this;
        messageDisplayer = new MessageDisplayer();
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Minecart minecart) handleMinecart(minecart);
        else if(event.getVehicle() instanceof Boat boat) handleBoat(boat);
    }

    public void handleBoat(Boat boat) {
        if (boat.getPassengers().isEmpty()) return;

        Set<Block> currentBlocks = getBlocksUnderBoat(boat);
        Set<Block> lastBlocks = lastBlocksUnderBoat.getOrDefault(boat, Collections.emptySet());

        for (Block block : currentBlocks) {
            if (block.getType() != Config.IceActivator || lastBlocks.contains(block))
                continue;

            if (!MinecartAnnouncer.messages.containsKey(block))
                continue;

            if(isDirectional(block, Config.DirectionalIceMat, 0))
            {
                var prevBoatPos = prevBoatLocation.get(boat);
                if(prevBoatPos != null)
                {
                    Vector velocity = boat.getLocation().toVector().subtract(prevBoatPos.toVector());
                    velocity = velocity.normalize();

                    if(isCommingDirectional(block, velocity))
                        SendBoat(boat, block);
                }
            }
            else
                SendBoat(boat, block);
        }

        lastBlocksUnderBoat.put(boat, currentBlocks);
        prevBoatLocation.put(boat, boat.getLocation());
    }
    void SendBoat(Boat boat, Block block)
    {
        for (Entity entity : boat.getPassengers()) {
        if (entity instanceof Player player) {
            messageDisplayer.SendMessage(player, MinecartAnnouncer.messages.get(block));
        }
    }
    }

    boolean isCommingDirectional(Block block, Vector velocity)
    {
        double x = velocity.getX();
        double z = velocity.getZ();

        double threshold = 0.5;

        if (z > threshold && Math.abs(x) < threshold) {
            return block.getRelative(0, 0, -1).getType() == Config.DirectionalIceMat; // N
        } else if (z > threshold && x > threshold) {
            return block.getRelative(0, 0, -1).getType() == Config.DirectionalIceMat &&
                    block.getRelative(-1, 0, 0).getType() == Config.DirectionalIceMat; // NE
        } else if (Math.abs(z) < threshold && x > threshold) {
            return block.getRelative(-1, 0, 0).getType() == Config.DirectionalIceMat; // E
        } else if (z < -threshold && x > threshold) {
            return block.getRelative(0, 0, 1).getType() == Config.DirectionalIceMat &&
                    block.getRelative(-1, 0, 0).getType() == Config.DirectionalIceMat; // SE
        } else if (z < -threshold && Math.abs(x) < threshold) {
            return block.getRelative(0, 0, 1).getType() == Config.DirectionalIceMat; // S
        } else if (z < -threshold && x < -threshold) {
            return block.getRelative(0, 0, 1).getType() == Config.DirectionalIceMat &&
                    block.getRelative(1, 0, 0).getType() == Config.DirectionalIceMat; // SW
        } else if (Math.abs(z) < threshold && x < -threshold) {
            return block.getRelative(1, 0, 0).getType() == Config.DirectionalIceMat; // W
        } else if (z > threshold && x < -threshold) {
            return block.getRelative(0, 0, -1).getType() == Config.DirectionalIceMat &&
                    block.getRelative(1, 0, 0).getType() == Config.DirectionalIceMat; // NW
        }


        return false;
    }

    private Set<Block> getBlocksUnderBoat(Boat boat) {
        Set<Block> blocks = new HashSet<>();
        BoundingBox box = boat.getBoundingBox();
        World world = boat.getWorld();

        double minX = box.getMinX() - 0.1;
        double maxX = box.getMaxX() + 0.1;
        double minZ = box.getMinZ() - 0.1;
        double maxZ = box.getMaxZ() + 0.1;
        int y = (int) box.getCenterY();

        int startX = (int) Math.floor(minX);
        int endX = (int) Math.floor(maxX);
        int startZ = (int) Math.floor(minZ);
        int endZ = (int) Math.floor(maxZ);

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                blocks.add(world.getBlockAt(x, y, z));
            }
        }

        return blocks;
    }

    public void handleMinecart(Minecart minecart)
    {
        if (minecart.getPassengers().isEmpty()) return;
        Entity passenger = minecart.getPassengers().get(0);
        if (!(passenger instanceof Player player)) return;

        Block currentBlock = minecart.getLocation().getBlock().getRelative(0, 0, 0);
        Block previousBlock = lastPlayerRail.get(player);

        boolean repeating = previousBlock != null && previousBlock.equals(currentBlock);

        if (!repeating) {
            assert previousBlock != null;
            boolean directionalCondition = previousBlock.getRelative(0, -1, 0).getType() == Config.DirectionaRailMat;
            boolean directional = isDirectional(currentBlock, Config.DirectionaRailMat, -1);
            if (!directional || directionalCondition) {
                if (MinecartAnnouncer.messages.containsKey(currentBlock)) {
                    messageDisplayer.SendMessage(player, messages.get(currentBlock));
                }
            }
        }
        lastPlayerRail.put(player, currentBlock);
    }
    private boolean isDirectional(Block block, Material checker, int yOffset)
    {
        return (block.getRelative(-1, yOffset, 0).getType() == checker ||
                block.getRelative(1, yOffset, 0).getType() == checker ||
                block.getRelative(0, yOffset, -1).getType() == checker ||
                block.getRelative(0, yOffset, 1).getType() == checker
        );
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        if(isRightSetup(event.getClickedBlock()))
            MessageAssigner.AssignMessage(event);
    }
    private boolean isRightSetup(Block block) {
        if(block == null) return false;

        if(block.getType() == Config.Rail &&
                block.getRelative(0, -1, 0).getType() == Config.RailSetupMat)
            return true;

        if(block.getType() == Config.IceActivator &&
                Config.IceSetupMats.isTagged(block.getRelative(0, -1, 0).getType()))
            return true;

        return false;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dev_showRailMessages")) {
            if (sender instanceof Player player && player.isOp()) {

                if (messages.isEmpty()) {
                    player.sendMessage("There aren't any asigned messages.");
                } else {
                    player.sendMessage("Assigned messages:");
                    for (Map.Entry<Block, List<String>> entry : messages.entrySet()) {
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


    @EventHandler
    public void onBoatDestroy(VehicleDestroyEvent event) {
        if (event.getVehicle() instanceof Boat boat) {
            lastBlocksUnderBoat.remove(boat);
            prevBoatLocation.remove(boat);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastPlayerRail.remove(event.getPlayer());
    }



    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        getServer().getPluginManager().registerEvents(this, this);

        FileHandler.SetupFile();
        messages = FileHandler.loadRailMessages();
    }

    @Override
    public void onDisable() {
        FileHandler.saveRailMessages(messages);
    }
}
