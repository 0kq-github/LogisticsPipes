/** 
 * Copyright (c) Krapht, 2011
 * 
 * "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package logisticspipes.routing;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import logisticspipes.interfaces.ILogisticsModule;
import logisticspipes.interfaces.routing.IPowerRouter;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.Position;

public interface IRouter extends IPowerRouter {
	public interface IRAction {
		public boolean isInteresting(IRouter that);
		public boolean doTo(IRouter that);
		public void doneWith(IRouter that);
	}
	public void destroy();
	public void update(boolean fullRefresh);
	public void sendRoutedItem(ItemStack item, IRouter destination, Position origin);
	public void updateInterests(); // calls getInterests on the attached pipe, and updates the global cache.
	
	public boolean isRoutedExit(ForgeDirection connection);
	public boolean hasRoute(int id);
	public ForgeDirection getExitFor(int id);
	
	public ArrayList<ExitRoute> getRouteTable();
	public List<ExitRoute> getIRoutersByCost();
	public CoreRoutedPipe getPipe();
	public CoreRoutedPipe getCachedPipe();
	public boolean isAt(int dimension, int xCoord, int yCoord, int zCoord);
	public UUID getId();
	@Deprecated
	public void displayRoutes();
	@Deprecated
	public void displayRouteTo(int r);
	public void inboundItemArrived(RoutedEntityItem routedEntityItem);
	
	public ILogisticsModule getLogisticsModule();
	public void clearPipeCache();
	
	public IRouter getRouter(ForgeDirection insertOrientation);
	public int getSimpleID();

	public boolean act(BitSet hasBeenProcessed, IRAction actor);
	public void flagForRoutingUpdate();
	public boolean checkAdjacentUpdate();
	public void clearPrevAdjacent();
	
	/* Automated Disconnection */
	public boolean isSideDisconneceted(ForgeDirection dir);
	public ExitRoute getDistanceTo(IRouter r);
}
