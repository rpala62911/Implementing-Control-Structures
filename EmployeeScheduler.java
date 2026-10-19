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


    static int[] parsePreference(String value) {
        String[] codes = value.trim().toUpperCase(Locale.ROOT).replace(" ", "").split(",", -1);
        if (codes.length == 1 && codes[0].equals("O")) return null;
        if (codes.length != 1 && codes.length != 3)
            throw new IllegalArgumentException("Enter one shift, all three ranked shifts, or O.");
        int[] ranking = new int[codes.length];
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < codes.length; i++) {
            int shift = codes[i].equals("M") ? 0 : codes[i].equals("A") ? 1
                : codes[i].equals("E") ? 2 : -1;
            if (shift < 0 || !seen.add(shift))
                throw new IllegalArgumentException("Use unique M, A, E codes, or O.");
            ranking[i] = shift;
        }
        return ranking;
    }

    static int preferenceRank(int[] ranking, int shift) {
        for (int i = 0; i < ranking.length; i++)
            if (ranking[i] == shift) return i;
        return 1; // Unlisted shifts tie after a single first choice.
    }

    static boolean containsNameIgnoreCase(List<String> names, String candidate) {
        for (String name : names)
            if (name.equalsIgnoreCase(candidate)) return true;
        return false;
    }

    static List<List<Integer>> bestDailyShifts(
            List<Integer> employees, int day, int[][][] preferences) {
        int[] order = employees.stream().mapToInt(Integer::intValue).toArray();
        int[] best = order.clone();
        int[] bestScore = {Integer.MAX_VALUE};
        chooseDailyOrder(order, 0, day, preferences, best, bestScore);
        List<List<Integer>> result = new ArrayList<>();
        for (int shift = 0; shift < 3; shift++)
            result.add(new ArrayList<>(Arrays.asList(best[shift * 2], best[shift * 2 + 1])));
        return result;
    }

    // At most 720 orders: choose the best ranks for today's six employees.
    static void chooseDailyOrder(int[] order, int position, int day,
            int[][][] preferences, int[] best, int[] bestScore) {
        if (position == order.length) {
            int score = 0;
            for (int i = 0; i < order.length; i++)
                score += preferenceRank(preferences[order[i]][day], i / 2);
            if (score < bestScore[0]) {
                bestScore[0] = score;
                System.arraycopy(order, 0, best, 0, order.length);
            }
            return;
        }
        for (int i = position; i < order.length; i++) {
            int temp = order[position]; order[position] = order[i]; order[i] = temp;
            chooseDailyOrder(order, position + 1, day, preferences, best, bestScore);
            temp = order[position]; order[position] = order[i]; order[i] = temp;
        }
    }

    static ScheduleResult repairSchedule(int[][][] preferences) {
        List<Set<Integer>> attendance = new ArrayList<>();
        List<Set<Integer>> worked = new ArrayList<>();
        for (int day = 0; day < DAYS.length; day++) attendance.add(new HashSet<>());
        for (int e = 0; e < preferences.length; e++) worked.add(new HashSet<>());
        for (int day = 0; day < DAYS.length; day++) {
            while (attendance.get(day).size() < 6) {
                if (!fillDay(day, preferences, attendance, worked,
                        new HashSet<>(), new HashSet<>())) return null;
            }
        }
        List<List<List<Integer>>> schedule = createEmptySchedule();
        int[] workDays = new int[preferences.length];
        for (int day = 0; day < DAYS.length; day++) {
            schedule.set(day, bestDailyShifts(new ArrayList<>(attendance.get(day)),
                day, preferences));
            for (int e : attendance.get(day)) workDays[e]++;
        }
        return new ScheduleResult(schedule, workDays);
    }

    // Find a replacement chain: move a fully booked employee only after
    // another employee has filled their previous assignment.
    static boolean fillDay(int day, int[][][] preferences,
            List<Set<Integer>> attendance, List<Set<Integer>> worked,
            Set<Integer> visitedDays, Set<Integer> visitedEmployees) {
        visitedDays.add(day);
        List<Integer> candidates = new ArrayList<>();
        for (int e = 0; e < preferences.length; e++)
            if (preferences[e][day] != null && !attendance.get(day).contains(e))
                candidates.add(e);
        Collections.shuffle(candidates);
        candidates.sort(Comparator.comparingInt(e -> worked.get(e).size()));
        for (int employee : candidates) {
            if (!visitedEmployees.add(employee)) continue;
            if (worked.get(employee).size() < MAX_WORK_DAYS) {
                attendance.get(day).add(employee);
                worked.get(employee).add(day);
                return true;
            }
            for (int previousDay : new ArrayList<>(worked.get(employee))) {
                if (!visitedDays.contains(previousDay) && fillDay(previousDay,
                        preferences, attendance, worked, visitedDays, visitedEmployees)) {
                    attendance.get(previousDay).remove(employee);
                    worked.get(employee).remove(previousDay);
                    attendance.get(day).add(employee);
                    worked.get(employee).add(day);
                    return true;
                }
            }
        }
        return false;
    }

    // Generating a complete weekly schedule using randomized attempts
    static ScheduleResult createSchedule(int[][][] preferences) {
        int employeeCount = preferences.length;

        for (int attempt = 0; attempt < 100; attempt++) {
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
                                    && preferences[employee][day] != null) {
                                candidates.add(employee);
                            }
                        }

                        // Stopping the current attempt when no candidates remain
                        if (candidates.isEmpty()) {
                            success = false;
                            break;
                        }

                        // Prioritizing preferred shifts among eligible employees
                        Collections.shuffle(candidates);
                        candidates.sort(Comparator
                            .comparingInt((Integer e) -> preferenceRank(preferences[e][day], shift))
                            .thenComparingInt(e -> workDays[e]));

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
                for (int day = 0; day < DAYS.length; day++) {
                    List<Integer> employees = new ArrayList<>();
                    for (List<Integer> shift : schedule.get(day)) employees.addAll(shift);
                    schedule.set(day, bestDailyShifts(employees, day, preferences));
                }
                return new ScheduleResult(schedule, workDays);
            }
        }

        // Repair earlier choices when randomized attempts cannot finish the week.
        return repairSchedule(preferences);
    }

    // Validating shift assignments, availability, and weekly workday limits
    static List<String> validateSchedule(
            List<List<List<Integer>>> schedule,
            int[][][] preferences,
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
                    if (preferences[employee][day] == null) {
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

        System.out.println("Bonus: enter all three shifts in priority order, e.g. M,E,A.");
        System.out.println("Single choices are allowed; other shifts tie as fallbacks.");
        System.out.println("Each shift has capacity two (the required minimum).");

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
        int[][][] preferences = new int[employeeCount][DAYS.length][];

        // Collecting unique employee names and daily shift preferences
        for (int i = 0; i < employeeCount; i++) {
            String name;

            while (true) {
                System.out.print("\nEnter Employee " + (i + 1) + " name: ");
                name = scanner.nextLine().trim();

                // Checking for empty or duplicate employee names
                if (!name.isEmpty() && !containsNameIgnoreCase(names, name)) break;

                System.out.println("Name cannot be empty or a duplicate.");
            }

            names.add(name);

            // Collecting valid shift preferences for each weekday
            for (int d = 0; d < DAYS.length; d++) {
                while (true) {
                    System.out.print(
                        "  " + name + ", " + DAYS[d] + " [M/A/E, ranked M,E,A, or O]: "
                    );

                    String choice = scanner.nextLine().trim().toUpperCase();

                    try {
                        preferences[i][d] = parsePreference(choice);
                        break;
                    } catch (IllegalArgumentException error) {
                        System.out.println(error.getMessage());
                    }
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

        System.out.println("\nRANKED PREFERENCE AND CONFLICT SUMMARY");
        int[] ranks = new int[3];
        for (int day = 0; day < DAYS.length; day++) {
            for (int shift = 0; shift < SHIFTS.length; shift++) {
                for (int employee : result.schedule.get(day).get(shift)) {
                    int rank = preferenceRank(preferences[employee][day], shift);
                    ranks[rank]++;
                    if (rank > 0) {
                        System.out.println("  " + names.get(employee) + ", " + DAYS[day]
                            + ": " + SHIFTS[preferences[employee][day][0]] + " requested -> "
                            + SHIFTS[shift] + " assigned (fallback rank " + (rank + 1) + ")");
                    }
                }
            }
        }
        System.out.println("Rank 1: " + ranks[0] + " | Rank 2/fallback: "
            + ranks[1] + " | Rank 3: " + ranks[2]);
        System.out.println("Ranks are improved for each day's selected employees; "
            + "global optimality is not claimed.");

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
