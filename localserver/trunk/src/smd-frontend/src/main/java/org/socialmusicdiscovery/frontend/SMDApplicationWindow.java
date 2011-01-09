package org.socialmusicdiscovery.frontend;

import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtkx.Bindable;
import org.apache.pivot.wtkx.WTKX;
import org.apache.pivot.wtkx.WTKXSerializer;

import java.io.IOException;

public class SMDApplicationWindow extends Window implements Bindable {
    @WTKX
    PushButton listViewButton;

    @WTKX
    PushButton crudSearchButton;

    private Resources resources;

    @Override
    public void initialize(Resources resources) {
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
                     WTKXSerializer wtkxSerializer = new WTKXSerializer(resources);
                     SMDCRUDSearchWindow window = (SMDCRUDSearchWindow) wtkxSerializer.readObject(this, "SMDCRUDSearchWindow.wtkx");
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
                     WTKXSerializer wtkxSerializer = new WTKXSerializer(resources);
                     SMDListViewWindow window = (SMDListViewWindow) wtkxSerializer.readObject(this, "SMDListViewWindow.wtkx");
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
