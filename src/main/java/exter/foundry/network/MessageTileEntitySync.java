package exter.foundry.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import exter.foundry.Foundry;
import exter.foundry.config.FoundryConfig;
import exter.foundry.tileentity.TileEntityFoundry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageTileEntitySync implements IMessage
{
    static public class Handler implements IMessageHandler<MessageTileEntitySync, IMessage>
    {
        @Override
        public IMessage onMessage(MessageTileEntitySync message, MessageContext ctx)
        {
            IThreadListener main_thread;
            World world;
            if (ctx.side == Side.SERVER)
            {
                world = ctx.getServerHandler().player.world;
                main_thread = (WorldServer) world;
            }
            else
            {
                world = getClientWorld();
                main_thread = Minecraft.getMinecraft();
            }
            main_thread.addScheduledTask(new SyncRunnable(message.data, world));
            return null;
        }
    }

    static private class SyncRunnable implements Runnable
    {
        private final NBTTagCompound data;
        private final World world;

        public SyncRunnable(NBTTagCompound data, World world)
        {
            this.data = data;
            this.world = world;
        }

        @Override
        public void run()
        {
            int x = data.getInteger("x");
            int y = data.getInteger("y");
            int z = data.getInteger("z");
            int dim = data.getInteger("dim");
            if (dim == world.provider.getDimension())
            {
                TileEntityFoundry tile = (TileEntityFoundry) world.getTileEntity(new BlockPos(x, y, z));
                if (tile != null)
                {
                    tile.readFromNBT(data);
                    if (!world.isRemote)
                    {
                        tile.markDirty();
                    }
                }
                else if (FoundryConfig.debug)
                {
                    Foundry.LOGGER
                            .debug("TileEntitySync: Unexpected null tile at " + x + ", " + y + ", " + z + ", " + dim);
                }
            }
        }

    }

    @SideOnly(Side.CLIENT)
    static private World getClientWorld()
    {
        return Minecraft.getMinecraft().world;
    }

    NBTTagCompound data;

    public MessageTileEntitySync()
    {

    }

    public MessageTileEntitySync(NBTTagCompound data)
    {
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        try
        {
            ByteArrayInputStream s = new ByteArrayInputStream(bytes);
            data = CompressedStreamTools.readCompressed(s);
            s.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        try
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(data, stream);
            byte[] bytes = stream.toByteArray();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
