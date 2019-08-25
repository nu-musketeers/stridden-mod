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

package stridden.enrich.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stridden.enrich.recipes.CompactorRecipes;
import stridden.enrich.slots.SlotCompactorOutput;
import stridden.enrich.slots.SlotDisplay;
import stridden.enrich.tileentities.TileEntityCompactor;
import stridden.enrich.tileentities.TileEntityOreGen;

// TODO: Auto-generated Javadoc

/**
 * @author Stridden
 *
 */
public class ContainerOreGen extends Container
{
    private final TileEntityOreGen tileOreGen;
    private final int sizeInventory;
    private int currentOreGenTime;
    private int ticksPerOre;

    /**
     * Instantiates a new container compactor.
     *
     * @param parInventoryPlayer
     *            the par inventory player
     * @param parIInventory
     *            the par I inventory
     */
    public ContainerOreGen(InventoryPlayer parInventoryPlayer, IInventory parIInventory)
    {
        tileOreGen = (TileEntityOreGen) parIInventory;
        sizeInventory = tileOreGen.getSizeInventory();

        int i;
        for(int j = 0; j < 3; j++)
        {
            for(i = 0; i < 3; i++)
            {
                addSlotToContainer(new Slot(tileOreGen, j * 3 + i, 35 + i * 18, 18 + j * 18));
            }
        }

        // PLAYER INV
        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                addSlotToContainer(new Slot(parInventoryPlayer, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            addSlotToContainer(new Slot(parInventoryPlayer, i, 8 + i * 18, 144));
        }

        // CHANCE DISPLAY
        for(i = 0; i < 3; i++)
        {
            addSlotToContainer(new SlotDisplay(tileOreGen, i + 9, 125, 18 + i * 18));
        }
    }

    /**
     * Add the given Listener to the list of Listeners. Method name is for legacy.
     *
     * @param listener
     *            the listener
     */
    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, tileOreGen);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener iContainerListener = this.listeners.get(i);

            if (currentOreGenTime != tileOreGen.getField(0))
            {
                iContainerListener.sendWindowProperty(this, 0, this.tileOreGen.getField(0));
            }

            if (ticksPerOre != tileOreGen.getField(2))
            {
                iContainerListener.sendWindowProperty(this, 1, this.tileOreGen.getField(2));
            }
        }

        currentOreGenTime = tileOreGen.getField(0); // tick compacting item so far
        ticksPerOre = tileOreGen.getField(2); // ticks per item
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.Container#updateProgressBar(int, int)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        tileOreGen.setField(id, data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.Container#canInteractWith(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return tileOreGen.isUsableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     *
     * @param playerIn
     *            the player in
     * @param slotIndex
     *            the slot index
     * @return the item stack
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex)
    {
        ItemStack itemStack1 = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemStack2 = slot.getStack();
            itemStack1 = itemStack2.copy();

            if(slotIndex < sizeInventory)
            {
                if(!mergeItemStack(itemStack2, sizeInventory, sizeInventory+36, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStack2, itemStack1);
            }
            else if(!mergeItemStack(itemStack2, sizeInventory, sizeInventory + 36, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemStack2.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemStack2.getCount() == itemStack1.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemStack2);
        }

        return itemStack1;
    }
}