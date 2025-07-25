package com.warterpl.minecartannoucer;

import com.google.gson.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.block.Block;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.warterpl.minecartannoucer.MinecartAnnouncer;

public class FileHandler
{
    private static File messagesFile;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void SetupFile()
    {
        messagesFile = new File(MinecartAnnouncer.plugin.getDataFolder(), "railMessages.json");
    }

    public static Map<Block, List<String>> loadRailMessages() {
        Map<Block, List<String>> railMessages = new HashMap<>();
        if (messagesFile.exists()) {
            try (Reader reader = new FileReader(messagesFile)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);

                for (String key : json.keySet()) {
                    Block block = getBlockFromString(key);
                    JsonArray pagesArray = json.getAsJsonArray(key);
                    List<String> pages = new ArrayList<>();
                    for (JsonElement element : pagesArray) {
                        pages.add(element.getAsString());
                    }
                    railMessages.put(block, pages);
                }
            } catch (IOException e) {
                MinecartAnnouncer.plugin.getLogger().warning("Error while loading message from file.");
            }
        }
        return railMessages;
    }

    public static void saveRailMessages(Map<Block, List<String>> railMessages) {
        JsonObject json = new JsonObject();

        for (Map.Entry<Block, List<String>> entry : railMessages.entrySet()) {
            String blockKey = blockToString(entry.getKey());
            List<String> messages = entry.getValue();
            JsonArray jsonArray = new JsonArray();

            for (String page : messages) {
                jsonArray.add(page);
            }

            json.add(blockKey, jsonArray);
        }

        try (Writer writer = new FileWriter(messagesFile)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            MinecartAnnouncer.plugin.getLogger().warning("Error while saving message to file.");
        }
    }

    private static String blockToString(Block block) {
        return block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
    }

    private static Block getBlockFromString(String blockString) {
        String[] parts = blockString.split(":");
        return MinecartAnnouncer.plugin.getServer()
                .getWorld(parts[0])
                .getBlockAt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }
}
