package project;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Course {

	private static ArrayList<Course> allCourses = new ArrayList<Course>();

	private String courseId;
	private int group;
	private String nickname;


	private int codeIndex;
	private String name;
	private List<TimeSlot> timeSlots;
	private int penalty; //How much its hurt for not taking this course. Higher mean more hurt

	
	public Course(String name, int penalty, List<TimeSlot> timeSlots) {
    	this.name = name;
		this.timeSlots = timeSlots;
		this.penalty = penalty;

		this.codeIndex = allCourses.size();
		allCourses.add(this);
	}
	
    public Course(String courseId, int group, String name, String nickname, int penalty) {
    	this.courseId = courseId;
    	this.group = group;
    	this.name = name;
		this.timeSlots = new ArrayList<TimeSlot>();
		this.penalty = penalty;
		this.nickname = nickname;

		this.codeIndex = allCourses.size();
		allCourses.add(this);
	}

	public void addTimeSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
	    TimeSlot timeSlot = new TimeSlot(dayOfWeek, startTime, endTime);
	    this.timeSlots.add(timeSlot);
	}


	public String getName() {
		return name;
	}
	
	public String getNichname() {
		return this.nickname;
	}

	public List<TimeSlot> getTimeSlots() {
		return timeSlots;
	}

	public int getPenalty() {
		return penalty;
	}

	public int getIndex() {
		return codeIndex;
	}
	
	public int getGroup() {
		return this.group;
	}

	public String getCourseId() {
		return this.courseId;
	}
	

	public boolean overlapsWith(Course otherCourse) {
		if(this.courseId == otherCourse.courseId)
			return true;
		

		for (TimeSlot slot1 : timeSlots) {
			for (TimeSlot slot2 : otherCourse.getTimeSlots()) {
				if (slot1.overlaps(slot2)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean overlapsWith(PersonalConstraint personal_constrain) {
		for (TimeSlot slot1 : timeSlots) {
			for (TimeSlot slot2 : personal_constrain.getTimeSlots()) {
				if (slot1.overlaps(slot2)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean overlapsWith(List<Course> otherCourses) {
		if (otherCourses.size() == 0)
			return true;
		for(Course otherCourse: otherCourses)
			if(this.overlapsWith(otherCourse))
				return true;
		return false;
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


	public String toStringShort() {
		return "Course{" +
				"name='" + name + '\'' +
				", timeSlots=" + timeSlots +
				'}';
	}
	
	@Override
	public String toString() {
	    return "Course{" +
	            "codeIndex=" + codeIndex +
	            ", name='" + name + '\'' +
	            ", nickname='" + nickname + '\'' +
	            ", courseId='" + courseId + '\'' +
	            ", group=" + group +
	            ", timeSlots=" + timeSlots +
	            ", penalty=" + penalty +
	            '}';
	}


	//######################## STATIC METHODS #####################################
	/**
	 * Calculates the number of course schedule clashes in a list of courses.
	 *
	 * @return the number of course schedule clashes as a double value.
	 */
	public static double calculateNumClashes() {
		double numClashes = 0.0;
		for (int i = 0; i < allCourses.size(); i++) {
			for (int j = i + 1; j < allCourses.size(); j++) {
				Course course1 = allCourses.get(i);
				Course course2 = allCourses.get(j);
				if (course1.overlapsWith(course2)) {
					numClashes += 1.0;
				}
			}
		}
		return numClashes;
	}

	public static int totalPenalty() {
		int sum = 0;
		for (int i = 0; i < allCourses.size(); i++) 
			sum+= allCourses.get(i).getPenalty();
		return sum;

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Course> getAllCourses() {

		return  (ArrayList<Course>) allCourses.clone();
	}

	public static Course getCourseByIndex(int index) {
		return allCourses.get(index);
	}

}
