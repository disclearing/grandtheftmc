package net.grandtheftmc.vice.weapon.ranged.lmg;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.LMGWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class CombatMG extends LMGWeapon implements RankedWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public CombatMG() {
        super(
        		(short) 27,
                "Combat MG", //Name
                WeaponType.LMG, //Weapon Type
                AmmoType.LMG, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 261).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.BLOCK_NOTE_SNARE,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ENTITY_SKELETON_STEP,
                },
                Effect.VOID_FOG //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.DIAMOND_SPADE).setName(getName()).build());
        setDescription("Cover me,", "I'm going in! (Military style)");

        setSupportedAttachments(Attachment.SUPPRESSOR, Attachment.EXTENDED_MAGS, Attachment.GRIP, Attachment.SCOPE);
        setWeaponSkins(
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 2), "&6&lUrban Camo"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 5), "&e&lGreen"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 7), "&e&lSlate")
                /*new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 1), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 2), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 3), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 4), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 5), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 6), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 7), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 8), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 9), null)*/
        );

        this.walkSpeed = 0.14; //Weapon
        this.delay = 5;

        this.damage = 9.0; //RangedWeapon
        this.meleeDamage = 6.0; //RangedWeapon
        this.accuracy = 0.02; //RangedWeapon
        this.magSize = 100; //RangedWeapon
        this.reloadTime = 50; //RangedWeapon
        this.range = 60; //RangedWeapon
        this.recoil = 0.1; //RangedWeapon
        this.zoom = 5; //RangedWeapon

        this.rpm = 550; //LMGWeapon
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.SPONSOR;
    }
}