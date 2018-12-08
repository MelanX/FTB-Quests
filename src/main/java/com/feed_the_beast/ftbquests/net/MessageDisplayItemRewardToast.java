package com.feed_the_beast.ftbquests.net;

import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.gui.tree.ToastReward;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class MessageDisplayItemRewardToast extends MessageToClient
{
	private ItemStack stack;

	public MessageDisplayItemRewardToast()
	{
	}

	public MessageDisplayItemRewardToast(ItemStack is)
	{
		stack = is;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBQuestsNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeItemStack(stack);
	}

	@Override
	public void readData(DataIn data)
	{
		stack = data.readItemStack();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		String s = stack.getDisplayName();

		if (stack.getCount() > 1)
		{
			s = stack.getCount() + "x " + s;
		}

		Minecraft.getMinecraft().getToastGui().add(new ToastReward(stack.getRarity().color + s, ItemIcon.getItemIcon(ItemHandlerHelper.copyStackWithSize(stack, 1))));
	}
}