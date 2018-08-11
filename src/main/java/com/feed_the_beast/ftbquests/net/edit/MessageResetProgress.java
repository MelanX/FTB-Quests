package com.feed_the_beast.ftbquests.net.edit;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.FTBQuests;
import com.feed_the_beast.ftbquests.quest.ProgressingQuestObject;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.util.FTBQuestsTeamData;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageResetProgress extends MessageToServer
{
	private String id;
	private boolean all;

	public MessageResetProgress()
	{
	}

	public MessageResetProgress(String i, boolean a)
	{
		id = i;
		all = a;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBQuestsEditNetHandler.EDIT;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeString(id);
		data.writeBoolean(all);
	}

	@Override
	public void readData(DataIn data)
	{
		id = data.readString();
		all = data.readBoolean();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (id.isEmpty() && FTBQuests.canEdit(player))
		{
			QuestObject object = ServerQuestFile.INSTANCE.get(id);

			if (object instanceof ProgressingQuestObject)
			{
				ServerQuestFile.INSTANCE.shouldSendUpdates = false;
				ProgressingQuestObject o = (ProgressingQuestObject) object;

				if (all)
				{
					for (ForgeTeam team : Universe.get().getTeams())
					{
						o.resetProgress(FTBQuestsTeamData.get(team));
						team.markDirty();
					}

					new MessageResetProgressResponse(id).sendToAll();
				}
				else
				{
					ForgePlayer player1 = Universe.get().getPlayer(player);

					if (player1.team.isValid())
					{
						o.resetProgress(FTBQuestsTeamData.get(player1.team));
						player1.team.markDirty();
					}

					new MessageResetProgressResponse(id).sendTo(player);
				}

				Universe.get().clearCache();
				ServerQuestFile.INSTANCE.shouldSendUpdates = true;
			}
		}
	}
}