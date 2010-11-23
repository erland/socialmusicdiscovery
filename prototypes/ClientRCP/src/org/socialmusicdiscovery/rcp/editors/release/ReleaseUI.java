package org.socialmusicdiscovery.rcp.editors.release;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;
import org.socialmusicdiscovery.server.business.model.core.Release;

public class ReleaseUI extends AbstractComposite<Release> {
	private DataBindingContext m_bindingContext;
	private Text textName;
	private Release release;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm scrldfrmRelease;
	protected Text text;
	protected Section metaData;
	protected Composite metadataContainer;
	protected Label labelLabel;
	protected Label labelDate;
	protected Combo comboLabel;
	private ComboViewer comboViewer;
	protected CDateTime dateTime;
	protected CTabFolder tabFolder;
	protected CTabItem tbtmMedia;
	protected CTabItem tbtmContributors;
	protected Grid gridTracks;
	private GridTreeViewer gridViewerTracks;
	protected GridColumn colTrackNumber;
	private GridViewerColumn gvclTrackNumber;
	protected GridColumn colTitle;
	private GridViewerColumn gvcTitle;
	protected GridColumn colArtists;
	private GridViewerColumn gvcArtists;
	protected CTabItem tbtmRecordingSessions;
	protected Composite composite_1;
	protected Grid grid_1;
	private GridTableViewer gridTableViewer;
	protected GridColumn gridColumn_3;
	private GridViewerColumn gridViewerColumn_3;
	private Section sctnTracks;
	private Composite gridContainer;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ReleaseUI(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		scrldfrmRelease = formToolkit.createScrolledForm(this);
		scrldfrmRelease.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(scrldfrmRelease);
		scrldfrmRelease.setText("Release");
		scrldfrmRelease.getBody().setLayout(new GridLayout(1, false));
		
		Label lblName = formToolkit.createLabel(scrldfrmRelease.getBody(), "Name", SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		textName = formToolkit.createText(scrldfrmRelease.getBody(), "text", SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText("");
		
		sctnTracks = formToolkit.createSection(scrldfrmRelease.getBody(), Section.EXPANDED | Section.TITLE_BAR);
		sctnTracks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnTracks);
		sctnTracks.setText("Tracks");
		
		gridContainer = formToolkit.createComposite(sctnTracks, SWT.NONE);
		formToolkit.paintBordersFor(gridContainer);
		sctnTracks.setClient(gridContainer);
		gridContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		gridViewerTracks = new GridTreeViewer(gridContainer, SWT.BORDER);
		gridTracks = gridViewerTracks.getGrid();
		gridTracks.setHeaderVisible(true);
		formToolkit.paintBordersFor(gridTracks);
		
		gvclTrackNumber = new GridViewerColumn(gridViewerTracks, SWT.NONE);
		colTrackNumber = gvclTrackNumber.getColumn();
		colTrackNumber.setWidth(100);
		colTrackNumber.setText("Number");
		
		gvcTitle = new GridViewerColumn(gridViewerTracks, SWT.NONE);
		colTitle = gvcTitle.getColumn();
		colTitle.setWidth(100);
		colTitle.setText("Title");
		
		gvcArtists = new GridViewerColumn(gridViewerTracks, SWT.NONE);
		colArtists = gvcArtists.getColumn();
		colArtists.setWidth(100);
		colArtists.setText("Artist(s)");
		
		metaData = formToolkit.createSection(scrldfrmRelease.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		metaData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(metaData);
		metaData.setText("Meta Data");
		
		metadataContainer = formToolkit.createComposite(metaData, SWT.NONE);
		formToolkit.paintBordersFor(metadataContainer);
		metaData.setClient(metadataContainer);
		metadataContainer.setLayout(new GridLayout(2, false));
		
		labelLabel = formToolkit.createLabel(metadataContainer, "Label", SWT.NONE);
		
		labelDate = formToolkit.createLabel(metadataContainer, "Date", SWT.NONE);
		
		comboViewer = new ComboViewer(metadataContainer, SWT.NONE);
		comboLabel = comboViewer.getCombo();
		comboLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.paintBordersFor(comboLabel);
		
		dateTime = new CDateTime(metadataContainer, CDT.BORDER | CDT.CLOCK_24_HOUR);
		dateTime.setNullText("<no date>");
		formToolkit.adapt(dateTime);
		formToolkit.paintBordersFor(dateTime);
		tabFolder = new CTabFolder(metadataContainer, SWT.BORDER | SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		formToolkit.adapt(tabFolder);
		formToolkit.paintBordersFor(tabFolder);
		
		tbtmRecordingSessions = new CTabItem(tabFolder, SWT.NONE);
		tbtmRecordingSessions.setText("Recording Sessions");
		
		composite_1 = formToolkit.createComposite(tabFolder, SWT.NONE);
		tbtmRecordingSessions.setControl(composite_1);
		formToolkit.paintBordersFor(composite_1);
		composite_1.setLayout(new GridLayout(1, false));
		
		gridTableViewer = new GridTableViewer(composite_1, SWT.BORDER);
		grid_1 = gridTableViewer.getGrid();
		GridData gd_grid_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_grid_1.minimumHeight = 100;
		grid_1.setLayoutData(gd_grid_1);
		grid_1.setHeaderVisible(true);
		formToolkit.paintBordersFor(grid_1);
		
		gridViewerColumn_3 = new GridViewerColumn(gridTableViewer, SWT.NONE);
		gridColumn_3 = gridViewerColumn_3.getColumn();
		gridColumn_3.setWidth(200);
		gridColumn_3.setText("(stub - watch this space :-)");
		
		tbtmMedia = new CTabItem(tabFolder, SWT.NONE);
		tbtmMedia.setText("Media");
		
		tbtmContributors = new CTabItem(tabFolder, SWT.NONE);
		tbtmContributors.setText("Contributors");
		
		}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setEntity(Release entity) {
		if (m_bindingContext!=null) {
			m_bindingContext.dispose();
		}
		release = entity;
		m_bindingContext = initDataBindings();
	}

	@Override
	public Release getEntity() {
		return release;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textNameObserveTextObserveWidget = SWTObservables.observeText(textName, SWT.Modify);
		IObservableValue artistgetNameEmptyObserveValue = PojoObservables.observeValue(release, "name");
		bindingContext.bindValue(textNameObserveTextObserveWidget, artistgetNameEmptyObserveValue, null, null);
		//
		return bindingContext;
	}
}
