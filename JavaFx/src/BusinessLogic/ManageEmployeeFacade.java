package BusinessLogic;

import java.util.ArrayList;

import DataBase.DataBase;

public class ManageEmployeeFacade {
    private ArrayList<Employee> employees;

    public ManageEmployeeFacade() {
        employees = DataBase.Instance().getEmployees();
    }

    
    public Employee createNewProfile(String ID, String name, String role) {
        Employee newEmployee = new Employee(ID, name, role);
        employees.add(newEmployee);
        DataBase.Instance().addEmployee(newEmployee); // Ensure this method adds the employee to your database
        return newEmployee;
    }

    public void modifyProfile(Employee updatedEmployee) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeID().equals(updatedEmployee.getEmployeeID())) {
                employees.set(i, updatedEmployee);
                DataBase.Instance().updateEmployee(updatedEmployee); 
                break;
            }
        }
    }

    public void deleteProfile(String employeeID) {
        employees.removeIf(employee -> employee.getEmployeeID().equals(employeeID));
        DataBase.Instance().removeEmployee(employeeID); 
    }

    public Employee findEmployee(String employeeID) {
        for (Employee employee : employees) {
            if (employee.getEmployeeID().equals(employeeID)) {
                return employee;
            }
        }
        return null;
    }

    public Schedule setScheduleForEmp(String employeeID, String day, String startTime, String endTime) {
        Employee employeeToUpdate = findEmployee(employeeID);
        
        if (employeeToUpdate != null) {
            Schedule newSchedule = new Schedule(day, startTime, endTime);
            
            employeeToUpdate.getScheduleList().add(newSchedule);
            
            DataBase.Instance().addEmployeeSchedule(employeeToUpdate, newSchedule);
            return newSchedule;
        }
        return null;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }
}
