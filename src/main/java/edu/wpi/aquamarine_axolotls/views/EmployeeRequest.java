package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.db.DatabaseInfo;

import java.util.Map;

public class EmployeeRequest {

    private String assigned;
    private String assignee;
    private String status;
    private String serviceRequest;
    private String location;
    private String requestID;

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

    public String getRequestID() {
        return requestID;
    }

    public EmployeeRequest(Map<String, String> sr) {
        this.assigned = sr.get("EMPLOYEEID"); // TODO : update when DB database is here
        this.assignee = sr.get("FIRSTNAME") + " " + sr.get("LASTNAME");
        this.status = sr.get("STATUS");
        this.serviceRequest = sr.get("REQUESTTYPE");
        this.location = sr.get("LOCATIONID");
        this.requestID = sr.get("REQUESTID");
    }
}
