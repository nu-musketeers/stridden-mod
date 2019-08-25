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

package stridden.enrich.tileentities;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import stridden.enrich.MainMod;
import stridden.enrich.containers.ContainerOreGen;

import java.util.*;

// TODO: Auto-generated Javadoc

/**
 * @author Stridden
 *
 */
public class TileEntityOreGen extends TileEntityLockable implements ITickable, ISidedInventory
{
    public static final ArrayList<ItemStack> ORES;
    static {
        ORES = new ArrayList<>();
        ORES.add(new ItemStack(Blocks.COAL_ORE));
        ORES.add(new ItemStack(Blocks.IRON_ORE));
        ORES.add(new ItemStack(Blocks.GOLD_ORE));
        ORES.add(new ItemStack(Blocks.DIAMOND_ORE));
        ORES.add(new ItemStack(Blocks.EMERALD_ORE));
        ORES.add(new ItemStack(Blocks.LAPIS_ORE));
        ORES.add(new ItemStack(Blocks.QUARTZ_ORE));
        ORES.add(new ItemStack(Blocks.REDSTONE_ORE));
    }

    protected final HashMap<ItemStack, Integer> ORE_CHANCES = new HashMap<>();
    protected final ArrayList<ItemStack> NEARBY_ORES = new ArrayList<>();

    protected static final int[] slotsTop = new int[] { };
    protected static final int[] slotsBottom = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
    protected static final int[] slotsSides = new int[] { };

