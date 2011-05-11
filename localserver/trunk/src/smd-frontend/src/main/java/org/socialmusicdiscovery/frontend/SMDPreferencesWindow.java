/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.frontend;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;

import javax.ws.rs.core.MediaType;
import java.net.URL;

public class SMDPreferencesWindow extends Window implements Bindable {
    @Inject
    @Named("smd-server.host")
    private String SMDSERVER;

    @Inject
    @Named("org.socialmusicdiscovery.server.port")
    private String SMDSERVERPORT;

    /**
     * URL to SMD Server application
     */
    private String HOSTURL = null;

    @BXML
    PushButton closeButton;

    @BXML
    TableView preferencesTableView;

    private Resources resources;

    @Inject
    private ClientConfig config;

    /**
     * Representation of a single row in one of the search result tables
     */
    public static class TableViewItem {
        private String name;
        private ConfigurationParameter.Type type;
        private String value;

        public TableViewItem(String name, String type, String value) {
            this.name = name;
            this.value = value;
            this.type = ConfigurationParameter.Type.valueOf(type);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            if (type == ConfigurationParameter.Type.INTEGER) {
                this.value = Integer.valueOf(value).toString();
            } else if (type == ConfigurationParameter.Type.DOUBLE) {
                this.value = Double.valueOf(value).toString();
            } else if (type == ConfigurationParameter.Type.BOOLEAN) {
                this.value = Boolean.valueOf(value).toString();
            } else {
                this.value = value;
            }
        }

        public String getType() {
            return type.toString();
        }

        public void setType(String type) {
            this.type = ConfigurationParameter.Type.valueOf(type);
        }
    }

    @Override
    public void initialize(org.apache.pivot.collections.Map<String, Object> stringObjectMap, URL url, Resources resources) {
        this.resources = resources;
        InjectHelper.injectMembers(this);
        HOSTURL = "http://" + SMDSERVER + ":" + SMDSERVERPORT;
    }

    @Override
    public void open(Display display, Window owner) {
        super.open(display, owner);

        try {

            JSONArray array = Client.create().resource(HOSTURL + "/configurations").accept(MediaType.APPLICATION_JSON).get(JSONArray.class);
            List<TableViewItem> configurations = new ArrayList();
            for (int i = 0; i < array.length(); i++) {
                String name = array.getJSONObject(i).getString("id");
                String type = array.getJSONObject(i).getString("type");
                String value = array.getJSONObject(i).getString("value");
                TableViewItem item = new TableViewItem(name, type, value);
                configurations.add(item);
            }
            preferencesTableView.setTableData(configurations);
            preferencesTableView.getTableViewRowListeners().add(new TableViewRowListener.Adapter() {
                @Override
                public void rowUpdated(TableView tableView, int index) {
                    TableViewItem item = (TableViewItem) tableView.getTableData().get(index);
                    ConfigurationParameterEntity parameter = new ConfigurationParameterEntity(item.getName(), ConfigurationParameter.Type.valueOf(item.getType()), item.getValue());
                    Client.create(config).resource(HOSTURL + "/configurations/" + item.getName()).type(MediaType.APPLICATION_JSON).put(ConfigurationParameter.class, parameter);
                }
            });

            // Setup listeners for close button
            closeButton.getButtonPressListeners().add(new ButtonPressListener() {
                @Override
                public void buttonPressed(Button button) {
                    getWindow().close();
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
