package com.artillexstudios.axdisplayconverter.entityid;

import com.artillexstudios.axdisplayconverter.AxDisplayConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntityManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<EntityType, Integer> entityIDs = new HashMap<>();

    public static void init() {
        try {
            InputStream stream = AxDisplayConverter.getInstance().getResource("entity_type.json");
            InputStreamReader isReader = new InputStreamReader(stream);
            JsonReader jsReader = new JsonReader(isReader);
            JsonObject object = gson.fromJson(jsReader, JsonObject.class);

            String goodKey = null;
            for (String key : object.keySet()) {
                if (!isValidVersion(key)) continue;
                goodKey = key;
                break;
            }

            JsonObject idMap = object.get(goodKey).getAsJsonObject();
            for (EntityType entityType : EntityType.values()) {
                try {
                    entityIDs.put(entityType, idMap.get(entityType.name().toLowerCase(Locale.ENGLISH)).getAsInt());
                } catch (NullPointerException ex) {
                    // if there is no id, just ignore
                }
            }

            jsReader.close();
            isReader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<EntityType, Integer> getEntityIDs() {
        return entityIDs;
    }

    public static boolean isValidVersion(String testVersion) {
        testVersion = testVersion.substring(2).replace("_", ".");
        int[] testParts = getVersionParts(testVersion);
        int[] currentParts = getVersionParts(Bukkit.getServer().getBukkitVersion().split("-")[0]);

        for (int i = 0; i < 3; i++) {
            if (testParts[i] > currentParts[i]) return false;
        }
        return true;
    }

    private static int[] getVersionParts(String version) {
        String[] split = version.split("\\.");
        int[] parts = new int[3];
        for (int i = 0; i < 3; i++) {
            if (split.length - 1 < i) continue;
            parts[i] = Integer.parseInt(split[i]);
        }
        return parts;
    }
}
