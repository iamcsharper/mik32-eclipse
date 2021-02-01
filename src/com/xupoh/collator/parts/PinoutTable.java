package com.xupoh.collator.parts;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import com.xupoh.collator.parts.common.PinInfo;

public class PinoutTable extends TableViewer {

	private Listener onPinModeChangeListener;

	public void setOnPinModeChangeListener(Listener listener) {
		onPinModeChangeListener = listener;
	}

	public PinoutTable(Composite parent) {
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.BORDER);

		final Table table = getTable();

		TableViewerColumn colId = new TableViewerColumn(this, SWT.NONE);
		TableViewerColumn colName = new TableViewerColumn(this, SWT.NONE);
		TableViewerColumn colAnalog = new TableViewerColumn(this, SWT.NONE);

		final ReusableColumnViewerComparator cSorter = new ReusableColumnViewerComparator(
				this, colId) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				PinInfo p1 = (PinInfo) e1;
				PinInfo p2 = (PinInfo) e2;
				return p1.getId() - p2.getId();
			}

		};

		new ReusableColumnViewerComparator(this, colName) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				PinInfo p1 = (PinInfo) e1;
				PinInfo p2 = (PinInfo) e2;
				return p1.getModeById(p1.selectedModeId).designation.compareToIgnoreCase(
						p2.getModeById(p2.selectedModeId).designation);
			}

		};

		new ReusableColumnViewerComparator(this, colAnalog) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				PinInfo p1 = (PinInfo) e1;
				PinInfo p2 = (PinInfo) e2;
				String a = p1.analogFunction;
				if (a == null) {
					a = "";
				}
				String b = p2.analogFunction;
				if (b == null) {
					b = "";
				}
				return a.compareToIgnoreCase(b);
			}

		};

		cSorter.setSorter(cSorter, ReusableColumnViewerComparator.ASC);

		colId.getColumn().setWidth(40);
		colId.getColumn().setText("#");
		colId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PinInfo p = (PinInfo) element;
				return String.valueOf(p.getId());
			}
		});

		colName.getColumn().setWidth(100);
		colName.getColumn().setText("Обозначение");
		colName.setEditingSupport(new EditingSupport(this) {

			@Override
			protected void setValue(Object element, Object value) {
				int modeId = ((Integer) value).intValue();
				((PinInfo) element).selectedModeId = modeId;
				getViewer().update(element, null);

				Event event = new Event();
				event.index = ((PinInfo) element).getId() - 1;
				event.keyCode = modeId;

				onPinModeChangeListener.handleEvent(event);
			}

			@Override
			protected Object getValue(Object element) {
				// We need to calculate back to the index
				return Integer.valueOf(((PinInfo) element).selectedModeId);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new ComboBoxCellEditor(table,
						((PinInfo) element).createModesStrings());
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PinInfo p = (PinInfo) element;
				return String.valueOf(p.getModeById(p.selectedModeId).designation);
			}
		});

		colAnalog.getColumn().setWidth(150);
		colAnalog.getColumn().setText("Аналог. функция");
		colAnalog.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PinInfo p = (PinInfo) element;
				return p.analogFunction;
			}
		});

		setContentProvider(ArrayContentProvider.getInstance());

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private static abstract class ReusableColumnViewerComparator
			extends ViewerComparator {

		public static final int ASC = 1;
		public static final int NONE = 0;
		public static final int DESC = -1;

		private int direction = 0;
		private TableViewerColumn column;
		private ColumnViewer viewer;

		public ReusableColumnViewerComparator(ColumnViewer viewer,
				TableViewerColumn column) {
			this.column = column;
			this.viewer = viewer;
			SelectionAdapter selectionAdapter = createSelectionAdapter();
			this.column.getColumn().addSelectionListener(selectionAdapter);
		}

		private SelectionAdapter createSelectionAdapter() {
			return new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (ReusableColumnViewerComparator.this.viewer
							.getComparator() != null) {
						if (ReusableColumnViewerComparator.this.viewer
								.getComparator() == ReusableColumnViewerComparator.this) {
							int tdirection = ReusableColumnViewerComparator.this.direction;
							if (tdirection == ASC) {
								setSorter(ReusableColumnViewerComparator.this, DESC);
							} else if (tdirection == DESC) {
								setSorter(ReusableColumnViewerComparator.this, NONE);
							}
						} else {
							setSorter(ReusableColumnViewerComparator.this, ASC);
						}
					} else {
						setSorter(ReusableColumnViewerComparator.this, ASC);
					}
				}
			};
		}

		public void setSorter(ReusableColumnViewerComparator sorter, int direction) {
			Table columnParent = column.getColumn().getParent();
			if (direction == NONE) {
				columnParent.setSortColumn(null);
				columnParent.setSortDirection(SWT.NONE);
				viewer.setComparator(null);

			} else {
				columnParent.setSortColumn(column.getColumn());
				sorter.direction = direction;
				columnParent.setSortDirection(direction == ASC ? SWT.DOWN : SWT.UP);

				if (viewer.getComparator() == sorter) {
					viewer.refresh();
				} else {
					viewer.setComparator(sorter);
				}

			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return direction * doCompare(viewer, e1, e2);
		}

		protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
	}
}
