package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;
import net.grandtheftmc.core.util.C;

import java.text.NumberFormat;
import java.util.Locale;

public class CmdMoneyBalance extends FCommand {

    private final Locale locale = Locale.US;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

    public CmdMoneyBalance() {
        super();
        this.aliases.add("b");
        this.aliases.add("bal");
        this.aliases.add("balance");

        //this.requiredArgs.add("");
        this.optionalArgs.put("cartel", "yours");

        this.permission = Permission.MONEY_BALANCE.node;
        this.setHelpShort(TL.COMMAND_MONEYBALANCE_SHORT.toString());

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = myFaction;
        if (this.argIsSet(0)) {
            faction = this.argAsFaction(0);
        }

        if (faction == null || !myFaction.isNormal()) return;
        if (faction != myFaction && !Permission.MONEY_BALANCE_ANY.has(sender, true)) return;

//        Econ.sendBalanceInfo(fme, faction);
        msg(C.GREEN + "<a>%s's<i> balance is <h>%s<i>.", faction.describeTo(fme, true), currencyFormatter.format(faction.getStash()));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYBALANCE_DESCRIPTION;
    }

}
