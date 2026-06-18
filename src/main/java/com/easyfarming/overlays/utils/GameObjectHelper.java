package com.easyfarming.overlays.utils;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import com.easyfarming.utils.Constants;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for finding game objects by name or ID.
 * Caches name lookups per scene to avoid full scene scans every frame (issue #86).
 */
public class GameObjectHelper {
    private final Client client;

    /** Cache of object name -> ids in current scene. */
    private final Map<String, List<Integer>> objectIdsByNameCache = new HashMap<>();
    /** Scene key when cache was built: regionId * 10 + plane. */
    private int lastSceneKey = -1;

    @Inject
    public GameObjectHelper(Client client) {
        this.client = client;
    }

    /**
     * Gets game object IDs by name in the current scene.
     */
    public List<Integer> getGameObjectIdsByName(String name) {
        if (client.getLocalPlayer() == null) {
            return new ArrayList<>();
        }
        WorldView topWv = client.getTopLevelWorldView();
        if (topWv == null || topWv.getScene() == null) {
            return new ArrayList<>();
        }

        int plane = topWv.getPlane();
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        int sceneKey = regionId * 10 + plane;

        if (sceneKey != lastSceneKey) {
            objectIdsByNameCache.clear();
            lastSceneKey = sceneKey;
        }

        List<Integer> cached = objectIdsByNameCache.get(name);
        if (cached != null) {
            return cached;
        }

        List<Integer> foundObjectIds = new ArrayList<>();
        Tile[][][] tiles = topWv.getScene().getTiles();

        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[plane][x][y];
                if (tile == null) {
                    continue;
                }

                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject != null) {
                        ObjectComposition objectComposition = client.getObjectDefinition(gameObject.getId());
                        if (objectComposition != null && name.equals(objectComposition.getName())) {
                            foundObjectIds.add(gameObject.getId());
                        }
                    }
                }
            }
        }

        objectIdsByNameCache.put(name, foundObjectIds);
        return foundObjectIds;
    }
}
