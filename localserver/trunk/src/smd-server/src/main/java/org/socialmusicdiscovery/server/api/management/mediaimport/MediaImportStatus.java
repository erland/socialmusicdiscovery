package org.socialmusicdiscovery.server.api.management.mediaimport;

import com.google.gson.annotations.Expose;

public class MediaImportStatus {
    @Expose
    private String module;
    @Expose
    private String currentDescription;
    @Expose
    private Long currentNumber;
    @Expose
    private Long totalNumber;

    public MediaImportStatus() {}
    public MediaImportStatus(String module, String currentDescription, Long currentNumber, Long totalNumber) {
        this.module = module;
        this.currentDescription = currentDescription;
        this.currentNumber = currentNumber;
        this.totalNumber = totalNumber;
    }
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCurrentDescription() {
        return currentDescription;
    }

    public void setCurrentDescription(String currentDescription) {
        this.currentDescription = currentDescription;
    }

    public Long getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Long currentNumber) {
        this.currentNumber = currentNumber;
    }

    public Long getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Long totalNumber) {
        this.totalNumber = totalNumber;
    }
}
