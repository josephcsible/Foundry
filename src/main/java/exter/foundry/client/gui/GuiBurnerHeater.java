package exter.foundry.client.gui;

import org.lwjgl.opengl.GL11;

import exter.foundry.Foundry;
import exter.foundry.container.ContainerBurnerHeater;
import exter.foundry.tileentity.TileEntityBurnerHeater;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBurnerHeater extends GuiFoundry
{

    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Foundry.MODID,
            "textures/gui/burnerheater.png");

    public static final int BURN_X = 80;
    public static final int BURN_Y = 17;
    public static final int BURN_WIDTH = 14;
    public static final int BURN_HEIGHT = 14;

    public static final int BURN_OVERLAY_X = 176;
    public static final int BURN_OVERLAY_Y = 0;

    private final TileEntityBurnerHeater te_bh;

    private final String STRING_MACHINE;

    public GuiBurnerHeater(TileEntityBurnerHeater bh, EntityPlayer player)
    {
        super(new ContainerBurnerHeater(bh, player), player.inventory);
        allowUserInput = false;
        ySize = 166;
        te_bh = bh;
        STRING_MACHINE = I18n.format("tile.foundry.burner_heater.name");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(GUI_TEXTURE);
        int window_x = (width - xSize) / 2;
        int window_y = (height - ySize) / 2;
        drawTexturedModalRect(window_x, window_y, 0, 0, xSize, ySize);

        if (te_bh.isBurning())
        {
            int burn = (int) (((te_bh.getSumBoost() - 0.4D) * 5 / 3) * BURN_HEIGHT);
            if (burn > 0)
            {
                drawTexturedModalRect(window_x + BURN_X, window_y + BURN_Y + BURN_HEIGHT - burn, BURN_OVERLAY_X,
                        BURN_OVERLAY_Y + BURN_HEIGHT - burn, BURN_WIDTH, burn);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
    {
        super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);

        fontRenderer.drawString(STRING_MACHINE, xSize / 2 - fontRenderer.getStringWidth(STRING_MACHINE) / 2, 6,
                0x404040);
        fontRenderer.drawString(getInventoryName(), 8, ySize - 96 + 2, 0x404040);

    }

    @Override
    protected ResourceLocation getGUITexture()
    {
        return GUI_TEXTURE;
    }
}
