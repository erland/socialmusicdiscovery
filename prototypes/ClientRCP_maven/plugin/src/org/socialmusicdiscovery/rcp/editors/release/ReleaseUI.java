package org.socialmusicdiscovery.rcp.editors.release;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.GridColumnLabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.socialmusicdiscovery.rcp.content.ObservableRelease;
import org.socialmusicdiscovery.rcp.views.util.AbstractComposite;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;

public class ReleaseUI extends AbstractComposite<ObservableRelease> {

	private static final String CONTRIBUTOR_TYPE_CONDUCTOR = "conductor";
	private static final String CONTRIBUTOR_TYPE_COMPOSER = "composer";
	private static final String CONTRIBUTOR_TYPE_PERFORMER = "performer";
	
	// use to separate name fragments, e.g. to compile names of many Work into name of 1 Recording
	// TODO make user configurable?
	private static final String COMPOSITE_NAME_SEPARATOR = "/"; 
	
	private abstract class MyAbstractTrackLabelProvider extends GridColumnLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			super.update(cell);
			Track track = (Track)cell.getElement();
			Object value = getCellValue(track);
			String cellValue = value==null ? "" : String.valueOf(value);
			cell.setText(cellValue);
		}

		protected abstract Object getCellValue(Track element);
	}
	private final class MyTrackNumberLabelProvider extends MyAbstractTrackLabelProvider {
		@Override
		protected Object getCellValue(Track track) {
			return track.getNumber();
		}
	}
	private class MyTrackMediumNumberLabelProvider extends MyAbstractTrackLabelProvider  {
		@Override
		protected Object getCellValue(Track track) {
//			List<Medium> media = release.getMediums();
//			int i=0;
//			for (Medium medium : media) {
//				i++;
//				List<Track> tracks = medium.getTracks();
//				if (contains(tracks, track)) {
//					return Integer.valueOf(i);
//				}
//			}
			return null;
		}

//		private boolean contains(Collection<? extends SMDIdentity> members, SMDIdentity prospect) {
//			// THIS DOES NOT WORK - we really want the same instances ...
//			for (SMDIdentity e : members) {
//				if (e.getReference().equals(prospect.getReference())) {
//					return true;
//				}
//			}
//			return false;
//		}

	}


	private final class MyTrackTitleLabelProvider extends MyAbstractTrackLabelProvider {
		@Override
		protected Object getCellValue(Track track) {
			String name = null;
			Recording recording = track.getRecording();
			if (recording!=null) {
				name = recording.getName();
				if (isEmpty(name) && hasWork(recording)) {
					name = resolveName(recording.getWorks());
				}
			}
			return name;
		}

	}

	private class MyTrackContributorLabelProvider extends MyAbstractTrackLabelProvider {
		private final String contributionType;
		private MyTrackContributorLabelProvider(String contributionType) {
			super();
			this.contributionType = contributionType;
		}
		
		@Override
		protected Object getCellValue(Track track) {
            Recording recording = track.getRecording();
			Set<Contributor> contributorSet = new HashSet<Contributor>(recording.getContributors());
            if (hasWork(recording)) {
                contributorSet.addAll(compileContributors(recording));
            }
            Map<String, StringBuilder> contributors = getContributorMap(contributorSet);
            return contributors.get(contributionType);
		}
		
	    private Collection<? extends Contributor> compileContributors(Recording recording) {
	    	Set<Contributor> contributors = new HashSet<Contributor>();
	    	for (Work w : recording.getWorks()) {
				contributors.addAll(w.getContributors());
			}
			return contributors;
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

	private Text textName;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	protected ScrolledForm scrldfrmRelease;
	protected Text text;
	protected Section sectionAlbumData;
	protected Composite compositeAlbumData;
	protected CTabFolder tabFolderAlbumData;
	protected CTabItem tabItemComposers;
	protected CTabItem tabItemConductors;
	protected Grid gridTracks;
	private GridTableViewer gridViewerTracks;
	protected GridColumn colTrackNumber;
	private GridViewerColumn gvcTrackNumber;
	protected GridColumn colTitle;
	private GridViewerColumn gvcTitle;
	protected GridColumn colPerformer;
	private GridViewerColumn gvcPerformer;
	protected CTabItem tabItemPerformers;
	private Section sctnTracks;
	private Composite gridContainer;
	private GridColumn colComposer;
	private GridViewerColumn gvcComposer;
	private GridColumn colConductor;
	private GridViewerColumn gvcConductor;
	private GridColumnGroup groupContributors;
	private ContributorPanel performersPanel;
	private ContributorPanel composersPanel;
	private ContributorPanel conductorsPanel;
	private GridColumn colMediumNbr;
	private GridViewerColumn gvcMediumNbr;
	private GridColumnGroup groupNumbers;

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
		
		groupNumbers = new GridColumnGroup(gridTracks, SWT.TOGGLE);
		groupNumbers.setExpanded(false);
		groupNumbers.setText("Index");
		
		colMediumNbr = new GridColumn(groupNumbers, SWT.RIGHT);
		colMediumNbr.setVisible(false);
		colMediumNbr.setSummary(false);
		colMediumNbr.setSort(SWT.UP);
		colMediumNbr.setWidth(100);
		colMediumNbr.setText("Medium#");
		gvcMediumNbr = new GridViewerColumn(gridViewerTracks, colMediumNbr);
		gvcMediumNbr.setLabelProvider(new MyTrackMediumNumberLabelProvider());
		
		colTrackNumber = new GridColumn(groupNumbers, SWT.RIGHT);
		colTrackNumber.setMoveable(true);
		colTrackNumber.setWidth(100);
		colTrackNumber.setText("Track#");
		gvcTrackNumber = new GridViewerColumn(gridViewerTracks, colTrackNumber);
		gvcTrackNumber.setLabelProvider(new MyTrackNumberLabelProvider());
		
		gvcTitle = new GridViewerColumn(gridViewerTracks, SWT.NONE);
		gvcTitle.setLabelProvider(new MyTrackTitleLabelProvider());
		colTitle = gvcTitle.getColumn();
		colTitle.setMoveable(true);
		colTitle.setWidth(300);
		colTitle.setText("Title");
		
		groupContributors = new GridColumnGroup(gridTracks, SWT.NONE);
		groupContributors.setText("Contributors");
		colPerformer = new GridColumn(groupContributors, SWT.NONE);
		gvcPerformer = new GridViewerColumn(gridViewerTracks, colPerformer);
		gvcPerformer.setLabelProvider(new MyTrackContributorLabelProvider(CONTRIBUTOR_TYPE_PERFORMER));
		colPerformer = gvcPerformer.getColumn();
		colPerformer.setMoveable(true);
		colPerformer.setWidth(100);
		colPerformer.setText("Performer(s)");
		
		colComposer = new GridColumn(groupContributors, SWT.NONE);
		gvcComposer = new GridViewerColumn(gridViewerTracks, colComposer);
		colComposer.setMoveable(true);
		colComposer.setWidth(100);
		colComposer.setText("Composer(s)");
		gvcComposer.setLabelProvider(new MyTrackContributorLabelProvider(CONTRIBUTOR_TYPE_COMPOSER));
		
		colConductor = new GridColumn(groupContributors, SWT.NONE);
		gvcConductor = new GridViewerColumn(gridViewerTracks, colConductor);
		colConductor.setMoveable(true);
		colConductor.setWidth(100);
		colConductor.setText("Conductor");
		gvcConductor.setLabelProvider(new MyTrackContributorLabelProvider(CONTRIBUTOR_TYPE_CONDUCTOR));
		
		
		sectionAlbumData = formToolkit.createSection(scrldfrmRelease.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		sectionAlbumData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		formToolkit.paintBordersFor(sectionAlbumData);
		sectionAlbumData.setText("Album Data");
		sectionAlbumData.setExpanded(true);
		
		compositeAlbumData = formToolkit.createComposite(sectionAlbumData, SWT.NONE);
		formToolkit.paintBordersFor(compositeAlbumData);
		sectionAlbumData.setClient(compositeAlbumData);
		compositeAlbumData.setLayout(new GridLayout(2, false));
		tabFolderAlbumData = new CTabFolder(compositeAlbumData, SWT.BORDER | SWT.BOTTOM);
		tabFolderAlbumData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		formToolkit.adapt(tabFolderAlbumData);
		formToolkit.paintBordersFor(tabFolderAlbumData);
		
		tabItemPerformers = new CTabItem(tabFolderAlbumData, SWT.NONE);
		tabItemPerformers.setText("Performer(s)");
		
		performersPanel = new ContributorPanel(tabFolderAlbumData, SWT.NONE);
		tabItemPerformers.setControl(performersPanel);
		formToolkit.paintBordersFor(performersPanel);
		
		
		tabItemComposers = new CTabItem(tabFolderAlbumData, SWT.NONE);
		tabItemComposers.setText("Composer(s)");
		
		composersPanel = new ContributorPanel(tabFolderAlbumData, SWT.NONE);
		tabItemComposers.setControl(composersPanel);
		formToolkit.paintBordersFor(composersPanel);
		
		tabItemConductors = new CTabItem(tabFolderAlbumData, SWT.NONE);
		tabItemConductors.setText("Conductor(s)");
		
		conductorsPanel = new ContributorPanel(tabFolderAlbumData, SWT.NONE);
		tabItemConductors.setControl(conductorsPanel);
		formToolkit.paintBordersFor(conductorsPanel);

		initStatic();
		}

	private void initStatic() {
		gridViewerTracks.setContentProvider(new ArrayContentProvider());
		tabFolderAlbumData.setSelection(tabItemPerformers);
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void afterSetModel(ObservableRelease release) {
		gridViewerTracks.setInput(getModel().getTracks());
		colMediumNbr.pack();
		colTrackNumber.pack();
		bindAlbumData();
	}


	private void bindAlbumData() {
		Map<String, List<Contributor>> map = createContributorMap(getModel().getContributors());
		bindContributor(getPerformersPanel(), map, CONTRIBUTOR_TYPE_PERFORMER);
		bindContributor(getComposersPanel(), map, CONTRIBUTOR_TYPE_COMPOSER);
		bindContributor(getConductorsPanel(), map, CONTRIBUTOR_TYPE_CONDUCTOR);
	}

	// TODO rewrite for data binding; this is a derive map, not properly backed by model object 
	private Map<String, List<Contributor>> createContributorMap(Set<Contributor> contributorSet) {
	    Map<String, List<Contributor>> contributors = new HashMap<String, List<Contributor>>();
	    for (Contributor contributor : contributorSet) {
	        String type = contributor.getType();
			if (!contributors.containsKey(type)) {
	            contributors.put(type, new ArrayList<Contributor>());
	        }
			contributors.get(type).add(contributor);
	    }
	    return contributors;
	}

	private void bindContributor(ContributorPanel panel, Map<String, List<Contributor>> map, String key) {
		List<Contributor> contributors = safeGet(map, key);
		
		GridTableViewer viewer = panel.getGridViewer();
		WritableList list = new WritableList(contributors, Contributor.class);
		IBeanValueProperty labelProperty = BeanProperties.value(Contributor.class, "artist.name");
		
		ViewerSupport.bind(viewer, list, labelProperty);
	}

	private List<Contributor> safeGet(Map<String, List<Contributor>> map, String key) {
		List<Contributor> result = map.get(key);
		if (result==null) {
			result = new ArrayList<Contributor>();
			map.put(key, result);
		}
		return result;
	}

	private static boolean isEmpty(String s) {
		return s==null || s.trim().length()<1;
	}
	public ContributorPanel getPerformersPanel() {
		return performersPanel;
	}
	public ContributorPanel getConductorsPanel() {
		return conductorsPanel;
	}
	public ContributorPanel getComposersPanel() {
		return composersPanel;
	}
	public GridTableViewer getGridViewerTracks() {
		return gridViewerTracks;
	}

	protected boolean hasWork(Recording recording) {
		return !recording.getWorks().isEmpty();
	}

	protected String resolveName(Set<Work> set) {
		StringBuilder sb = new StringBuilder();
		for (Work work : set) {
			if (sb.length()>0) {
				sb.append(COMPOSITE_NAME_SEPARATOR);
			}
			sb.append(work.getName());
		}
		return sb.toString();
	}

	/**
	 * @return {@link ObservableRelease}
	 * @see #getModel()
	 */
	public ObservableRelease getRelease() {
		return getModel();
	}
	
	@Override
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textNameObserveTextObserveWidget = SWTObservables.observeText(textName, SWT.Modify);
		IObservableValue artistgetNameEmptyObserveValue = BeansObservables.observeValue(getModel(), "name");
		bindingContext.bindValue(textNameObserveTextObserveWidget, artistgetNameEmptyObserveValue, null, null);
		//
		IObservableValue textNameObserveTooltipTextObserveWidget = SWTObservables.observeTooltipText(textName);
		IObservableValue getModelToolTipTextObserveValue = BeansObservables.observeValue(getModel(), "toolTipText");
		bindingContext.bindValue(textNameObserveTooltipTextObserveWidget, getModelToolTipTextObserveValue, null, null);
		//
		return bindingContext;
	}
}
