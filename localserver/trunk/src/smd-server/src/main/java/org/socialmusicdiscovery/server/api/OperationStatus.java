package org.socialmusicdiscovery.server.api;

import com.google.gson.annotations.Expose;

public class OperationStatus {
    @Expose
    private Boolean success;
    public OperationStatus() {};
    public OperationStatus(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
