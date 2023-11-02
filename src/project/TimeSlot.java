package project;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class TimeSlot {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + "-" + endTime;
    }

    
    /**
     * Determines if this TimeSlot overlaps with another TimeSlot.
     *
     * @param other the other TimeSlot to compare with.
     * @return true if this TimeSlot overlaps with the other TimeSlot, false otherwise.
     */
    public boolean overlaps(TimeSlot other) {
        if (!dayOfWeek.equals(other.getDayOfWeek())) {
            return false;
        }

        LocalTime otherStart = other.getStartTime();
        LocalTime otherEnd = other.getEndTime();

        boolean startsInside = startTime.compareTo(otherStart) >= 0 && startTime.compareTo(otherEnd) < 0;
        boolean endsInside = endTime.compareTo(otherStart) > 0 && endTime.compareTo(otherEnd) <= 0;
        boolean coversWholeSlot = startTime.compareTo(otherStart) <= 0 && endTime.compareTo(otherEnd) >= 0;

        return startsInside || endsInside || coversWholeSlot;
    }
}
