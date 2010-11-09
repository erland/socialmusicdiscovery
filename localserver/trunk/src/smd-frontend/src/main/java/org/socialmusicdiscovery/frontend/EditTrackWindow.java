package org.socialmusicdiscovery.frontend;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtkx.Bindable;
import org.apache.pivot.wtkx.WTKX;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Track;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class EditTrackWindow extends Window implements Bindable {
    private final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy");
    private final DateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    @WTKX TextInput trackNumberTextInput;
    @WTKX TextInput recordingNameTextInput;
    @WTKX TextInput recordingYearTextInput;
    @WTKX TextInput workNameTextInput;
    @WTKX TableView contributorsTableView;
    @WTKX PushButton cancelButton;
    @WTKX PushButton searchWorksButton;

    public class ContributorData {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Contributor getContributor() {
            return contributor;
        }

        public void setContributor(Contributor contributor) {
            this.contributor = contributor;
        }

        private String name;
        private String type;
        private Contributor contributor;
    }
    private Track track;

    private List<ContributorData> contributorData = new ArrayList<ContributorData>();

    private Resources resources;
    @Override
    public void initialize(Resources resources) {
        this.resources = resources;
    }

    public void open(Display display, Window owner, Track track) {
        this.track = track;

        searchWorksButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Alert.alert("Not implemented yet", getWindow());
            }
        });

        cancelButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                getWindow().close();
            }
        });

        if(track.getNumber() != null) {
            trackNumberTextInput.setText(track.getNumber().toString());
        }
        recordingNameTextInput.setText(track.getRecording().getName());
        if(track.getRecording().getWork()!=null) {
            workNameTextInput.setText(track.getRecording().getWork().getName());
        }
        if(track.getRecording().getDate() != null) {
            recordingYearTextInput.setText(DATE_FORMAT_DATE.format(this.track.getRecording().getDate()));
        }

        Set<Contributor> contributors = new HashSet<Contributor>(track.getRecording().getContributors());
        if(track.getRecording().getWork()!=null) {
            contributors.addAll(track.getRecording().getWork().getContributors());
        }
        updateContributors(contributors);

        super.open(display, owner);
    }

    private void updateContributors(java.util.Set<Contributor> contributors) {
        contributorData.clear();
        for (Contributor contributor : contributors) {
            ContributorData contributorData = new ContributorData();
            contributorData.setContributor(contributor);
            contributorData.name = contributor.getArtist().getName();
            contributorData.type = contributor.getType();
            this.contributorData.add(contributorData);
        }
        contributorsTableView.setTableData(this.contributorData);
    }
}
