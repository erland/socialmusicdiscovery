package org.socialmusicdiscovery.rcp.editors.release;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.GridColumnLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
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
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

import com.sun.jersey.api.client.Client;

public class ReleaseUI extends AbstractComposite<Release> {

	private abstract class MyAbstractTrackLabelProvider extends GridColumnLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			super.update(cell);
			Track track = (Track)cell.getElement();
			Object value = getCellValue(track);
			String cellValue = value==null ? "---" : String.valueOf(value);
			cell.setText(cellValue);
		}

		protected abstract Object getCellValue(Track element);
	}
	private final class MyNumberLabelProvider extends MyAbstractTrackLabelProvider {
		@Override
		protected Object getCellValue(Track track) {
			return track.getNumber();
		}
	}

	private final class MyTitleLabelProvider extends MyAbstractTrackLabelProvider {
		@Override
		protected Object getCellValue(Track track) {
			String name = null;
			Recording recording = track.getRecording();
			if (recording!=null) {
				name = recording.getName();
				if (name==null && recording.getWork()!=null) {
					name = recording.getWork().getName();
				}
			}
			return name;
		}
	}

	private class MyContributorLabelProvider extends MyAbstractTrackLabelProvider {
		private final String contributionType;
		private MyContributorLabelProvider(String contributionType) {
			super();
			this.contributionType = contributionType;
		}
		
		@Override
		protected Object getCellValue(Track track) {
            Set<Contributor> contributorSet = new HashSet<Contributor>(track.getRecording().getContributors());
            if (track.getRecording().getWork() != null) {
                contributorSet.addAll(track.getRecording().getWork().getContributors());
            }
            Map<String, StringBuilder> contributors = getContributorMap(contributorSet);
            return contributors.get(contributionType);
		}
	    private Map<String, StringBuilder> getContributorMap(Set<Contributor> contributorSet) {
	        Map<String, StringBuilder> contributors = new HashMap<String, StringBuilder>();
	        for (Contributor contributor : contributorSet) {
	            if (!contributors.containsKey(contributor.getType())) {
	                contributors.put(contributor.getType(), new StringBuilder());
	            }
	            StringBuilder contributorString = contributors.get(contributor.getType());
	            if (contributorString.length() > 0) {
	                contributorString.append(", ");
	            }
	            contributorString.append(contributor.getArtist().getName());
	        }
	        return contributors;
	    }
	}

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
	private GridTableViewer gridViewerTracks;
	protected GridColumn colTrackNumber;
	private GridViewerColumn gvcTrackNumber;
	protected GridColumn colTitle;
	private GridViewerColumn gvcTitle;
	protected GridColumn colPerformer;
	private GridViewerColumn gvcPerformer;
	protected CTabItem tbtmRecordingSessions;
	protected Composite composite_1;
	protected Grid grid_1;
	private GridTableViewer gridTableViewer;
	protected GridColumn gridColumn_3;
	private GridViewerColumn gridViewerColumn_3;
	private Section sctnTracks;
	private Composite gridContainer;
	private GridColumn colComposer;
	private GridViewerColumn gvcComposer;
	private GridColumn colConductor;
	private GridViewerColumn gvcConductor;
	private GridColumnGroup groupContributors;
	private GridColumn gridColumn;

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
		
		gridViewerTracks = new GridTableViewer(gridContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		gridTracks = gridViewerTracks.getGrid();
		gridTracks.setCellSelectionEnabled(true);
		gridTracks.setHeaderVisible(true);
		formToolkit.paintBordersFor(gridTracks);
		
		gvcTrackNumber = new GridViewerColumn(gridViewerTracks, SWT.NONE);
		gvcTrackNumber.setLabelProvider(new MyNumberLabelProvider());
		colTrackNumber = gvcTrackNumber.getColumn();
		colTrackNumber.setMoveable(true);
		colTrackNumber.setWidth(100);
		colTrackNumber.setText("Number");
		
		gvcTitle = new GridViewerColumn(gridViewerTracks, SWT.NONE);
		gvcTitle.setLabelProvider(new MyTitleLabelProvider());
		colTitle = gvcTitle.getColumn();
		colTitle.setMoveable(true);
		colTitle.setWidth(100);
		colTitle.setText("Title");
		
		groupContributors = new GridColumnGroup(gridTracks, SWT.NONE);
		groupContributors.setText("Contributors");
		colPerformer = new GridColumn(groupContributors, SWT.NONE);
		gvcPerformer = new GridViewerColumn(gridViewerTracks, colPerformer);
		gvcPerformer.setLabelProvider(new MyContributorLabelProvider("performer"));
		colPerformer = gvcPerformer.getColumn();
		colPerformer.setMoveable(true);
		colPerformer.setWidth(100);
		colPerformer.setText("Performer(s)");
		
		colComposer = new GridColumn(groupContributors, SWT.NONE);
		gvcComposer = new GridViewerColumn(gridViewerTracks, colComposer);
		colComposer.setMoveable(true);
		colComposer.setWidth(100);
		colComposer.setText("Composer(s)");
		gvcComposer.setLabelProvider(new MyContributorLabelProvider("composer"));
		
		colConductor = new GridColumn(groupContributors, SWT.NONE);
		gvcConductor = new GridViewerColumn(gridViewerTracks, colConductor);
		colConductor.setMoveable(true);
		colConductor.setWidth(100);
		colConductor.setText("Conductor");
		gvcConductor.setLabelProvider(new MyContributorLabelProvider("conductor"));
		
		
		metaData = formToolkit.createSection(scrldfrmRelease.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		metaData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(metaData);
		metaData.setText("Meta Data");
		metaData.setExpanded(true);
		
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

		initStatic();
		}

	private void initStatic() {
		gridViewerTracks.setContentProvider(new ArrayContentProvider());
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
		release = getRelease(entity);
		gridViewerTracks.setInput(release.getTracks());
		m_bindingContext = initDataBindings();
	}

	private Release getRelease(Release entity) {
	    Release release = Client.create().resource("http://localhost:9998/releases/" + entity.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);
		return release;
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
