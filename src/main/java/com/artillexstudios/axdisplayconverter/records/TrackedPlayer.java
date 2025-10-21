package com.artillexstudios.axdisplayconverter.records;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public record TrackedPlayer(Player player, Map<Integer, List<Integer>> replacements) {
}
