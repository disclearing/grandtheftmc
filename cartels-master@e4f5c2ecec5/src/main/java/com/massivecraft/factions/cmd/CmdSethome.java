package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.eco.EcoResult;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import net.grandtheftmc.core.util.NumeralUtil;

import java.util.Locale;

public class CmdSethome extends FCommand {

    public CmdSethome() {
        this.aliases.add("sethome");

        //this.requiredArgs.add("");
        this.optionalArgs.put("cartel tag", "mine");

        this.permission = Permission.SETHOME.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!Conf.homesEnabled) {
            fme.msg(TL.COMMAND_SETHOME_DISABLED);
            return;
        }

        Faction faction = this.argAsFaction(0, myFaction);
        if (faction == null) {
            return;
        }

        if (me.getWorld().getName().equals("spawn")) {
            return;
        }

        // Can the player set the home for this faction?
        if (faction == myFaction) {
            if (!Permission.SETHOME_ANY.has(sender) && !assertMinRole(Role.MODERATOR)) {
                return;
            }
        } else {
            if (!Permission.SETHOME_ANY.has(sender, true)) {
                return;
            }
        }

        // Can the player set the faction home HERE?
        if (!Permission.BYPASS.has(me) &&
                    Conf.homesMustBeInClaimedTerritory &&
                    Board.getInstance().getFactionAt(new FLocation(me)) != faction) {
            fme.msg(TL.COMMAND_SETHOME_NOTCLAIMED);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
//        if (!payForCommand(Conf.econCostSethome, TL.COMMAND_SETHOME_TOSET, TL.COMMAND_SETHOME_FORSET)) {
//            return;//msg("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis)
//        }

        if(Conf.econCostSethome > 0) {
            EcoResult result = faction.takeFromStash(Conf.econCostSethome);
            if(result == EcoResult.LOW_FUNDS) {
                fme.msg("<h>%s<i> can't afford <h>%s<i> %s.", faction.describeTo(faction, true), NumeralUtil.toCurrency(Conf.econCostSethome, Locale.US), TL.COMMAND_SETHOME_TOSET);
                return;
            }

            if(result != EcoResult.SUCCESS) return;
        }

        faction.setHome(me.getLocation());

        faction.msg(TL.COMMAND_SETHOME_SET, fme.describeTo(myFaction, true));
        faction.sendMessage(p.cmdBase.cmdHome.getUseageTemplate());
        if (faction != myFaction) {
            fme.msg(TL.COMMAND_SETHOME_SETOTHER, faction.getTag(fme));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETHOME_DESCRIPTION;
    }

}
