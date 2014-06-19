package logisticspipes.modules;

import java.util.ArrayList;
import java.util.List;

import logisticspipes.api.IRoutedPowerProvider;
import logisticspipes.interfaces.IClientInformationProvider;
import logisticspipes.interfaces.IWorldProvider;
import logisticspipes.logisticspipes.IInventoryProvider;
import logisticspipes.modules.abstractmodules.LogisticsGuiModule;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.network.NewGuiHandler;
import logisticspipes.network.abstractguis.ModuleCoordinatesGuiProvider;
import logisticspipes.network.abstractguis.ModuleInHandGuiProvider;
import logisticspipes.network.guis.module.inpipe.FluidSupplierSlot;
import logisticspipes.pipefxhandlers.Particles;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.SinkReply;
import logisticspipes.utils.SinkReply.FixedPriority;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.item.ItemIdentifierInventory;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModuleFluidSupplier extends LogisticsGuiModule implements IClientInformationProvider {
	
	private final ItemIdentifierInventory _filterInventory = new ItemIdentifierInventory(9, "Requested liquids", 1);

	private IWorldProvider _world;
	IRoutedPowerProvider _power;
	
	public IInventory getFilterInventory(){
		return _filterInventory;
	}

	@Override
	public void registerHandler(IInventoryProvider invProvider, IWorldProvider world, IRoutedPowerProvider powerprovider) {
		_world = world;
		_power = powerprovider;
		_invProvider = invProvider;
	}

	private static final SinkReply _sinkReply = new SinkReply(FixedPriority.ItemSink, 0, true, false, 0, 0);
	@Override
	public SinkReply sinksItem(ItemIdentifier item, int bestPriority, int bestCustomPriority, boolean allowDefault, boolean includeInTransit) {
		if(bestPriority > _sinkReply.fixedPriority.ordinal() || (bestPriority == _sinkReply.fixedPriority.ordinal() && bestCustomPriority >= _sinkReply.customPriority)) return null;
		if (_filterInventory.containsItem(item)){
			MainProxy.sendSpawnParticlePacket(Particles.VioletParticle, getX(), getY(), getZ(), _world.getWorld(), 2);
			return _sinkReply;
		}
		return null;
	}

	@Override
	protected ModuleCoordinatesGuiProvider getPipeGuiProvider() {
		return NewGuiHandler.getGui(FluidSupplierSlot.class);
	}

	@Override
	protected ModuleInHandGuiProvider getInHandGuiProvider() {
		return null;
	}	
	@Override
	public LogisticsModule getSubModule(int slot) {return null;}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		_filterInventory.readFromNBT(nbttagcompound, "");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
    	_filterInventory.writeToNBT(nbttagcompound, "");
	}

	@Override
	public void tick() {}
	
	@Override
	public List<String> getClientInformation() {
		List<String> list = new ArrayList<String>();
		list.add("Supplied: ");
		list.add("<inventory>");
		list.add("<that>");
		return list;
	}

	@Override
	public boolean hasGenericInterests() {
		return true;
	}

	@Override
	public List<ItemIdentifier> getSpecificInterests() {
		return null;
	}

	@Override
	public boolean interestedInAttachedInventory() {
		return false;
	}

	@Override
	public boolean interestedInUndamagedID() {
		return false;
	}

	@Override
	public boolean recievePassive() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconTexture(IconRegister register) {
		return null;
	}
}
