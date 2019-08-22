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

package stridden.enrich.networking;

import stridden.enrich.MainMod;
import stridden.enrich.items.IExtendedReach;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

// TODO: Auto-generated Javadoc
/**
 * @author jabelar
 *
 */
public class MessageExtendedReachAttack implements IMessage
{
    private int entityId;

    /**
     * Instantiates a new message extended reach attack.
     */
    public MessageExtendedReachAttack()
    {
        // need this constructor
    }

    /**
     * Instantiates a new message extended reach attack.
     *
     * @param parEntityId
     *            the par entity id
     */
    public MessageExtendedReachAttack(int parEntityId)
    {
        entityId = parEntityId;
        // DEBUG
        System.out.println("Constructor");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraftforge.fml.common.network.simpleimpl.IMessage#fromBytes(io.netty.buffer.ByteBuf)
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityId = ByteBufUtils.readVarInt(buf, 4);
        // DEBUG
        System.out.println("fromBytes");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraftforge.fml.common.network.simpleimpl.IMessage#toBytes(io.netty.buffer.ByteBuf)
     */
    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeVarInt(buf, entityId, 4);
        // DEBUG
        System.out.println("toBytes encoded");
    }

    public static class Handler implements IMessageHandler<MessageExtendedReachAttack, IMessage>
    {

        /*
         * (non-Javadoc)
         * 
         * @see net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler#onMessage(net.minecraftforge.fml.common.network.simpleimpl.IMessage,
         * net.minecraftforge.fml.common.network.simpleimpl.MessageContext)
         */
        @Override
        public IMessage onMessage(final MessageExtendedReachAttack message, MessageContext ctx)
        {
            // DEBUG
            System.out.println("Message received");
            // Know it will be on the server so make it thread-safe
            final EntityPlayerMP thePlayer = (EntityPlayerMP) MainMod.proxy.getPlayerEntityFromContext(ctx);
            thePlayer.getServer().addScheduledTask(
                    new Runnable() {
                        @Override
                        public void run()
                        {
                            Entity theEntity = thePlayer.world.getEntityByID(message.entityId);
                            // DEBUG
                            System.out.println("Entity = " + theEntity + " targeted with active item = " + thePlayer.getHeldItemMainhand());

                            // Need to ensure that hackers can't cause trick kills, so double check weapon type
                            // and reach
                            if (thePlayer.getHeldItemMainhand() == null)
                            {
                                return;
                            }
                            if (thePlayer.getHeldItemMainhand().getItem() instanceof IExtendedReach)
                            {
                                // DEBUG
                                System.out.println("Active item has extended reach");

                                IExtendedReach theExtendedReachWeapon = (IExtendedReach) thePlayer.getHeldItemMainhand().getItem();
                                double distanceSq = thePlayer.getDistanceSq(theEntity);
                                double reachSq = theExtendedReachWeapon.getReach() * theExtendedReachWeapon.getReach();
                                if (reachSq >= distanceSq)
                                {
                                    // DEBUG
                                    System.out.println("The target is within range");
                                    
                                    thePlayer.attackTargetEntityWithCurrentItem(theEntity);
                                }
                            }
                            return; // no response in this case
                        }
                    });
            return null; // no response message
        }
    }
}
