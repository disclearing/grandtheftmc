package net.grandtheftmc.vice.utils.advancement;

import org.bukkit.Bukkit;

public enum FrameType {
    TASK("task"),
    GOAL("goal"),
    CHALLENGE("challenge");
    private String name;

    FrameType(String name) {
        this.name = name;
    }

    public static FrameType getFromString(String frameType) {
        if (frameType.equalsIgnoreCase("random")) return FrameType.RANDOM();
        else try {
            return FrameType.valueOf(frameType);
        } catch (EnumConstantNotPresentException e) {
            Bukkit.getLogger().info("[AdvancementUtil] Unknown FrameType given. Using default (TASK)");
            return FrameType.TASK;
        }
    }

    public static FrameType RANDOM() {
        FrameType[] frameTypes = FrameType.values();
        return frameTypes[(int) (Math.random() * (frameTypes.length - 1))];
    }

    public String toString() {
        return name;
    }
}
