/*
 * Employee Shift Scheduler
 * Developing a weekly employee scheduling application using Java collections and randomization.
 * Collecting employee names, daily shift preferences, and unavailable days.
 * Generating schedules while enforcing shift capacity and weekly working-day restrictions.
 * Validating assignments and displaying schedules, workday summaries, and validation results.
 */

// Importing  libraries
import java.util.*;

public class EmployeeScheduler {

    // Defining weekly scheduling days and available shifts
    static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    };

    static final String[] SHIFTS = {
        "Morning", "Afternoon", "Evening"
    };

    // Setting shift capacity and employee working-day restrictions
    static final int SHIFT_CAPACITY = 2;
    static final int MAX_WORK_DAYS = 5;
    static final int UNAVAILABLE = -1;

    // Storing the generated schedule and employee workday totals
    static class ScheduleResult {
        List<List<List<Integer>>> schedule;
        int[] workDays;

        // Initializing schedule results and workday information
        ScheduleResult(List<List<List<Integer>>> schedule, int[] workDays) {
            this.schedule = schedule;
            this.workDays = workDays;
        }
    }

    // Creating an empty schedule for all days and shifts
    static List<List<List<Integer>>> createEmptySchedule() {
        List<List<List<Integer>>> schedule = new ArrayList<>();

        for (int day = 0; day < DAYS.length; day++) {
            List<List<Integer>> dailySchedule = new ArrayList<>();

            for (int shift = 0; shift < SHIFTS.length; shift++) {
                dailySchedule.add(new ArrayList<>());
            }

            schedule.add(dailySchedule);
        }

        return schedule;
    }

    // Generating a complete weekly schedule using randomized attempts
    static ScheduleResult createSchedule(int[][] preferences) {
        int employeeCount = preferences.length;

        for (int attempt = 0; attempt < 2000; attempt++) {
            List<List<List<Integer>>> schedule = createEmptySchedule();
            int[] workDays = new int[employeeCount];

            // Preparing and randomizing the weekly scheduling order
            List<Integer> dayOrder = new ArrayList<>();

            for (int day = 0; day < DAYS.length; day++) {
                dayOrder.add(day);
            }

            Collections.shuffle(dayOrder);
            boolean success = true;

            // Processing days in randomized order
            for (int day : dayOrder) {
                Set<Integer> assignedToday = new HashSet<>();
                List<Integer> shiftOrder = new ArrayList<>();

                // Preparing and randomizing the daily shift order
                for (int shift = 0; shift < SHIFTS.length; shift++) {
                    shiftOrder.add(shift);
                }

                Collections.shuffle(shiftOrder);

                // Filling each shift with two eligible employees
                for (int shift : shiftOrder) {
                    while (schedule.get(day).get(shift).size() < SHIFT_CAPACITY) {
                        List<Integer> candidates = new ArrayList<>();

                        // Identifying employees meeting availability restrictions
                        for (int employee = 0; employee < employeeCount; employee++) {
                            if (!assignedToday.contains(employee)
                                    && workDays[employee] < MAX_WORK_DAYS
                                    && preferences[employee][day] != UNAVAILABLE) {
                                candidates.add(employee);
                            }
                        }

                        // Stopping the current attempt when no candidates remain
                        if (candidates.isEmpty()) {
                            success = false;
                            break;
                        }

                        // Prioritizing preferred shifts among eligible employees
                        candidates.sort((first, second) -> {
                            boolean firstPref = (preferences[first][day] == shift);
                            boolean secondPref = (preferences[second][day] == shift);
                            return Boolean.compare(!firstPref, !secondPref);
                        });

                        // Assigning the selected employee and updating workday totals
                        int employee = candidates.get(0);
                        schedule.get(day).get(shift).add(employee);
                        assignedToday.add(employee);
                        workDays[employee]++;
                    }

                    // Ending shift processing when scheduling becomes impossible
                    if (!success) break;
                }

                // Ending day processing when an incomplete shift is encountered
                if (!success) break;
            }

            // Returning the schedule after successfully filling every shift
            if (success) {
                return new ScheduleResult(schedule, workDays);
            }
        }

        // Returning null after exhausting all scheduling attempts
        return null;
    }

    // Validating shift assignments, availability, and weekly workday limits
    static List<String> validateSchedule(
            List<List<List<Integer>>> schedule,
            int[][] preferences,
            int[] workDays) {

        List<String> errors = new ArrayList<>();

        // Resetting workday totals before validating assignments
        Arrays.fill(workDays, 0);

        // Checking every scheduled day and shift
        for (int day = 0; day < DAYS.length; day++) {
            Set<Integer> assignedToday = new HashSet<>();

            for (int shift = 0; shift < SHIFTS.length; shift++) {
                List<Integer> employees = schedule.get(day).get(shift);

                // Verifying the required number of employees per shift
                if (employees.size() != SHIFT_CAPACITY) {
                    errors.add(
                        DAYS[day] + " " + SHIFTS[shift] + ": "
                        + employees.size() + "/2 employees."
                    );
                }

                // Checking duplicate daily assignments and employee availability
                for (int employee : employees) {
                    if (!assignedToday.add(employee)) {
                        errors.add(
                            "Employee " + (employee + 1)
                            + " has multiple shifts on " + DAYS[day]
                        );
                    }

                    // Updating employee workday totals
                    workDays[employee]++;

                    // Detecting assignments on unavailable days
                    if (preferences[employee][day] == UNAVAILABLE) {
                        errors.add(
                            "Employee " + (employee + 1)
                            + " is unavailable on " + DAYS[day]
                        );
                    }
                }
            }
        }

        // Checking the maximum weekly working-day limit
        for (int employee = 0; employee < workDays.length; employee++) {
            if (workDays[employee] > MAX_WORK_DAYS) {
                errors.add(
                    "Employee " + (employee + 1)
                    + " exceeds five working days."
                );
            }
        }

        // Returning all detected validation errors
        return errors;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Displaying application instructions and scheduling rules
        System.out.println("     EMPLOYEE SHIFT SCHEDULER (JAVA)     ");
        System.out.println("Rules: 2 employees/shift | Max 5 days/week");
        System.out.println("Prefs: M=Morning, A=Afternoon, E=Evening, O=Unavailable\n");

        // Collecting and validating the total employee count
        int employeeCount = 0;

        while (true) {
            System.out.print("Enter number of employees (minimum 9): ");

            try {
                employeeCount = Integer.parseInt(scanner.nextLine().trim());

                if (employeeCount >= 9) break;

                System.out.println("Please enter at least 9 employees.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Initializing employee names and daily preference storage
        List<String> names = new ArrayList<>();
        int[][] preferences = new int[employeeCount][DAYS.length];

        // Collecting unique employee names and daily shift preferences
        for (int i = 0; i < employeeCount; i++) {
            String name;

            while (true) {
                System.out.print("\nEnter Employee " + (i + 1) + " name: ");
                name = scanner.nextLine().trim();

                // Checking for empty or duplicate employee names
                if (!name.isEmpty() && !names.contains(name)) break;

                System.out.println("Name cannot be empty or a duplicate.");
            }

            names.add(name);

            // Collecting valid shift preferences for each weekday
            for (int d = 0; d < DAYS.length; d++) {
                while (true) {
                    System.out.print(
                        "  " + name + ", " + DAYS[d] + " [M / A / E / O]: "
                    );

                    String choice = scanner.nextLine().trim().toUpperCase();

                    // Recording the selected shift or unavailable status
                    if (choice.equals("M")) {
                        preferences[i][d] = 0;
                        break;
                    }

                    if (choice.equals("A")) {
                        preferences[i][d] = 1;
                        break;
                    }

                    if (choice.equals("E")) {
                        preferences[i][d] = 2;
                        break;
                    }

                    if (choice.equals("O")) {
                        preferences[i][d] = UNAVAILABLE;
                        break;
                    }

                    System.out.println("Invalid choice. Enter M, A, E, or O.");
                }
            }
        }

        // Generating the weekly employee shift schedule
        System.out.println("Generating schedule...");
        ScheduleResult result = createSchedule(preferences);

        // Handling unsuccessful scheduling attempts
        if (result == null) {
            System.out.println(
                "[!] Could not generate a complete schedule with current availabilities."
            );
            scanner.close();
            return;
        }

        // Displaying the completed weekly shift schedule
        System.out.println("      FINAL WEEKLY SHIFT SCHEDULE        ");

        for (int day = 0; day < DAYS.length; day++) {
            System.out.println("[" + DAYS[day] + "]");

            for (int shift = 0; shift < SHIFTS.length; shift++) {
                List<Integer> emps = result.schedule.get(day).get(shift);
                List<String> empNames = new ArrayList<>();

                // Converting assigned employee indexes into names
                for (int e : emps) {
                    empNames.add(names.get(e));
                }

                // Displaying shift assignments and staffing capacity
                System.out.printf(
                    "  %-10s: %s (%d/2)%n",
                    SHIFTS[shift],
                    String.join(", ", empNames),
                    emps.size()
                );
            }
        }

        // Displaying employee workday summaries
        System.out.println("        EMPLOYEE WORKDAY SUMMARY         ");

        for (int i = 0; i < employeeCount; i++) {
            System.out.println(
                "  " + names.get(i) + ": "
                + result.workDays[i] + " working days"
            );
        }

        // Validating the completed schedule and recalculating workday totals
        List<String> errors = validateSchedule(
            result.schedule,
            preferences,
            result.workDays
        );

        // Displaying final scheduling validation results
        System.out.println("          SCHEDULING VALIDATION          ");

        if (errors.isEmpty()) {
            System.out.println("PASS: All 21 shifts have exactly 2 employees.");
            System.out.println("PASS: No employee works multiple shifts per day.");
            System.out.println("PASS: No employee works more than 5 days.");
            System.out.println("PASS: Unavailable days were successfully respected.");
        } else {
            // Reporting detected scheduling validation errors
            System.out.println("Validation errors detected:");

            for (String err : errors) {
                System.out.println("  - " + err);
            }
        }

        // Closing the input scanner
        scanner.close();
    }
}