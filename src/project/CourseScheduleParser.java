package project;

import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CourseScheduleParser {
	public static ArrayList<Course> ParseCourse(String coursesFile) {
	    JSONParser parser = new JSONParser();
        // create a list to store the parsed courses
	    ArrayList<Course> parsedCourses = new ArrayList<Course>();
        
	    try (FileReader reader = new FileReader(coursesFile)) {
	        Object obj = parser.parse(reader);
	        JSONArray courses = (JSONArray) obj;

	        for (Object courseObj : courses) {
	            JSONObject course = (JSONObject) courseObj;
	            String courseId = (String) course.get("course_id");
	            String name = (String) course.get("name");
	            int penalty = Integer.parseInt((String) course.get("penalty"));
	            String nickname = (String) course.get("nickname"); 


	            JSONArray groupArray = (JSONArray) course.get("group");
	            for (Object groupObj : groupArray) {
	                JSONObject group = (JSONObject) groupObj;
	                int groupNumber = Integer.parseInt((String) group.get("group_number"));

	                JSONArray timeSlotArray = (JSONArray) group.get("time_slots");
                    Course parsedCourse = new Course(courseId, groupNumber, name, nickname, penalty);
	                for (Object timeSlotObj : timeSlotArray) {
	                    JSONObject timeSlot = (JSONObject) timeSlotObj;
	                    DayOfWeek dayOfWeek = DayOfWeek.valueOf((String) timeSlot.get("day"));
	                    LocalTime startTime = LocalTime.parse((String) timeSlot.get("startTime"));
	                    LocalTime endTime = LocalTime.parse((String) timeSlot.get("endTime"));

	                    // create a course object with the parsed data
	                    parsedCourse.addTimeSlot(dayOfWeek, startTime, endTime);

	                    // add the course object to the list
	                }
                    parsedCourses.add(parsedCourse);
	            }
	        }

	        // Print Courses
//	        for (Course parsedCourse : parsedCourses) {
//	            System.out.println(parsedCourse);
//	        }
	    } catch (IOException | ParseException e) {
	        e.printStackTrace();
	    }
		return parsedCourses;
	}
}
