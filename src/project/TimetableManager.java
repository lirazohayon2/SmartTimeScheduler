package project;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Scanner;

public class TimetableManager {
    private static final String COURSES_FILE = "data/courses.json";
    private static final String PERSONAL_CONSTRAINTS_FILE = "data/personal_constraints.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Schedule Manager!");
        System.out.print("Please enter your name: ");
        String studentName = scanner.nextLine();
        System.out.println("Hello, " + studentName + "!");
        System.out.println("Creating Your Schedule :) Please Wait...");


        TimetableScheduler scheduler = new TimetableScheduler(studentName, COURSES_FILE, PERSONAL_CONSTRAINTS_FILE);
        System.out.println("Done :)");

        int choice = -1;

        while (choice != 0) {
            System.out.println("\n>>>Please choose an option:");
            System.out.println("1. Show all courses");
            System.out.println("2. Show best schedule");
            System.out.println("3. Show top 4 schedules");
            System.out.println("");
            System.out.println("4. Add personal constraint");
            System.out.println("5. Remove personal constraint");
            System.out.println("6. Show personal constraints");
            System.out.println("");
            System.out.println("7. Show best schedule WITH CONSTRAINT");
            System.out.println("8. Show top 4 schedules WITH CONSTRAINT");
            System.out.println("");
            System.out.println("0. Exit");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline character
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // consume the invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println(scheduler.getAllCourses());
                    break;
                case 2:
                    System.out.println(scheduler.getBestSchedule());
                    break;
                case 3:
                    System.out.println(scheduler.getTopSchedules(4));
                    break;
                case 4:
                    addPersonalConstraint(scanner, scheduler);
                    break;
                case 5:
                    System.out.println("Enter constraint name to remove:");
                    String constraintName = scanner.nextLine();
                    if (scheduler.removePersonalConstraint(constraintName)) {
                        System.out.println("Personal constraint removed successfully.");
                    } else {
                        System.out.println("Personal constraint not found.");
                    }
                    break;
                case 6:
                    System.out.println(scheduler.getPersonalConstraints());
                    break;
                case 7:
                    System.out.println(scheduler.getTopSchedulesWithConstraint(1));
                    break;
                case 8:
                    System.out.println(scheduler.getTopSchedulesWithConstraint(4));
                    break;
                case 0:
                    System.out.println("Goodbye, " + studentName + "!");
                    break;
                default:
                    System.out.println("Invalid input. Please enter a valid number.");
                    break;
            }
        }

        scanner.close();
    }

    private static void addPersonalConstraint(Scanner scanner, TimetableScheduler scheduler) {
        System.out.println("Enter constraint name:");
        String name = scanner.nextLine();
        System.out.println("Enter day of the week (e.g. MONDAY):");
        String dayString = scanner.nextLine();
        System.out.println("Enter start time (hh:mm):");
        String startTimeString = scanner.nextLine();
        System.out.println("Enter end time (hh:mm):");
        String endTimeString = scanner.nextLine();
        

       
        try {
            TimeSlot timeslot = new TimeSlot(DayOfWeek.valueOf(dayString),
                    LocalTime.parse(startTimeString), LocalTime.parse(endTimeString));
            
            PersonalConstraint constraint = new PersonalConstraint(name, Arrays.asList(timeslot));
            scheduler.addPersonalConstraint(constraint);
            System.out.println("Personal constraint added successfully.");
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
        }
    }


}
