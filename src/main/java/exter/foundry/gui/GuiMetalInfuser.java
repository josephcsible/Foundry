package exter.foundry.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import exter.foundry.api.FoundryAPI;
import exter.foundry.api.substance.ISubstanceGuiTexture;
import exter.foundry.api.substance.InfuserSubstance;
import exter.foundry.container.ContainerMetalInfuser;
import exter.foundry.recipes.SubstanceGuiTexture;
import exter.foundry.recipes.manager.InfuserRecipeManager;
import exter.foundry.tileentity.TileEntityMetalInfuser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiMetalInfuser extends GuiFoundry
{

  private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("foundry:textures/gui/infuser.png");

  public static final int TANK_HEIGHT = 47;
  private static final int INPUT_TANK_X = 85;
  private static final int INPUT_TANK_Y = 43;

  private static final int OUTPUT_TANK_X = 134;
  private static final int OUTPUT_TANK_Y = 43;

  private static final int SUBSTANCE_X = 71;
  private static final int SUBSTANCE_Y = 43;
  private static final int SUBSTANCE_HEIGHT = 47;

  private static final int PROGRESS_X = 42;
  private static final int PROGRESS_Y = 59;
  private static final int PROGRESS_WIDTH = 22;
  private static final int PROGRESS_HEIGHT = 15;
  
  private static final int TANK_OVERLAY_X = 176;
  private static final int TANK_OVERLAY_Y = 0;

  private static final int PROGRESS_OVERLAY_X = 176;
  private static final int PROGRESS_OVERLAY_Y = 53;

  private TileEntityMetalInfuser te_infuser;

  public GuiMetalInfuser(TileEntityMetalInfuser inf, EntityPlayer player)
  {
    super(new ContainerMetalInfuser(inf, player));
    allowUserInput = false;
    ySize = 209;
    te_infuser = inf;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
  {
    super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);

    fontRendererObj.drawString("Metal Infuser", 5, 6, 0x404040);
    fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
  {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    mc.renderEngine.bindTexture(GUI_TEXTURE);
    int window_x = (width - xSize) / 2;
    int window_y = (height - ySize) / 2;
    drawTexturedModalRect(window_x, window_y, 0, 0, xSize, ySize);

    //Draw progress bar.
    int progress = te_infuser.getProgress() * PROGRESS_WIDTH / te_infuser.getExtractTime();
    if(progress > 0)
    {
      drawTexturedModalRect(window_x + PROGRESS_X, window_y + PROGRESS_Y, PROGRESS_OVERLAY_X, PROGRESS_OVERLAY_Y, progress, PROGRESS_HEIGHT);
    }
    
    displayTank(window_x, window_y, INPUT_TANK_X, INPUT_TANK_Y, TANK_HEIGHT, TANK_OVERLAY_X, TANK_OVERLAY_Y, te_infuser.getTank(TileEntityMetalInfuser.TANK_INPUT));
    displayTank(window_x, window_y, OUTPUT_TANK_X, OUTPUT_TANK_Y, TANK_HEIGHT, TANK_OVERLAY_X, TANK_OVERLAY_Y, te_infuser.getTank(TileEntityMetalInfuser.TANK_OUTPUT));
    
    //Draw substance bar.
    InfuserSubstance sub = te_infuser.getSubstance();
    if(sub != null && sub.amount > 0)
    {
      ISubstanceGuiTexture tex = InfuserRecipeManager.instance.GetSubstanceTexture(sub.type);
      if(tex != null)
      {
        mc.renderEngine.bindTexture(tex.getLocation());
        int height = sub.amount * TANK_HEIGHT / FoundryAPI.INFUSER_SUBSTANCE_AMOUNT_MAX;

        setGLColor(tex.getColor());
        drawTexturedModalRect(window_x + SUBSTANCE_X, window_y + SUBSTANCE_Y + SUBSTANCE_HEIGHT - height, tex.getX(), tex.getY() + SUBSTANCE_HEIGHT - height, SubstanceGuiTexture.TEXTURE_WIDTH, height);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(getGUITexture());
      }
    }
  }

  @Override
  public void drawScreen(int mousex, int mousey, float par3)
  {
    super.drawScreen(mousex, mousey, par3);
    if(isPointInRegion(INPUT_TANK_X,INPUT_TANK_Y,16,TANK_HEIGHT,mousex,mousey))
    {
      List<String> currenttip = new ArrayList<String>();
      addTankTooltip(currenttip, mousex, mousey, te_infuser.getTank(TileEntityMetalInfuser.TANK_INPUT));
      drawHoveringText(currenttip, mousex, mousey, fontRendererObj);
    }

    if(isPointInRegion(OUTPUT_TANK_X,OUTPUT_TANK_Y,16,TANK_HEIGHT,mousex,mousey))
    {
      List<String> currenttip = new ArrayList<String>();
      addTankTooltip(currenttip, mousex, mousey, te_infuser.getTank(TileEntityMetalInfuser.TANK_OUTPUT));
      drawHoveringText(currenttip, mousex, mousey, fontRendererObj);
    }

    if(isPointInRegion(SUBSTANCE_X, SUBSTANCE_Y, SubstanceGuiTexture.TEXTURE_WIDTH, SUBSTANCE_HEIGHT, mousex, mousey))
    {
      List<String> currenttip = new ArrayList<String>();
      InfuserSubstance sub = te_infuser.getSubstance();
      if(sub != null && sub.amount > 0)
      {
        currenttip.add(StatCollector.translateToLocal("substance." + sub.type));
        currenttip.add(String.valueOf(sub.amount) + " / " + String.valueOf(FoundryAPI.INFUSER_SUBSTANCE_AMOUNT_MAX) + " mL");
      } else
      {
        currenttip.add("0 / " + String.valueOf(FoundryAPI.INFUSER_SUBSTANCE_AMOUNT_MAX) + " mL");
      }
      drawHoveringText(currenttip, mousex, mousey, fontRendererObj);
    }
  }

  @Override
  protected ResourceLocation getGUITexture()
  {
    return GUI_TEXTURE;
  }
}
