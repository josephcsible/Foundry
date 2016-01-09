package exter.foundry.integration.jei;

import exter.foundry.container.ContainerAlloyFurnace;
import exter.foundry.container.ContainerInductionCrucibleFurnace;
import exter.foundry.container.ContainerMetalCaster;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class JEIFoundryPlugin implements IModPlugin
{
  public IJeiHelpers helpers;
  
  @Override
  public void register(IModRegistry registry)
  {
    registry.addRecipeCategories(
        new AlloyFurnaceJEI.Category(helpers),
        new MeltingJEI.Category(helpers),
        new CastingJEI.Category(helpers));

    registry.addRecipeHandlers(
        new AlloyFurnaceJEI.Handler(),
        new MeltingJEI.Handler(),
        new CastingJEI.Handler()
    );
    IRecipeTransferRegistry transfer_registry = registry.getRecipeTransferRegistry();

    transfer_registry.addRecipeTransferHandler(ContainerAlloyFurnace.class, "foundry.alloyfurnace",
        ContainerAlloyFurnace.SLOTS_TE,
        ContainerAlloyFurnace.SLOTS_TE_SIZE, 
        ContainerAlloyFurnace.SLOTS_INVENTORY, 36);
    transfer_registry.addRecipeTransferHandler(ContainerInductionCrucibleFurnace.class, "foundry.melting",
        ContainerInductionCrucibleFurnace.SLOTS_TE,
        ContainerInductionCrucibleFurnace.SLOTS_TE_SIZE, 
        ContainerInductionCrucibleFurnace.SLOTS_INVENTORY, 36);
    transfer_registry.addRecipeTransferHandler(ContainerMetalCaster.class, "foundry.casting",
        ContainerMetalCaster.SLOTS_TE,
        ContainerMetalCaster.SLOTS_TE_SIZE, 
        ContainerMetalCaster.SLOTS_INVENTORY, 36);

    registry.addRecipes(AlloyFurnaceJEI.getRecipes(helpers));
    registry.addRecipes(MeltingJEI.getRecipes());
    registry.addRecipes(CastingJEI.getRecipes());
  }

  @Override
  public void onJeiHelpersAvailable(IJeiHelpers helpers)
  {
    this.helpers = helpers;
  }

  @Override
  public void onItemRegistryAvailable(IItemRegistry itemRegistry)
  {
    
  }

  @Override
  public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry)
  {
    
  }
}
