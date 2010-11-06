package org.socialmusicdiscovery.server.api.management.mediaimport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mediaimporter")
public class MediaImportStatus {
    private String module;
    private String currentDescription;
    private Long currentNumber;
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
