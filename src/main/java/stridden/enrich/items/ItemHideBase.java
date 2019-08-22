/**
    Copyright (C) 2017 by jabelar

    This file is part of jabelar's Minecraft Forge modding examples; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
*/

package stridden.enrich.items;

import stridden.enrich.utilities.Utilities;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

// TODO: Auto-generated Javadoc
/**
 * @author jabelar
 *
 */
@SuppressWarnings("deprecation")
public class ItemHideBase extends Item
{

    /**
     * Instantiates a new item hide base.
     */
    public ItemHideBase()
    {
        super();
        setCreativeTab(CreativeTabs.MATERIALS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.item.Item#getItemStackDisplayName(net.minecraft.item.ItemStack)
     */
    @Override
    public String getItemStackDisplayName(ItemStack parItemStack)
    {
        return (Utilities.stringToRainbow(I18n.translateToLocal(getUnlocalizedNameInefficiently(parItemStack) + ".name")).trim());
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     *
     * @param parWorld
     *            the par world
     * @param parPlayer
     *            the par player
     * @param parHand
     *            the par hand
     * @return the action result
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World parWorld, EntityPlayer parPlayer, EnumHand parHand)
    {
        RayTraceResult movingObjectPosition = rayTrace(parWorld, parPlayer, false);

        if (movingObjectPosition == null)
        {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, parPlayer.getActiveItemStack());
        }
        else
        {
            if (movingObjectPosition.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockPos = movingObjectPosition.getBlockPos();

                if (!parPlayer.canPlayerEdit(blockPos.offset(movingObjectPosition.sideHit), movingObjectPosition.sideHit, parPlayer.getActiveItemStack()))
                {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, parPlayer.getActiveItemStack());
                }

                // if you want to do special stuff with interaction of item with specific blocks
                // you can do that here
                // IBlockState theBlockState = parWorld.getBlockState(blockPos);
                // Block theBlock = theBlockState.getBlock();
                // if (theBlock == BlockRegistry.TANNING_RACK)
                // {
                // // DEBUG
                // System.out.println("ItemHorseHide onRightClick() interacting with Tanning Rack");
                // parPlayer.addStat(BlockSmith.achievementTanningAHide);
                // parPlayer.addStat(StatList.getObjectUseStats(this));
                // return new ActionResult(EnumActionResult.SUCCESS, exchangeItemStack(parPlayer.getActiveItemStack(), parPlayer, Items.LEATHER));
                // }
            }

            return new ActionResult<ItemStack>(EnumActionResult.FAIL, parPlayer.getActiveItemStack());
        }
    }
}
