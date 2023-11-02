package project;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableScheduler {
	private Student student;

	private final ArrayList<Course> courses;
	private ArrayList<PersonalConstraint> personalConstraints;
	private final PSOAlgorithm pso;


	public TimetableScheduler(String studentName, String coursesFile, String personalConstraintsFile) {
		this.student =  new Student("studentName"); 
		//Create courses
		this.courses = initCoursesFile(coursesFile);
		this.personalConstraints = new ArrayList<PersonalConstraint>();
		for (Course course : courses)
			this.student.addCourse(course);


		// Initialize the PSO algorithm with the parameters and the fitness function
		this.pso = new PSOAlgorithm(this.courses);

		// Run the PSO algorithm and get the best schedule
		pso.run();

	}

	public String getBestSchedule() {
		return  pso.getBestSolution().toString();
	}
	public String getTopSchedules(int top_num) {
		StringBuilder res = new StringBuilder();
		List<Solution> top_solutions = getTopDiffSolutions(pso.getAllSolutions(),top_num);
		int num = 1;
		res.append("\n ################ START - Top Solutions ######################\n");

		for(Solution sol : top_solutions) {
			res.append("\n ################ Solution "+ (num++) +" >:\n");
			res.append(sol.toString());
		}
		res.append("\n ################ END - Top Solutions ######################\n");
		return res.toString();
	}
	public String getAllCourses() {
		Map<String, Map<String, List<Course>>> coursesGrouped = new HashMap<>();

		for (Course course : this.courses) {
			String courseId = course.getCourseId();
			String courseName = course.getName();
			Map<String, List<Course>> coursesByName = coursesGrouped.getOrDefault(courseName, new HashMap<>());
			List<Course> coursesById = coursesByName.getOrDefault(courseId, new ArrayList<>());
			coursesById.add(course);
			coursesByName.put(courseId, coursesById);
			coursesGrouped.put(courseName, coursesByName);
		}

		StringBuilder res = new StringBuilder();
		res.append("\n ################ START - All Courses ######################\n");

		for (String courseName : coursesGrouped.keySet()) {
			res.append(String.format("%s", courseName));

			Map<String, List<Course>> coursesByName = coursesGrouped.get(courseName);

			for (String courseId : coursesByName.keySet()) {
				res.append(String.format("(%s):\n", courseId));

				List<Course> coursesById = coursesByName.get(courseId);
				res.append(String.format("Nichname: '%s'\n", coursesById.get(0).getNichname()));
				res.append("Groups:\n");

				for (Course course : coursesById) {
					res.append(String.format("\tGroup %d\n", course.getGroup()));
					for (TimeSlot timeSlot : course.getTimeSlots()) {
						res.append(String.format("\t\t%s\n", timeSlot));
					}
				}
			}
			res.append("\n");
		}

		res.append("\n ################ END - All Courses ######################\n");

		return res.toString();
	}
	public String getPersonalConstraints() {
		StringBuilder sb = new StringBuilder();
		sb.append("Personal Constraints:\n");
		if (personalConstraints.isEmpty()) {
			sb.append("None\n");
		} else {
			for (PersonalConstraint constraint : personalConstraints) {
				for(TimeSlot timeslot: constraint.getTimeSlots()) {
					sb.append("- ").append(constraint.getName()).append(":\n");
					sb.append("    Day: ").append(timeslot.getDayOfWeek()).append("\n");
					sb.append("    Start Time: ").append(timeslot.getStartTime()).append("\n");
					sb.append("    End Time: ").append(timeslot.getEndTime()).append("\n");
				}
			}
		}
		return sb.toString();
	}


	public void addPersonalConstraint(PersonalConstraint constraint) {
		personalConstraints.add(constraint);		
	}
	public boolean removePersonalConstraint(String constraintName) {
		for (PersonalConstraint constraint : personalConstraints) {
			if (constraint.getName().equals(constraintName)) {
				personalConstraints.remove(constraint);
				return true;
			}
		}
		return false;
	}
	

	public String getTopSchedulesWithConstraint(int top_number) {
		// Creating Personal Constraints on the schedule
		List<Solution> solutionsWithConstrain = filterSolutions(this.personalConstraints, pso.getAllSolutions());

		List<Solution> top_solutions_with_personal = getTopDiffSolutions(solutionsWithConstrain, top_number);

		StringBuilder sb = new StringBuilder();
		int num = 1;
		sb.append("\n ################ START - Top Solutions WITH PERSONAL CONSTRAINT ######################\n");

		for(Solution sol : top_solutions_with_personal) {
			sb.append("\n ################ Special Solution "+ (num++) +" >:\n");
			sb.append(sol.toString(this.personalConstraints));
		}
		sb.append("\n ################ END - Top Solutions WITH PERSONAL CONSTRAINT ######################\n");
		return sb.toString();
	}

	private static ArrayList<Course> initCoursesFile(String coursesFile){
		return CourseScheduleParser.ParseCourse(coursesFile);
	}
	private static List<Solution> getTopDiffSolutions(List<Solution> solutions, int top_num) {
		// Reset the lists
		ArrayList<Solution> listOfBestSolutions = new ArrayList<Solution>();


		int count = 0, i=0;

		Collections.sort(solutions);
		while(count<top_num && i < solutions.size())
		{
			boolean already_exist = false;
			Solution currentSolution = solutions.get(i); 

			if(i>0) {
				for(Solution prevSol : listOfBestSolutions) {
					if(currentSolution.hasSamePosition(prevSol)) {
						already_exist = true;
						break;
					}
				}

			}

			if(!already_exist) {
				listOfBestSolutions.add(currentSolution);
				count++;
			}
			i++;
		}

		return listOfBestSolutions;
	}
	private static List<Solution> filterSolutions(ArrayList<PersonalConstraint> per_constrains,
			List<Solution> allSolutions) {
		List<Solution> filtered_solutions = new ArrayList<Solution>();
		for(Solution sol : allSolutions) {
			boolean no_collisions = true; 
			for(int i = 0; i< sol.getCoursesSize() && no_collisions ;i++) {
				for (PersonalConstraint per : per_constrains)
					if(sol.getCourse(i).overlapsWith(per)) {
						no_collisions = false;
						break;
					}
			}
			if(no_collisions)
				filtered_solutions.add(sol);
		}

		return filtered_solutions;	
	}
}
