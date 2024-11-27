package BusinessLogic;

import java.util.ArrayList;

public class Employee {
    private String employeeID;
    private String fullName;
    private String role;
    private String contactInfo;
    private ArrayList<Schedule> scheduleList;

    public Employee(String employeeID, String fullName, String role, String contactInfo, ArrayList<Schedule> scheduleList) {
        this.employeeID = employeeID;
        this.fullName = fullName;
        this.role = role;
        this.contactInfo = contactInfo;
        this.scheduleList = scheduleList;
    }

    public Employee (String employeeID, String fullName, String role) {
        this.employeeID = employeeID;
        this.fullName = fullName;
        this.role = role;
        this.contactInfo = "";  
        this.scheduleList = new ArrayList<>();
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public ArrayList<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(ArrayList<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public Schedule getSchedule() {
        if (scheduleList!= null &&!scheduleList.isEmpty()) {
            return scheduleList.get(0); // Return the first schedule
        } else {
            return null; // No schedule available
        }
    }
}