    protected NonNullList<ItemStack> genItemStacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
    protected NonNullList<ItemStack> displayItemStacks = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);

    protected int currentOreGenTime;
    protected int oresGenerated;
    protected int ticksPerOre = 2400;
    protected ItemStack currentOre;

    protected IItemHandler handlerTop = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.UP);
    protected IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.DOWN);
    protected IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, EnumFacing.WEST);

    /**
     * Returns the number of slots in the inventory.
     *
     * @return the size inventory
     */
    @Override
    public int getSizeInventory()
    {
        return genItemStacks.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        {
            for (ItemStack itemstack : genItemStacks)
            {
                if (!itemstack.isEmpty())
                {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Returns the stack in slot i.
     *
     * @param index
     *            the index
     * @return the stack in slot
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 9)
        {
            return genItemStacks.get(index);
        }
        if(index-9 >= ORE_CHANCES.size())
        {
            return ItemStack.EMPTY;
        }
        ItemStack[] chances = new ItemStack[ORE_CHANCES.size()];
        chances = ORE_CHANCES.keySet().toArray(chances);
        return chances[index-9];
        // TODO: Get these things to show
    }

    public int getChanceInSlot(int index) {
        return getChanceFromItemStack(getStackInSlot(index));
    }

    public int getChanceFromItemStack(ItemStack i)
    {
        if(i == ItemStack.EMPTY)
        {
            return 0;
        }
        return ORE_CHANCES.get(i);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
     *
     * @param index
     *            the index
     * @param count
     *            the count
     * @return the item stack
     */
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(this.genItemStacks, index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     *
     * @param index
     *            the index
     * @return the item stack
     */
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.genItemStacks, index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     *
     * @param index
     *            the index
     * @param stack
     *            the stack
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        if(index >= 9)
        {
            displayItemStacks.set(index, stack);
            displayItemStacks.get(index).setCount(1);
        }
        genItemStacks.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
        {
            stack.setCount(getInventoryStackLimit());
        }

        markDirty();
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon").
     *
     * @return the name
     */
    @Override
    public String getName()
    {
        return "container.ore_gen";
    }

    /**
     * Returns true if this thing is named.
     *
     * @return true, if successful
     */
    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.tileentity.TileEntityLockable#readFromNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        genItemStacks = NonNullList.<ItemStack> withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, genItemStacks);

        currentOreGenTime = compound.getShort("GenTime");
        oresGenerated = compound.getShort("TotalOres");
        ticksPerOre = timeToGenOneOre();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.tileentity.TileEntityLockable#writeToNBT(net.minecraft.nbt.NBTTagCompound)
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setShort("GenTime", (short) currentOreGenTime);
        compound.setShort("TotalOres", (short) oresGenerated);
        ItemStackHelper.saveAllItems(compound, genItemStacks);

        return compound;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't this more of a set than a get?*
     *
     * @return the inventory stack limit
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.util.ITickable#update()
     */
    @Override
    public void update()
    {
        // // DEBUG
        // System.out.println("update() in TileEntityCompactor");

        if (!world.isRemote)
        {
            if(currentOreGenTime < ticksPerOre)
            {
                ++currentOreGenTime;
            }
            else
            {
                if(currentOre == null)
                {
                    currentOre = pickOre();
                }
                if(generateOre())
                {
                    currentOreGenTime = 0;
                    ticksPerOre = timeToGenOneOre();
                    currentOre = pickOre();
                }
            }
        }
    }

    protected void reloadNearbyOres()
    {
        HashSet<ItemStack> ores = new HashSet<>();
        for(int y = -7; y <= 7; y++)
        {
            for(int z = -7; z <= 7; z++)
            {
                for(int x = -7; x <= 7; x++)
                {
                    for(ItemStack ore : ORES)
                    {
                        if(Item.getItemFromBlock(world.getBlockState(pos.add(x, y, z)).getBlock()) == ore.getItem())
                        {
                            ores.add(ore);
                            ORE_CHANCES.putIfAbsent(ore, 0);
                            System.out.println(ore.getItem().getUnlocalizedName() + " " + ORE_CHANCES.get(ore) + "%");
                        }
                    }
                }
            }
        }
        NEARBY_ORES.clear();
        NEARBY_ORES.addAll(ores);
    }

    protected ItemStack pickOre()
    {
        reloadNearbyOres();
        int size = NEARBY_ORES.size();

        if(size <= 0)
        {
            return null;
        }

        Random r = new Random();
        return NEARBY_ORES.get(r.nextInt(size));
    }

    /**
     * Time to generate one ore.
     *
     * @return the number of ticks to generate
     */
    public int timeToGenOneOre()
    {
        int max = 64;
        return 2400 - (int)((float)(oresGenerated < max ? oresGenerated : max) / (float)max * (float)2280);
    }

    public boolean generateOre()
    {
        if(currentOre == null)
        {
            return false;
        }

        boolean didGenerate = false;
        Random r = new Random();
        int currentChance = ORE_CHANCES.get(currentOre);

        if(r.nextInt(100) < currentChance)
        {
            for(ItemStack slot : genItemStacks)
            {
                if(slot == ItemStack.EMPTY || (slot.getItem() == currentOre.getItem() && slot.getCount() < getInventoryStackLimit()))
                {
                    ORE_CHANCES.put(currentOre, 0);
                    ++oresGenerated;
                    didGenerate = true;
                }

                if (slot == ItemStack.EMPTY)
                {
                    genItemStacks.set(genItemStacks.indexOf(slot), currentOre.copy());
                    break;
                }
                else if (slot.getItem() == currentOre.getItem() && slot.getCount() < getInventoryStackLimit())
                {
                    slot.setCount(slot.getCount() + 1);
                    break;
                }
            }
        }
        else
        {
            didGenerate = true;
            ORE_CHANCES.put(currentOre, currentChance + 1);
        }
        return didGenerate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#openInventory(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public void openInventory(EntityPlayer playerIn)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#closeInventory(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public void closeInventory(EntityPlayer playerIn)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#isItemValidForSlot(int, net.minecraft.item.ItemStack)
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.ISidedInventory#getSlotsForFace(net.minecraft.util.EnumFacing)
     */
    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item, side
     *
     * @param index
     *            the index
     * @param itemStackIn
     *            the item stack in
     * @param direction
     *            the direction
     * @return true, if successful
     */
    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return false;
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item, side
     *
     * @param parSlotIndex
     *            the par slot index
     * @param parStack
     *            the par stack
     * @param parFacing
     *            the par facing
     * @return true, if successful
     */
    @Override
    public boolean canExtractItem(int parSlotIndex, ItemStack parStack, EnumFacing parFacing)
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.world.IInteractionObject#getGuiID()
     */
    @Override
    public String getGuiID()
    {
        return MainMod.MODID + ":ore_gen";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.world.IInteractionObject#createContainer(net.minecraft.entity.player.InventoryPlayer, net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerOreGen(playerInventory, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#getField(int)
     */
    @Override
    public int getField(int id)
    {
        switch (id)
        {
        case 0:
            return currentOreGenTime;
        case 1:
            return oresGenerated;
        case 2:
            return ticksPerOre;
        default:
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#setField(int, int)
     */
    @Override
    public void setField(int id, int value)
    {
        switch (id)
        {
        case 0:
            currentOreGenTime = value;
            break;
        case 1:
            oresGenerated = value;
            break;
        case 2:
            ticksPerOre = value;
            break;
        default:
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#getFieldCount()
     */
    @Override
    public int getFieldCount()
    {
        return 3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#clear()
     */
    @Override
    public void clear()
    {
        genItemStacks.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.tileentity.TileEntityLockable#getCapability(net.minecraftforge.common.capabilities.Capability, net.minecraft.util.EnumFacing)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.inventory.IInventory#isUsableByPlayer(net.minecraft.entity.player.EntityPlayer)
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

}