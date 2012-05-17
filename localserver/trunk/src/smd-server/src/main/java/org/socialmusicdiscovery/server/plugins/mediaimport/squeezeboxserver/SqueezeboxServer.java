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

package org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.ImageProviderManager;
import org.socialmusicdiscovery.server.business.model.core.Image;
import org.socialmusicdiscovery.server.business.model.core.ImageEntity;
import org.socialmusicdiscovery.server.plugins.mediaimport.AbstractTagImporter;
import org.socialmusicdiscovery.server.plugins.mediaimport.TagData;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Map;

/**
 * Media import module for Squeezebox Server, require the Social Music Discovery plugin installed in Squeezebox Server to work
 */
public class SqueezeboxServer extends AbstractTagImporter implements MediaImporter {

    @Inject
    private ImageProviderManager imageProviderManager;

    @Inject
    @Named("squeezeboxserver.username")
    private String squeezeboxServerUsername;

    @Inject
    @Named("squeezeboxserver.password")
    private String squeezeboxServerPassword;

    @Inject
    @Named("squeezeboxserver.passwordhash")
    private String squeezeboxServerPasswordHash;

    @Inject
    @Named("squeezeboxserver.host")
    private String squeezeboxServerHost;

    @Inject
    @Named("squeezeboxserver.port")
    private String squeezeboxServerPort;

    /**
     * @inherit
     */
    public String getId() {
        return "squeezeboxserver";
    }

    /**
     * @inherit
     */
    @Override
    public void init(Map<String, String> executionParameters) {
        super.init(executionParameters);
    }

    /**
     * @inherit
     */
    public void executeImport(ProcessingStatusCallback progressHandler) {
        TrackListData trackList = null;
        final long CHUNK_SIZE = 20;
        final String SERVICE_URL = "http://" + squeezeboxServerHost + ":" + squeezeboxServerPort + "/jsonrpc.js";
        long offset = 0;

        try {
            JSONObject request = createRequest(offset, CHUNK_SIZE);
            Client c = Client.create();
            if (squeezeboxServerUsername != null && squeezeboxServerUsername.length() > 0) {
                if (squeezeboxServerPassword != null && squeezeboxServerPassword.length() > 0) {
                    c.addFilter(new HTTPBasicAuthFilter(squeezeboxServerUsername, squeezeboxServerPassword));
                } else if (squeezeboxServerPasswordHash != null && squeezeboxServerPasswordHash.length() > 0) {
                    c.addFilter(new HTTPBasicAuthFilter(squeezeboxServerUsername, squeezeboxServerPasswordHash));
                }
            }
            JSONObject response = null;
            if (squeezeboxServerPasswordHash != null && squeezeboxServerPasswordHash.length() > 0) {
                response = c.resource(SERVICE_URL).accept("application/json").header("X-Scanner", 1).post(JSONObject.class, request);
            } else {
                response = c.resource(SERVICE_URL).accept("application/json").post(JSONObject.class, request);
            }
            ObjectMapper mapper = new ObjectMapper();
            trackList = mapper.readValue(response.getString("result"), TrackListData.class);

            if (trackList != null) {
                while (trackList != null && !isAborted()) {
                    long i = 0;
                    entityManager.getTransaction().begin();
                    for (TrackData track : trackList.getTracks()) {
                        progressHandler.progress(getId(), track.getFile(), trackList.getOffset() + i + 1, trackList.getCount());
                        try {
                            importNewPlayableElement(track);
                        } catch (ConstraintViolationException e) {
                            //TODO: Change this so it uses the logging framework
                            System.err.println("ERROR when importing: " + track.getFile() + ": ");
                            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                                System.err.println("- " + violation.getLeafBean().getClass().getSimpleName() + "." + violation.getPropertyPath().toString() + ": " + violation.getMessage());
                            }
                        }
                        i++;
                    }
                    entityManager.flush();
                    entityManager.clear();
                    entityManager.getTransaction().commit();
                    if (offset + trackList.getTracks().size() < trackList.getCount()) {
                        offset = offset + trackList.getTracks().size();
                        request = createRequest(offset, CHUNK_SIZE);
                        if (squeezeboxServerPasswordHash != null && squeezeboxServerPasswordHash.length() > 0) {
                            response = c.resource(SERVICE_URL).accept("application/json").header("X-Scanner", 1).post(JSONObject.class, request);
                        } else {
                            response = c.resource(SERVICE_URL).accept("application/json").post(JSONObject.class, request);
                        }
                        trackList = mapper.readValue(response.getString("result"), TrackListData.class);
                    } else {
                        trackList = null;
                    }
                }

                if (isAborted()) {
                    progressHandler.aborted(getId());
                } else {
                    progressHandler.finished(getId());
                }
            } else {
                progressHandler.failed(getId(), "Unable to retrieve data");
            }
        } catch (IOException e) {
            e.printStackTrace();
            progressHandler.failed(getId(), e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            progressHandler.failed(getId(), e.getLocalizedMessage());
        }
    }


    /**
     * Creates a JSON object representing the JSON request needed to request tracks from the Social Music Discovery SBS plugin
     *
     * @param offset    Offset to use when starting to retrieve tracks
     * @param chunkSize Number of tracks to retrieve data for
     * @return A JSON object
     * @throws JSONException
     */
    private JSONObject createRequest(long offset, long chunkSize) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("id", 1L);
        request.put("method", "slim.request");
        JSONArray paramsArray = new JSONArray();
        paramsArray.put("-");
        JSONArray paramsArray2 = new JSONArray();
        paramsArray2.put("socialmusicdiscovery");
        paramsArray2.put("tracks");
        paramsArray2.put("offset:" + offset);
        paramsArray2.put("size:" + chunkSize);
        paramsArray.put(paramsArray2);
        request.put("params", paramsArray);
        return request;
    }

    @Override
    protected ImageEntity getReleaseImage(TrackData data) {
        String sbsCoverId = null;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equalsIgnoreCase(TagData.SBS_COVER_ID)) {
                sbsCoverId = tagData.getValue();
                break;
            }
        }
        if (sbsCoverId != null) {
            ImageEntity defaultImage = new ImageEntity();
            defaultImage.setProviderId(SqueezeboxServerImageProvider.PROVIDER_ID);
            defaultImage.setProviderImageId(sbsCoverId);
            defaultImage.setType(Image.TYPE_COVER_FRONT);
            defaultImage.setUri(imageProviderManager.getProvider(SqueezeboxServerImageProvider.PROVIDER_ID).getImageURL(defaultImage));
            return defaultImage;
        }
        return null;
    }
}
