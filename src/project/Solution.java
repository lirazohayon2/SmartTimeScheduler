package project;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Solution implements Comparable<Solution>{


	private List<Course> chosenCourses; //only the course I TAKE

	// This value is the Value we want to minimize
	// minimize fitness meaning that this solution has better value = 
	// Good solution its a solution that has more courses (with high penalty) and less collisions "
	private double fitness; 

	//{0,1} | 0 not taking the course, 1 otherwise
	public enum  posValue {

		DONT_TAKE_COURSE(0),
		TAKE_COURSE(1);

		private final int value;

		posValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	//{0,1}^n | n is the TOTAL NUMBER OF COURSES <- Course.getAllcourses().size
	//The index are match to the courses index in Course Class.
	private posValue [] solutionPosition; 


	public Solution() {
		List<Course> allCourses = Course.getAllCourses();

		this.chosenCourses = new ArrayList<Course>();


		this.solutionPosition = new posValue[allCourses.size()];
		for (int i=0; i< this.solutionPosition.length; i++)
			this.solutionPosition[i] = posValue.DONT_TAKE_COURSE;

		// Shuffle the courses randomly to create a random solution
		Collections.shuffle(allCourses, new Random());

		// Add courses to the solution one by one, ensuring no collisions
		for (Course course : allCourses) {
			boolean overlaps = false;

			for (Course addedCourse : chosenCourses) {
				if (addedCourse.overlapsWith(course)) {
					overlaps = true;
					break;
				}
			}


			if (!overlaps) {
				this.chosenCourses.add(course);
				this.solutionPosition[course.getIndex()] = posValue.TAKE_COURSE;
			}
		}

		// Calculate the fitness of the solution
		fitness = calculateFitness();
	}

	public Solution(Solution other) {
		chosenCourses = new ArrayList<Course>();
		solutionPosition = new posValue[Course.getAllCourses().size()];

		for (int i=0; i< this.solutionPosition.length; i++)
			this.solutionPosition[i] = posValue.DONT_TAKE_COURSE;

		for (Course course : other.chosenCourses) {
			chosenCourses.add(course);
			this.solutionPosition[course.getIndex()] = posValue.TAKE_COURSE;
		}
		fitness = calculateFitness();
	}

	public Course getCourse(int index) {
		return chosenCourses.get(index);
	}

	public int getCoursesSize() {
		return chosenCourses.size();
	}

	public double getFitness() {
		return fitness;
	}

	public posValue getGenePosValue(int i) {
		return this.solutionPosition[i];
	}

	public void setGenePosValue(int i, posValue newValue) {
		if(newValue.getValue() == posValue.TAKE_COURSE.value) 
			this.chosenCourses.add(Course.getAllCourses().get(i));
		else
			this.chosenCourses.remove(Course.getAllCourses().get(i));

		this.solutionPosition[i] = newValue;		
		this.fitness = calculateFitness();
	}
	
	public boolean canAddCourse(int i) {
		Course chosenCourse = Course.getAllCourses().get(i);
		return ! chosenCourse.overlapsWith(chosenCourses);
	}

	// Calculate the penalty for Course clashes
	private double calculateFitness() {
		double penalty = 0.0;
		double numClashes = Course.calculateNumClashes();
		int numChosenCourses = chosenCourses.size();
		int totalNumCourses = Course.getAllCourses().size();

		// Calculate the penalty for not considering each Course
		for(int i=0; i<this.solutionPosition.length;i++) {
			if(this.solutionPosition[i].value ==posValue.DONT_TAKE_COURSE.value)
				penalty += Course.getCourseByIndex(i).getPenalty();
		}

		double totalPenalty = Course.totalPenalty();
		// Calculate the fitness based on the penalty, number of clashes, and number of chosen courses
		double fitness = 1.0 / (totalPenalty - penalty + 1.0);
		fitness *= 1.0 / (numClashes + 1.0);
		fitness /= (double)numChosenCourses / (double)totalNumCourses;

		return fitness;
	}
	
	
	
	@Override
	public int compareTo(Solution otherSolution) {
		if (this.fitness == otherSolution.fitness)
			return 0;

		if (this.fitness > otherSolution.fitness)
			return 1;
		else
			return -1;

	}
	
	public boolean hasSamePosition(Solution otherSolution) {
		boolean same_pos = true;
		for(int i=0; i<this.solutionPosition.length;i++) {
			if(this.solutionPosition[i].value != otherSolution.solutionPosition[i].value) {
				same_pos=false;
				break;
			}
		}
		return same_pos;
	}




	@Override
	public String toString() {
		int padding = 15;
		StringBuilder builder = new StringBuilder();
		builder.append("Fitness: ");
		builder.append(fitness);
		builder.append("\n");

		builder.append("Position: [");
		for(posValue pos:this.solutionPosition)
			builder.append(pos.value);
		builder.append("]\n");

		builder.append("Schedule:\n");
		for (int hour = PSOAlgorithm.START_HOUR; hour < PSOAlgorithm.START_HOUR + PSOAlgorithm.HOURS_PER_DAY; hour++) {
			if (hour == PSOAlgorithm.START_HOUR) {
				builder.append("              ");
				DayOfWeek[] days = DayOfWeek.values();

				for (int i=0; i<days.length;i++) {
				    int fix_i = i>0 ? i-1 : 6; //fix to start from sunday
				    builder.append(" |  ");
				    builder.append(String.format("%-" + (padding-1) + "s", days[fix_i].toString().substring(0, 3)));
				}
				builder.append(" |\n");
			}
			builder.append(String.format("%2d:00 - %2d:00 ", hour, hour + 1));
			for (int day = 0; day < PSOAlgorithm.DAYS_PER_WEEK; day++) {
				int fix_day = (day==0) ? 7 :day; //fix sunday

				builder.append(" | ");
				Course chosenCourse = null;
				for (Course course : chosenCourses) {
					if (course.overlapsWith(hour, fix_day)) {
						chosenCourse = course;
						break;
					}
				}
				if (chosenCourse != null) {
					String courseName = "#"+chosenCourse.getGroup()+"# "+chosenCourse.getNichname();
					if (courseName.length() < padding) {
					    int paddingLeft = (padding - courseName.length()) / 2;
					    int paddingRight = (padding - courseName.length() + 1) / 2;
					    for (int i = 0; i < paddingLeft; i++) {
					        builder.append(" ");
					    }
					    builder.append(courseName);
					    for (int i = 0; i < paddingRight; i++) {
					        builder.append(" ");
					    }
					} else {
					    builder.append(courseName.substring(0, padding));
					}
				} else {
					builder.append(" ".repeat(padding));
				}
			}
			builder.append(" |\n");
		}
		return builder.toString();
	}
	public String toString(ArrayList<PersonalConstraint> per_constrains) {
		int padding = 15;

		StringBuilder builder = new StringBuilder();
		builder.append("Fitness: ");
		builder.append(fitness);
		builder.append("\n");

		builder.append("Position: [");
		for(posValue pos:this.solutionPosition)
			builder.append(pos.value);
		builder.append("]\n");

		builder.append("Schedule:\n");
		for (int hour = PSOAlgorithm.START_HOUR; hour < PSOAlgorithm.START_HOUR + PSOAlgorithm.HOURS_PER_DAY; hour++) {
			if (hour == PSOAlgorithm.START_HOUR) {
				builder.append("              ");
				DayOfWeek[] days = DayOfWeek.values();

				for (int i=0; i<days.length;i++) {
				    int fix_i = i>0 ? i-1 : 6; //fix to start from sunday
				    builder.append(" |  ");
				    builder.append(String.format("%-" + (padding-1) + "s", days[fix_i].toString().substring(0, 3)));
				}
				builder.append(" |\n");
			}
			builder.append(String.format("%2d:00 - %2d:00 ", hour, hour + 1));
			for (int day = 0; day < PSOAlgorithm.DAYS_PER_WEEK; day++) {
				int fix_day = (day==0) ? 7 :day; //fix sunday

				builder.append(" | ");
				Course chosenCourse = null;
				for (Course course : chosenCourses) {
					if (course.overlapsWith(hour, fix_day)) {
						chosenCourse = course;
						break;
					}
				}

				PersonalConstraint persnoal_constrain = null;
				for (PersonalConstraint per : per_constrains) {
					if (per.overlapsWith(hour, fix_day)) {
						persnoal_constrain = per;
						break;
					}
				}


				if (chosenCourse != null || persnoal_constrain !=null) {
					String event_name = "";
					if(chosenCourse != null )
						event_name = "#"+chosenCourse.getGroup()+"# "+chosenCourse.getNichname();

					else
						event_name = persnoal_constrain.getName();
					if (event_name.length() < padding) {
					    int paddingLeft = (padding - event_name.length()) / 2;
					    int paddingRight = (padding - event_name.length() + 1) / 2;
					    for (int i = 0; i < paddingLeft; i++) {
					        builder.append(" ");
					    }
					    builder.append(event_name);
					    for (int i = 0; i < paddingRight; i++) {
					        builder.append(" ");
					    }
					} else {
					    builder.append(event_name.substring(0, padding));
					}
				} else {
					builder.append(" ".repeat(padding));
				}
			}
			builder.append(" |\n");
		}
		return builder.toString();
	}


}