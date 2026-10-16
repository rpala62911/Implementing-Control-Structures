// Employee Shift Scheduler (Java Version)

import java.util.*;

public class EmployeeScheduler {

    static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    };

    static final String[] SHIFTS = {
        "Morning", "Afternoon", "Evening"
    };

    static final int SHIFT_CAPACITY = 2;
    static final int MAX_WORK_DAYS = 5;
    static final int UNAVAILABLE = -1;

    static class ScheduleResult {
        List<List<List<Integer>>> schedule;
        int[] workDays;

        ScheduleResult(List<List<List<Integer>>> schedule, int[] workDays) {
            this.schedule = schedule;
            this.workDays = workDays;
        }
    }

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

    static ScheduleResult createSchedule(int[][] preferences) {
        int employeeCount = preferences.length;

        for (int attempt = 0; attempt < 2000; attempt++) {
            List<List<List<Integer>>> schedule = createEmptySchedule();
            int[] workDays = new int[employeeCount];

            List<Integer> dayOrder = new ArrayList<>();
            for (int day = 0; day < DAYS.length; day++) {
                dayOrder.add(day);
            }
            Collections.shuffle(dayOrder);

            boolean success = true;

            for (int day : dayOrder) {
                Set<Integer> assignedToday = new HashSet<>();
                List<Integer> shiftOrder = new ArrayList<>();
                for (int shift = 0; shift < SHIFTS.length; shift++) {
                    shiftOrder.add(shift);
                }
                Collections.shuffle(shiftOrder);

                for (int shift : shiftOrder) {
                    while (schedule.get(day).get(shift).size() < SHIFT_CAPACITY) {
                        List<Integer> candidates = new ArrayList<>();

                        for (int employee = 0; employee < employeeCount; employee++) {
                            if (!assignedToday.contains(employee)
                                    && workDays[employee] < MAX_WORK_DAYS
                                    && preferences[employee][day] != UNAVAILABLE) {
                                candidates.add(employee);
                            }
                        }

                        if (candidates.isEmpty()) {
                            success = false;
                            break;
                        }

                        // Prioritize employee shift preferences
                        candidates.sort((first, second) -> {
                            boolean firstPref = (preferences[first][day] == shift);
                            boolean secondPref = (preferences[second][day] == shift);
                            return Boolean.compare(!firstPref, !secondPref);
                        });

                        int employee = candidates.get(0);
                        schedule.get(day).get(shift).add(employee);
                        assignedToday.add(employee);
                        workDays[employee]++;
                    }
                    if (!success) break;
                }
                if (!success) break;
            }

            if (success) {
                return new ScheduleResult(schedule, workDays);
            }
        }
        return null;
    }

    static List<String> validateSchedule(List<List<List<Integer>>> schedule, int[][] preferences, int[] workDays) {
        List<String> errors = new ArrayList<>();
        Arrays.fill(workDays, 0);

        for (int day = 0; day < DAYS.length; day++) {
            Set<Integer> assignedToday = new HashSet<>();

            for (int shift = 0; shift < SHIFTS.length; shift++) {
                List<Integer> employees = schedule.get(day).get(shift);

                if (employees.size() != SHIFT_CAPACITY) {
                    errors.add(DAYS[day] + " " + SHIFTS[shift] + ": " + employees.size() + "/2 employees.");
                }

                for (int employee : employees) {
                    if (!assignedToday.add(employee)) {
                        errors.add("Employee " + (employee + 1) + " has multiple shifts on " + DAYS[day]);
                    }
                    workDays[employee]++;

                    if (preferences[employee][day] == UNAVAILABLE) {
                        errors.add("Employee " + (employee + 1) + " is unavailable on " + DAYS[day]);
                    }
                }
            }
        }

        for (int employee = 0; employee < workDays.length; employee++) {
            if (workDays[employee] > MAX_WORK_DAYS) {
                errors.add("Employee " + (employee + 1) + " exceeds five working days.");
            }
        }

        return errors;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("     EMPLOYEE SHIFT SCHEDULER (JAVA)     ");
        System.out.println("Rules: 2 employees/shift | Max 5 days/week");
        System.out.println("Prefs: M=Morning, A=Afternoon, E=Evening, O=Unavailable\n");

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

        List<String> names = new ArrayList<>();
        int[][] preferences = new int[employeeCount][DAYS.length];

        for (int i = 0; i < employeeCount; i++) {
            String name;
            while (true) {
                System.out.print("\nEnter Employee " + (i + 1) + " name: ");
                name = scanner.nextLine().trim();
                if (!name.isEmpty() && !names.contains(name)) break;
                System.out.println("Name cannot be empty or a duplicate.");
            }
            names.add(name);

            for (int d = 0; d < DAYS.length; d++) {
                while (true) {
                    System.out.print("  " + name + ", " + DAYS[d] + " [M / A / E / O]: ");
                    String choice = scanner.nextLine().trim().toUpperCase();
                    if (choice.equals("M")) { preferences[i][d] = 0; break; }
                    if (choice.equals("A")) { preferences[i][d] = 1; break; }
                    if (choice.equals("E")) { preferences[i][d] = 2; break; }
                    if (choice.equals("O")) { preferences[i][d] = UNAVAILABLE; break; }
                    System.out.println("Invalid choice. Enter M, A, E, or O.");
                }
            }
        }

        System.out.println("Generating schedule...");
        ScheduleResult result = createSchedule(preferences);

        if (result == null) {
            System.out.println("[!] Could not generate a complete schedule with current availabilities.");
            return;
        }

        System.out.println("      FINAL WEEKLY SHIFT SCHEDULE        ");
        for (int day = 0; day < DAYS.length; day++) {
            System.out.println("[" + DAYS[day] + "]");
            for (int shift = 0; shift < SHIFTS.length; shift++) {
                List<Integer> emps = result.schedule.get(day).get(shift);
                List<String> empNames = new ArrayList<>();
                for (int e : emps) empNames.add(names.get(e));
                System.out.printf("  %-10s: %s (%d/2)%n", SHIFTS[shift], String.join(", ", empNames), emps.size());
            }
        }

        System.out.println("        EMPLOYEE WORKDAY SUMMARY         ");
        for (int i = 0; i < employeeCount; i++) {
            System.out.println("  " + names.get(i) + ": " + result.workDays[i] + " working days");
        }

        List<String> errors = validateSchedule(result.schedule, preferences, result.workDays);
        System.out.println("          SCHEDULING VALIDATION          ");
        if (errors.isEmpty()) {
            System.out.println("PASS: All 21 shifts have exactly 2 employees.");
            System.out.println("PASS: No employee works multiple shifts per day.");
            System.out.println("PASS: No employee works more than 5 days.");
            System.out.println("PASS: Unavailable days were successfully respected.");
        } else {
            System.out.println("Validation errors detected:");
            for (String err : errors) {
                System.out.println("  - " + err);
            }
        }
        scanner.close();
    }
}