package Utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CourseJsonFileGenerator {

    private static final String INPUT_FILE = "data/courses.json";
    private static final String OUTPUT_FILE = "data/courses2.json";

    public static void main(String[] args) {

        JSONArray jsonArray = readJSONArrayFromFile(INPUT_FILE);

        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray groupArray = (JSONArray) jsonObject.get("group");
            for (Object groupObj : groupArray) {
                JSONObject groupJson = (JSONObject) groupObj;
                JSONArray timeSlotArray = new JSONArray();
                int totalDuration = 0;
                List<String> daysUsed = new ArrayList<>();
                while (totalDuration < 6) {
                    JSONObject timeSlotJson = new JSONObject();
                    String day = getRandomDay(daysUsed);
                    timeSlotJson.put("day", day);
                    timeSlotJson.put("startTime", getRandomStartTime());
                    int duration = getRandomDuration();
                    totalDuration += duration;
                    if (totalDuration > 6) {
                        duration -= totalDuration - 6;
                    }
                    timeSlotJson.put("endTime", getEndTime((String) timeSlotJson.get("startTime"), duration));
                    timeSlotArray.add(timeSlotJson);
                }
                groupJson.put("time_slots", timeSlotArray);
            }
        }

        writeJSONArrayToFile(jsonArray, OUTPUT_FILE);
    }

    private static String getRandomDay(List<String> daysUsed) {
        String[] days = { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY" };
        Random random = new Random();
        String day = days[random.nextInt(days.length)];
        boolean dayExists = daysUsed.contains(day);
        if (dayExists) {
            return getRandomDay(daysUsed);
        } else {
            daysUsed.add(day);
            return day;
        }
    }

    private static String getRandomStartTime() {
        int hour = getRandomHour();
        return String.format("%02d:00", hour);
    }

    private static String getEndTime(String startTime, int duration) {
        int startHour = Integer.parseInt(startTime.substring(0, 2));
        int endHour = startHour + duration;
        return String.format("%02d:00", endHour);
    }

    private static int getRandomHour() {
        Random random = new Random();
        return random.nextInt(11) + 8;
    }

    private static int getRandomDuration() {
        Random random = new Random();
        return random.nextInt(3) + 2;
    }

    private static JSONArray readJSONArrayFromFile(String fileName) {
        JSONArray jsonArray = null;
        try {
            JSONParser parser = new JSONParser();
            jsonArray = (JSONArray) parser.parse(new FileReader(fileName));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static void writeJSONArrayToFile(JSONArray jsonArray, String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
