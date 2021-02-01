package com.xupoh.collator.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.xupoh.collator.parts.common.GlobalConstants;
import com.xupoh.collator.parts.common.PinInfo;
import com.xupoh.collator.parts.housings.Mik32;

// add -clearPersistedState to arguments to clear workspace
public class SampleView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.xupoh.collator.parts.SampleView";

	@Inject
	IWorkbench workbench;

	private PinoutTable viewer;

	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	private PinoutCanvas partDrawer;

	/**
	 * Creates a composite with single-column grid layout
	 * 
	 * @param parent composite to be attached to
	 * @return
	 */
	private Composite createLeftPartGridded(Composite parent) {
		Composite leftPart = new Composite(parent, SWT.None);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 1;
		gridLayout.marginWidth = 1;
		gridLayout.verticalSpacing = 5;
		leftPart.setLayout(gridLayout);

		return leftPart;
	}

	/**
	 * Creates a composite with row layout
	 * 
	 * @param parent composite to be attached to
	 * @return
	 */
	private Composite createTableControls(Composite parent) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 35;

		Composite header = new Composite(parent, SWT.None);
		header.setLayout(new RowLayout(SWT.HORIZONTAL));
		header.setLayoutData(gridData);

		return header;
	}

	@PostConstruct
	public void createPartControl(Composite parent) {
		final Mik32 mik32 = new Mik32();

		// Sash divides left and right parts (table&tree and canvas)
		SashForm form = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
		form.SASH_WIDTH = 5;
		form.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		// Container for all stuff located in left
		Composite left = new Composite(form, SWT.None);
		left.setLayout(new FillLayout(SWT.VERTICAL));

		// TabFolder contains tab navigation buttons and content
		final TabFolder tableTreeSwitcher = new TabFolder(left, SWT.None);

		// Tab content containers
		Composite tablePart = createLeftPartGridded(tableTreeSwitcher);
		Composite treePart = createLeftPartGridded(tableTreeSwitcher);

		// A navigation button to switch content to table view
		TabItem item = new TabItem(tableTreeSwitcher, SWT.None);
		item.setText("Table view");
		item.setControl(tablePart);

		// A navigation button to switch content to tree view
		item = new TabItem(tableTreeSwitcher, SWT.None);
		item.setText("Tree view");
		item.setControl(treePart);

		/// TREE TAB CONTENT

		// Container for tree controls
		Composite header = createTableControls(treePart);

		Button btn = new Button(header, SWT.PUSH);
		btn.setText("Test tree");

		final PinoutTree tree = new PinoutTree(treePart, mik32);
		tree.setLayoutData(GlobalConstants.LAYOUT_GRID_FILL);

		/// TABLE TAB CONTENT

		// Container for table control buttons
		header = createTableControls(tablePart);

		Button button = new Button(header, SWT.PUSH);
		button.setText("Load XML data");
		button.setLayoutData(new RowData(150, 30));

		button = new Button(header, SWT.PUSH);
		button.setText("Gen. PAD config");
		button.setLayoutData(new RowData(150, 30));
		button.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				final int a = mik32.generatePort(0);
				final int b = mik32.generatePort(1);
				final int c = mik32.generatePort(2);
				// System.out.println("Generated results: ");

				StringBuilder sb = new StringBuilder();
				sb.append("PortA = ");
				sb.append(Integer.toHexString(a));
				sb.append("\nPortB = ");
				sb.append(Integer.toHexString(b));
				sb.append("\nPortC = ");
				sb.append(Integer.toHexString(c));

				showMessage(sb.toString());
			}

		});

		viewer = new PinoutTable(tablePart);
		viewer.getTable().setLayoutData(GlobalConstants.LAYOUT_GRID_FILL);

		/// RIGHT PART
		partDrawer = new PinoutCanvas(form);

		viewer.setOnPinModeChangeListener(new Listener() {

			@Override
			public void handleEvent(Event event) {
				// System.err.println("Table changed pin #" + event.index + " state");
				partDrawer.updatePinModeView(event.index, event.keyCode);
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				// TODO: table row being selected, canvas pin dropbox being open
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
//				System.err.println("Table selected pin #"
//						+ ((PinInfo) selection.getFirstElement()).pinId);
				
				tree.updateCheckboxes();
			}
		});

		viewer.setInput(mik32.getPins());

		partDrawer.setHousing(mik32);
		partDrawer.setOnPinClickListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				// System.out.println("Canvas clicked pin " + event.index);
				final Object obj = mik32.getPin(event.index);
				ISelection selection = new StructuredSelection(obj);
				viewer.setSelection(selection);
				viewer.reveal(obj);
			}
		});

		partDrawer.setOnPinModeChangeListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("Canvas PinMode changed for pin #" + (event.index + 1)
						+ " to = " + event.keyCode);

				mik32.getPin(event.index).selectedModeId = event.keyCode;

				viewer.refresh(true);
				tree.updateCheckboxes();
			}
		});

		workbench.getHelpSystem().setHelp(viewer.getControl(),
				"com.xupoh.collator.parts.viewer");
		makeActions();
		hookContextMenu();
		// hookDoubleClickAction();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		action1 = new Action() {
			@Override
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(workbench.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			@Override
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(workbench.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Pinout viewer",
				message);
	}

	@Focus
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}