package net.grandtheftmc.vice.utils;

import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.*;

/**
 * Created by Timothy Lampen on 8/20/2017.
 */
public class LocationUtil {

    public static final Set<Material> HOLLOW_MATERIALS = new HashSet<Material>();
    public static final Vector3D[] VOLUME;
    public static final int RADIUS = 3;

    static
    {
        HOLLOW_MATERIALS.add(Material.AIR);
        HOLLOW_MATERIALS.add(Material.SAPLING);
        HOLLOW_MATERIALS.add(Material.POWERED_RAIL);
        HOLLOW_MATERIALS.add(Material.DETECTOR_RAIL);
        HOLLOW_MATERIALS.add(Material.LONG_GRASS);
        HOLLOW_MATERIALS.add(Material.DEAD_BUSH);
        HOLLOW_MATERIALS.add(Material.YELLOW_FLOWER);
        HOLLOW_MATERIALS.add(Material.RED_ROSE);
        HOLLOW_MATERIALS.add(Material.BROWN_MUSHROOM);
        HOLLOW_MATERIALS.add(Material.RED_MUSHROOM);
        HOLLOW_MATERIALS.add(Material.TORCH);
        HOLLOW_MATERIALS.add(Material.REDSTONE_WIRE);
        HOLLOW_MATERIALS.add(Material.SEEDS);
        HOLLOW_MATERIALS.add(Material.SIGN_POST);
        HOLLOW_MATERIALS.add(Material.WOODEN_DOOR);
        HOLLOW_MATERIALS.add(Material.LADDER);
        HOLLOW_MATERIALS.add(Material.RAILS);
        HOLLOW_MATERIALS.add(Material.WALL_SIGN);
        HOLLOW_MATERIALS.add(Material.LEVER);
        HOLLOW_MATERIALS.add(Material.STONE_PLATE);
        HOLLOW_MATERIALS.add(Material.IRON_DOOR_BLOCK);
        HOLLOW_MATERIALS.add(Material.WOOD_PLATE);
        HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH_OFF);
        HOLLOW_MATERIALS.add(Material.REDSTONE_TORCH_ON);
        HOLLOW_MATERIALS.add(Material.STONE_BUTTON);
        HOLLOW_MATERIALS.add(Material.SNOW);
        HOLLOW_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
        HOLLOW_MATERIALS.add(Material.DIODE_BLOCK_OFF);
        HOLLOW_MATERIALS.add(Material.DIODE_BLOCK_ON);
        HOLLOW_MATERIALS.add(Material.PUMPKIN_STEM);
        HOLLOW_MATERIALS.add(Material.MELON_STEM);
        HOLLOW_MATERIALS.add(Material.VINE);
        HOLLOW_MATERIALS.add(Material.FENCE_GATE);
        HOLLOW_MATERIALS.add(Material.WATER_LILY);
        HOLLOW_MATERIALS.add(Material.NETHER_WARTS);
        HOLLOW_MATERIALS.add(Material.CARPET);
    }

    public static class Vector3D
    {
        public int x;
        public int y;
        public int z;

        public Vector3D(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    static
    {
        List<Vector3D> pos = new ArrayList<Vector3D>();
        for (int x = -RADIUS; x <= RADIUS; x++)
        {
            for (int y = -RADIUS; y <= RADIUS; y++)
            {
                for (int z = -RADIUS; z <= RADIUS; z++)
                {
                    pos.add(new Vector3D(x, y, z));
                }
            }
        }
        Collections.sort(
                pos, new Comparator<Vector3D>()
                {
                    @Override
                    public int compare(Vector3D a, Vector3D b)
                    {
                        return (a.x * a.x + a.y * a.y + a.z * a.z) - (b.x * b.x + b.y * b.y + b.z * b.z);
                    }
                });
        VOLUME = pos.toArray(new Vector3D[0]);
    }


    public static boolean isBlockAboveAir(final World world, final int x, final int y, final int z)
    {
        if (y > world.getMaxHeight())
        {
            return true;
        }
        return HOLLOW_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType());
    }

    public static Location getSafeDestination(final Location loc)
    {
        final World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int)Math.round(loc.getY());
        int z = loc.getBlockZ();
        final int origX = x;
        final int origY = y;
        final int origZ = z;
        while (isBlockAboveAir(world, x, y, z))
        {
            y -= 1;
            if (y < 0)
            {
                y = origY;
                break;
            }
        }
        if (isBlockUnsafe(world, x, y, z))
        {
            x = Math.round(loc.getX()) == origX ? x - 1 : x + 1;
            z = Math.round(loc.getZ()) == origZ ? z - 1 : z + 1;
        }
        int i = 0;
        while (isBlockUnsafe(world, x, y, z))
        {
            i++;
            if (i >= VOLUME.length)
            {
                x = origX;
                y = origY + RADIUS;
                z = origZ;
                break;
            }
            x = origX + VOLUME[i].x;
            y = origY + VOLUME[i].y;
            z = origZ + VOLUME[i].z;
        }
        while (isBlockUnsafe(world, x, y, z))
        {
            y += 1;
            if (y >= world.getMaxHeight())
            {
                x += 1;
                break;
            }
        }
        while (isBlockUnsafe(world, x, y, z))
        {
            y -= 1;
            if (y <= 1)
            {
                x += 1;
                y = world.getHighestBlockYAt(x, z);
                if (x - 48 > loc.getBlockX())
                {
                    return null;
                }
            }
        }
        return new Location(world, x + 0.5, y, z + 0.5, loc.getYaw(), loc.getPitch());
    }

    public static boolean isBlockUnsafe(final World world, final int x, final int y, final int z)
    {

        if (isBlockDamaging(world, x, y, z))
        {
            return true;
        }
        return isBlockAboveAir(world, x, y, z);
    }

    public static boolean isPlayerNearby(Location loc, double radius) {
        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getWorld().equals(loc.getWorld()) && player.getLocation().distance(loc)<=radius);
    }

    public static boolean isBlockDamaging(final World world, final int x, final int y, final int z)
    {
        final Block below = world.getBlockAt(x, y - 1, z);
        if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA)
        {
            return true;
        }
        if (below.getType() == Material.FIRE)
        {
            return true;
        }
        if (below.getType() == Material.BED_BLOCK)
        {
            return true;
        }
        if ((!HOLLOW_MATERIALS.contains(world.getBlockAt(x, y, z).getType())) || (!HOLLOW_MATERIALS.contains(world.getBlockAt(x, y + 1, z).getType())))
        {
            return true;
        }
        return false;
    }
}
