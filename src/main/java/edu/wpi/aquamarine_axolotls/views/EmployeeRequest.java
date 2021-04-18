package edu.wpi.aquamarine_axolotls.views;

import java.util.Map;

public class EmployeeRequest {

    private String assigned;
    private String assignee;
    private String status;
    private String serviceRequest;
    private String location;

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssigned() {
        return assigned;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getStatus() {
        return status;
    }

    public String getServiceRequest() {
        return serviceRequest;
    }

    public String getLocation() {
        return location;
    }

    public EmployeeRequest(Map<String, String> sr) {
        this.assigned = "N/A"; // TODO : maybe this should be left null?
        this.assignee = sr.get("FIRSTNAME") + " " + sr.get("LASTNAME");
        this.status = "Not Assigned";
        this.serviceRequest = sr.get("REQUESTTYPE");
        this.location = sr.get("LOCATIONID");
    }
}
