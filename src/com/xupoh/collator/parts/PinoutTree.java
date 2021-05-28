package com.xupoh.collator.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.xupoh.collator.models.Mik32;
import com.xupoh.collator.models.Periphery;
import com.xupoh.collator.models.PinInfo;
import com.xupoh.collator.models.PinModeInfo;
import com.xupoh.collator.services.PeripheryConfigurationService;

public class PinoutTree extends Tree {

	@Override
	protected void checkSubclass() {

	}

	private Mik32 mik32;

	private Map<String, Periphery> rootKeysToPeripherals = new HashMap<String, Periphery>();

	public PinoutTree(Composite parent, Mik32 mik32) {
		super(parent, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL);

		this.mik32 = mik32;

		mik32.addOnPinModeUpdateListener((int id, int mode, PinInfo info) -> {
			this.updateCheckboxes();
		});

		buildTree();
		makeActions();
		hookContextMenu();
	}

	private void buildTree() {
		SortedSet<Periphery> keys = new TreeSet<>(
				mik32.info.getPeripheralsToPins().keySet());

		for (Periphery key : keys) {
			final List<PinInfo> pins = mik32.info.getPeripheralsToPins().get(key);
			final String keyStr = key.toString();

			rootKeysToPeripherals.put(keyStr, key);

			TreeItem iItem = new TreeItem(this, SWT.CHECK);
			iItem.setText(keyStr);

			for (int i = 0; i < pins.size(); i++) {
				final PinInfo pin = pins.get(i);
				final PinModeInfo info = pin.findModeForPeriphery(key);

				if (info == null) {
					System.out.println(
							"Null info found for pin #" + pin.getId() + " mode " + key);
					continue;
				}

				final TreeItem jItem = new TreeItem(iItem, SWT.CHECK);
				jItem.setText("Pin #" + pin.getId() + " - " + info.designation);
				final boolean checked = info.id == pin.getSelectedModeId();
				if (checked) {
					iItem.setChecked(true);
				}

				jItem.setChecked(checked);
			}
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				final Periphery p = getSelectedPeriphery();

				if (p == null)
					return;

				System.out.println("Rendering " + p);

				PinoutTree.this.renderContextMenu(p, manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(this);
		this.setMenu(menu);
	}

	private Action minSlaveConfig, minMasterConfig;
	
	private Action notImplemetedYet;

	private void makeActions() {
		notImplemetedYet = new Action() {
			@Override
			public void run() {
				System.err.println("Not implemented yet");
			}
		};
		notImplemetedYet.setText("Not implemeted yet");
		notImplemetedYet.setImageDescriptor(SampleView.instance.workbench.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR));
		
		minSlaveConfig = new Action() {
			@Override
			public void run() {
				System.out.println("Clicked Min slave config!");
				
				PeripheryConfigurationService.instance
				.tryConfigure(PinoutTree.this.mik32.info, getSelectedPeriphery());
			}
		};
		minSlaveConfig.setText("Min SLAVE mode");
		minSlaveConfig.setToolTipText("Мин. конфигурация - SLAVE");
		minSlaveConfig.setImageDescriptor(SampleView.instance.workbench.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_DND_STACK_MASK));
		
		minMasterConfig = new Action() {
			@Override
			public void run() {
				System.out.println("Clicked Min MASTER config!");
			}
		};
		minMasterConfig .setText("Min MASTER mode");
		minMasterConfig .setToolTipText("Мин. конфигурация - MASTER");
		minMasterConfig .setImageDescriptor(SampleView.instance.workbench.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_DND_STACK_MASK));
	}

	protected void renderContextMenu(Periphery periphery, IMenuManager manager) {
		switch (periphery.getType()) {
		case I2C:
			break;
		case SPI:
			manager.add(minSlaveConfig);
			manager.add(minMasterConfig);
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			break;
		case TDI:
			break;
		case Timer16:
			break;
		case Timer32:
			break;
		case UART:
			manager.add(notImplemetedYet);
			break;

		default:
			break;
		}
	}

	protected Periphery getSelectedPeriphery() {
		if (this.getSelectionCount() < 1)
			return null;

		return this.rootKeysToPeripherals.get(this.getSelection()[0].getText());
	}

	public void updateCheckboxes() {
		System.out.println("Re-render the whole tree to view checkboxes");
//		int i = 0;
//		for (Entry<Periphery, List<PinInfo>> entry : mik32.info.getPeripheralsToPins()
//				.entrySet()) {
//			final Periphery key = entry.getKey();
//			final List<PinInfo> pins = entry.getValue();
//
//			final TreeItem periphItem = this.getItem(i);
//
//			boolean isOneChecked = false;
//
//			for (int j = 0; j < pins.size(); j++) {
//				final PinInfo pin = pins.get(j);
//				final PinModeInfo info = pin.findModeForPeriphery(key);
//
//				final TreeItem pinItem = periphItem.getItem(j);
//
//				final boolean checked = info.id == pin.getSelectedModeId();
//
//				if (checked) {
//					isOneChecked = true;
//				}
//
//				pinItem.setChecked(checked);
//			}
//
//			periphItem.setChecked(isOneChecked);
//
//			i++;
//		}
	}
}
