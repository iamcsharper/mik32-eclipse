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

import com.xupoh.collator.logic.PinoutCanvasController;
import com.xupoh.collator.logic.PinoutTableController;
import com.xupoh.collator.logic.PinoutTreeController;
import com.xupoh.collator.models.GlobalConstants;
import com.xupoh.collator.models.Mik32;

// add -clearPersistedState to arguments to clear workspace
public class SampleView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.xupoh.collator.parts.SampleView";

	@Inject
	public IWorkbench workbench;

	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/**
	 * View Controllers
	 */
	private PinoutTreeController treeController;
	private PinoutTableController tableController;
	private PinoutCanvasController canvasController;

	/**
	 * Views
	 */
	private PinoutTable table;
	private PinoutCanvas canvas;
	private PinoutTree tree;

	public static SampleView instance;

	@PostConstruct
	public void createPartControl(Composite parent) {
		instance = this;

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

		this.treeController = new PinoutTreeController(treePart, mik32);
		this.tree = this.treeController.getView();
		tree.setLayoutData(GlobalConstants.LAYOUT_GRID_FILL);

		/// TABLE TAB CONTENT

		// Container for table control buttons
		header = createTableControls(tablePart);

		Button button = new Button(header, SWT.PUSH);
		button.setText("Load XML data");
		button.setLayoutData(new RowData(150, 30));

		button = new Button(header, SWT.PUSH);
		button.setText("PAD config");
		button.setLayoutData(new RowData(150, 30));
		button.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				tableController.onGenPadClick();
			}

		});

		this.tableController = new PinoutTableController(tablePart, mik32);
		this.table = this.tableController.getView();
		table.getTable().setLayoutData(GlobalConstants.LAYOUT_GRID_FILL);

		/// RIGHT PART

		this.canvasController = new PinoutCanvasController(form, mik32);
		this.canvas = canvasController.getView();

		table.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				// TODO: table row being selected, canvas pin dropbox being open
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
//				System.err.println("Table selected pin #"
//						+ ((PinInfo) selection.getFirstElement()).pinId);

				tree.updateCheckboxes();
			}
		});

		table.setInput(mik32.info.getPins());

		canvas.setOnPinClickListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("Canvas clicked pin " + event.index);
				final Object obj = mik32.info.getPins().get(event.index);
				ISelection selection = new StructuredSelection(obj);
				table.setSelection(selection);
				table.reveal(obj);
			}
		});

		workbench.getHelpSystem().setHelp(table.getControl(),
				"com.xupoh.collator.parts.viewer");
		makeActions();
		hookContextMenu();
		// hookDoubleClickAction();
	}

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

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(table.getControl());
		table.getControl().setMenu(menu);
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
				IStructuredSelection selection = table.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		table.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	public void showMessage(String message) {
		MessageDialog.openInformation(table.getControl().getShell(), "Pinout viewer",
				message);
	}

	@Focus
	public void setFocus() {
		table.getControl().setFocus();
	}
}