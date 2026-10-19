import java.util.*;

public class EmployeeSchedulerTest {
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static boolean feasible(int[][][] preferences) {
        for (int mask = 1; mask < 128; mask++) {
            int supply = 0;
            for (int[][] employee : preferences) {
                int available = 0;
                for (int d = 0; d < 7; d++)
                    if ((mask & (1 << d)) != 0 && employee[d] != null) available++;
                supply += Math.min(5, available);
            }
            if (supply < 6 * Integer.bitCount(mask)) return false;
        }
        return true;
    }

    static void validate(EmployeeScheduler.ScheduleResult result, int[][][] prefs) {
        check(result != null, "Missing feasible schedule");
        check(EmployeeScheduler.validateSchedule(result.schedule, prefs, result.workDays)
            .isEmpty(), "Constraint violation");
        check(Arrays.stream(result.workDays).sum() == 42, "Expected 42 assignments");
        for (List<List<Integer>> day : result.schedule) {
            Set<Integer> people = new HashSet<>();
            for (List<Integer> shift : day) {
                check(shift.size() == 2, "Shift staffing");
                for (int e : shift) check(people.add(e), "Duplicate daily assignment");
            }
        }
    }

    public static void main(String[] args) {
        int[][][] same = new int[9][7][];
        for (int e = 0; e < 9; e++)
            for (int d = 0; d < 7; d++) same[e][d] = new int[]{0, 2, 1};
        for (int run = 0; run < 30; run++)
            validate(EmployeeScheduler.createSchedule(same), same);
        System.out.println("PASS: 30 runs with identical first choices");

        Random random = new Random(632);
        for (int run = 0; run < 120; run++) {
            int[][][] prefs = new int[8 + random.nextInt(7)][7][];
            for (int e = 0; e < prefs.length; e++)
                for (int d = 0; d < 7; d++)
                    if (random.nextDouble() > 0.24) {
                        List<Integer> ranks = new ArrayList<>(Arrays.asList(0, 1, 2));
                        Collections.shuffle(ranks, random);
                        prefs[e][d] = ranks.stream().mapToInt(Integer::intValue).toArray();
                    }
            EmployeeScheduler.ScheduleResult result = EmployeeScheduler.repairSchedule(prefs);
            check((result != null) == feasible(prefs), "Feasibility mismatch " + run);
            if (result != null) validate(result, prefs);
        }
        System.out.println("PASS: 120 availability cases match independent capacity test");

        check(Arrays.equals(EmployeeScheduler.parsePreference(" e, m, a "),
            new int[]{2, 0, 1}), "Rank parsing");
        check(EmployeeScheduler.parsePreference("o") == null, "Unavailable parsing");
        for (String input : new String[]{"", "M,M,E", "M,A", "O,M,E", "M,A,E,", "X"}) {
            boolean rejected = false;
            try { EmployeeScheduler.parsePreference(input); }
            catch (IllegalArgumentException expected) { rejected = true; }
            check(rejected, "Invalid input accepted: " + input);
        }
        check(EmployeeScheduler.containsNameIgnoreCase(Arrays.asList("Arjun"), "aRjUn"),
            "Case-insensitive duplicate");
        System.out.println("PASS: valid rankings, invalid rankings, duplicate names");

        EmployeeScheduler.ScheduleResult broken = EmployeeScheduler.createSchedule(same);
        broken.schedule.get(0).get(1).set(0, broken.schedule.get(0).get(0).get(0));
        check(!EmployeeScheduler.validateSchedule(broken.schedule, same, broken.workDays)
            .isEmpty(), "Validator missed duplicate");
        System.out.println("PASS: validator detects an intentionally broken schedule");
    }
}

