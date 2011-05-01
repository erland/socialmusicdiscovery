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

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;

import java.io.IOException;
import java.net.URL;

public class SMDApplicationWindow extends Window implements Bindable {
    @BXML
    PushButton listViewButton;

    @BXML
    PushButton crudSearchButton;

    private Resources resources;

    @Override
    public void initialize(org.apache.pivot.collections.Map<String, Object> stringObjectMap, URL url, Resources resources) {
        this.resources = resources;
        InjectHelper.injectMembers(this);
    }

    @Override
    public void open(Display display, Window owner) {
        super.open(display, owner);

        crudSearchButton.getButtonPressListeners().add(new ButtonPressListener() {
             @Override
             public void buttonPressed(Button button) {
                 try {
                     BXMLSerializer wtkxSerializer = new BXMLSerializer();
                     SMDCRUDSearchWindow window = (SMDCRUDSearchWindow) wtkxSerializer.readObject(getClass().getResource("SMDCRUDSearchWindow.bxml"),new Resources(resources,SMDCRUDSearchWindow.class.getName()));
                     window.open(getDisplay(), getWindow());
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 } catch (SerializationException e) {
                     throw new RuntimeException(e);
                 }
             }
         });

        listViewButton.getButtonPressListeners().add(new ButtonPressListener() {
             @Override
             public void buttonPressed(Button button) {
                 try {
                     BXMLSerializer wtkxSerializer = new BXMLSerializer();
                     SMDListViewWindow window = (SMDListViewWindow) wtkxSerializer.readObject(getClass().getResource("SMDListViewWindow.bxml"),new Resources(resources,SMDListViewWindow.class.getName()));
                     window.open(getDisplay(), getWindow());
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 } catch (SerializationException e) {
                     throw new RuntimeException(e);
                 }
             }
         });
    }
}
