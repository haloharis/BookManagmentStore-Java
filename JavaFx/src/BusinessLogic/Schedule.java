package BusinessLogic;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class Schedule {

    // Fields defined as StringProperty to work with JavaFX bindings
    private StringProperty day;
    private StringProperty startTime;
    private StringProperty endTime;

    // Constructor
    public Schedule(String day, String startTime, String endTime) {
        this.day = new SimpleStringProperty(day);
        this.startTime = new SimpleStringProperty(startTime);
        this.endTime = new SimpleStringProperty(endTime);
    }

    // Getter for day
    public String getDay() {
        return day.get();
    }

    // Setter for day
    public void setDay(String day) {
        this.day.set(day);
    }

    // Getter for startTime
    public String getStartTime() {
        return startTime.get();
    }

    // Setter for startTime
    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

    // Getter for endTime
    public String getEndTime() {
        return endTime.get();
    }

    // Setter for endTime
    public void setEndTime(String endTime) {
        this.endTime.set(endTime);
    }

    // Property methods (these allow for bindings in JavaFX)
    public StringProperty dayProperty() {
        return day;
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public StringProperty endTimeProperty() {
        return endTime;
    }
}
