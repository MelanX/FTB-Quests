package com.feed_the_beast.ftbquests.client;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.OtherMods;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.IOpenableGui;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiSelectItemStack;
import com.feed_the_beast.ftbquests.FTBQuestsCommon;
import com.feed_the_beast.ftbquests.net.edit.MessageAddReward;
import com.feed_the_beast.ftbquests.net.edit.MessageCreateObject;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestFile;
import com.feed_the_beast.ftbquests.quest.QuestObjectType;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.reward.FTBQuestsRewards;
import com.feed_the_beast.ftbquests.quest.reward.ItemReward;
import com.feed_the_beast.ftbquests.quest.reward.QuestRewardType;
import com.feed_the_beast.ftbquests.quest.task.FTBQuestsTasks;
import com.feed_the_beast.ftbquests.quest.task.ItemTask;
import com.feed_the_beast.ftbquests.quest.task.QuestTaskType;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.lwjgl.input.Keyboard;

public class FTBQuestsClient extends FTBQuestsCommon
{
	public static KeyBinding KEY_QUESTS;

	@Override
	public void preInit()
	{
		KEY_QUESTS = new KeyBinding("key.ftbquests.quests", KeyConflictContext.IN_GAME, Loader.isModLoaded(OtherMods.FTBGUIDES) ? KeyModifier.CONTROL : KeyModifier.NONE, Keyboard.KEY_G, FTBLib.KEY_CATEGORY);
		ClientRegistry.registerKeyBinding(KEY_QUESTS);
	}

	@Override
	public QuestFile getQuestFile(boolean clientSide)
	{
		return clientSide ? ClientQuestFile.INSTANCE : ServerQuestFile.INSTANCE;
	}

	@Override
	public void setTaskGuiProviders()
	{
		FTBQuestsTasks.ITEM.setGuiProvider(new QuestTaskType.GuiProvider()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public void openCreationGui(IOpenableGui gui, Quest quest)
			{
				new GuiSelectItemStack(gui, stack -> {
					if (!stack.isEmpty())
					{
						ItemTask itemTask = new ItemTask(quest);
						itemTask.items.add(ItemHandlerHelper.copyStackWithSize(stack, 1));
						itemTask.count = stack.getCount();
						NBTTagCompound nbt = new NBTTagCompound();
						itemTask.writeData(nbt);
						new MessageCreateObject(QuestObjectType.TASK, quest.uid, nbt).sendToServer();
					}
				}).openGui();
			}
		});
	}

	@Override
	public void setRewardGuiProviders()
	{
		FTBQuestsRewards.ITEM.setGuiProvider(new QuestRewardType.GuiProvider()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public void openCreationGui(IOpenableGui gui, Quest quest)
			{
				GuiHelper.playClickSound();
				new GuiSelectItemStack(gui, stack -> {
					if (!stack.isEmpty())
					{
						ItemReward reward = new ItemReward(quest);
						reward.stack = stack;
						NBTTagCompound nbt = new NBTTagCompound();
						reward.writeData(nbt);
						new MessageAddReward(quest.uid, nbt).sendToServer();
					}
				}).openGui();
			}
		});
	}
}