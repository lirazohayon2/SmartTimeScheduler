package project;

import java.time.LocalTime;
import java.util.List;

public class PersonalConstraint {
    private String name;
    private List<TimeSlot> timeSlots;
    
    public PersonalConstraint(String name,  List<TimeSlot> timeSlots) {
        this.name = name;
        this.timeSlots = timeSlots;
    }

	public List<TimeSlot> getTimeSlots() {
		return timeSlots;
	}
	
	
	public boolean overlapsWith(int hour, int day) {
		LocalTime lhour = LocalTime.of(hour, 0);
        for (TimeSlot slot1 : timeSlots) {
        	LocalTime startTime = slot1.getStartTime();
        	LocalTime endTime = slot1.getEndTime();

            if(day == slot1.getDayOfWeek().getValue() &&
            		startTime.compareTo(lhour) <= 0 &&
            		endTime.compareTo(lhour) > 0)
            	return true;
        }
        return false;
	}
    @Override
    public String toString() {
        return "PersonalConstrain{" +
                "name='" + name + '\'' +
                ", timeSlots=" + timeSlots +
                '}';
    }

	public String getName() {
		return name;
	}

}
