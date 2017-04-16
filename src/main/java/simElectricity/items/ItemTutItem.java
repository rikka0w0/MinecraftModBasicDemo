package simElectricity.items;

import simElectricity.utils.SEItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTutItem extends SEItem {
	
	public ItemTutItem(){
		super("tut_item", false);
	}
	
	@Override
	public void beforeRegister() {
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		setMaxStackSize(1);
		this.setMaxDamage(255);
	}
	
	@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {	
		ItemStack itemStack = player.getHeldItem(hand);
		itemStack.attemptDamageItem(1, player.getRNG());
		if (!player.world.isRemote)
		player.world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 50, true);
        return EnumActionResult.SUCCESS;
    }
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        return new ActionResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }
}
