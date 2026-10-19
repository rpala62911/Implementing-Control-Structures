"""
Developing a weekly employee shift scheduling system using Python randomization and preference-based assignment.
Collecting employee names and daily availability preferences for morning, afternoon, and evening shifts.
Generating a schedule while maintaining shift capacity, daily assignment limits, and weekly working-day restrictions.
Validating the completed schedule and displaying employee workday totals, preferred assignments, and scheduling results.
"""

# Employee Shift Scheduler

# Importing Library
import random
from itertools import permutations

DAYS = [
    "Monday", "Tuesday", "Wednesday", "Thursday",
    "Friday", "Saturday", "Sunday"
]

SHIFTS = ["Morning", "Afternoon", "Evening"]
SHIFT_CAPACITY = 2
MAX_WORK_DAYS = 5
UNAVAILABLE = -1


def parse_preference(value):
    """Accept a single choice, a full ranking such as M,E,A, or O."""
    codes = value.strip().upper().replace(" ", "").split(",")
    if codes == ["O"]:
        return UNAVAILABLE
    choices = {"M": 0, "A": 1, "E": 2}
    if len(codes) not in (1, 3) or len(set(codes)) != len(codes):
        raise ValueError("Enter one shift, all three ranked shifts, or O.")
    if any(code not in choices for code in codes):
        raise ValueError("Use M, A, E, or O.")
    return [choices[code] for code in codes]


def preference_rank(preference, shift):
    # Unlisted shifts tie as fallback choices after a single first preference.
    return preference.index(shift) if shift in preference else 1


def best_daily_shifts(employees, day, preferences):
    """Try the 720 possible orders for six employees, keeping the best ranks."""
    best = None
    best_score = None
    for order in permutations(employees):
        score = sum(preference_rank(preferences[e][day], i // 2)
                    for i, e in enumerate(order))
        if best_score is None or score < best_score:
            best_score = score
            best = [list(order[i:i + 2]) for i in range(0, 6, 2)]
    return best


def repair_schedule(preferences):
    """Find feasible attendance by moving assignments along replacement chains."""
    attendance = [set() for _ in DAYS]
    worked = [set() for _ in preferences]

    def fill_day(day, visited_days, visited_employees):
        visited_days.add(day)
        candidates = [e for e in range(len(preferences))
                      if preferences[e][day] != UNAVAILABLE
                      and e not in attendance[day]]
        random.shuffle(candidates)
        candidates.sort(key=lambda e: len(worked[e]))
        for employee in candidates:
            if employee in visited_employees:
                continue
            visited_employees.add(employee)
            if len(worked[employee]) < MAX_WORK_DAYS:
                attendance[day].add(employee)
                worked[employee].add(day)
                return True
            # Replace an earlier assignment before moving this employee here.
            for previous_day in sorted(worked[employee]):
                if previous_day not in visited_days and fill_day(
                        previous_day, visited_days, visited_employees):
                    attendance[previous_day].remove(employee)
                    worked[employee].remove(previous_day)
                    attendance[day].add(employee)
                    worked[employee].add(day)
                    return True
        return False

    for day in range(len(DAYS)):
        while len(attendance[day]) < 6:
            if not fill_day(day, set(), set()):
                return None
    return [best_daily_shifts(list(attendance[d]), d, preferences)
            for d in range(len(DAYS))]


def create_schedule(preferences):
    # Preparing employee counts and schedule information
    employee_count = len(preferences)

    # Repeating randomized attempts to generate a complete schedule
    for attempt in range(100):
        schedule = [[[] for _ in SHIFTS] for _ in DAYS]
        work_days = [0] * employee_count
        day_order = list(range(len(DAYS)))
        random.shuffle(day_order)
        success = True

        # Scheduling days in a randomized order
        for day in day_order:
            assigned_today = set()
            shift_order = list(range(len(SHIFTS)))
            random.shuffle(shift_order)

            # Filling each shift with two different employees
            for shift in shift_order:
                while len(schedule[day][shift]) < SHIFT_CAPACITY:
                    candidates = [
                        employee
                        for employee in range(employee_count)
                        if employee not in assigned_today
                        and work_days[employee] < MAX_WORK_DAYS
                        and preferences[employee][day] != UNAVAILABLE
                    ]

                    # Randomizing candidates and prioritizing preferred shifts
                    random.shuffle(candidates)
                    candidates.sort(
                        key=lambda employee:
                        (preference_rank(preferences[employee][day], shift),
                         work_days[employee])
                    )

                    # Checking candidate availability before assigning shifts
                    if not candidates:
                        success = False
                        break

                    # Assigning an employee and updating daily and weekly counts
                    employee = candidates[0]
                    schedule[day][shift].append(employee)
                    assigned_today.add(employee)
                    work_days[employee] += 1

                # Stopping the current attempt when a shift cannot be filled
                if not success:
                    break

            # Stopping the current attempt when scheduling becomes impossible
            if not success:
                break

        # Returning the schedule after successfully filling every shift
        if success:
            for day in range(len(DAYS)):
                employees = [e for shift in schedule[day] for e in shift]
                schedule[day] = best_daily_shifts(employees, day, preferences)
            return schedule

    # Repair conflicts across days if the randomized attempts did not succeed.
    return repair_schedule(preferences)


def validate_schedule(schedule, preferences):
    # Initializing employee workday counts and validation messages
    work_days = [0] * len(preferences)
    errors = []

    # Checking employee assignments across every day and shift
    for day in range(len(DAYS)):
        assigned_today = set()

        for shift in range(len(SHIFTS)):
            employees = schedule[day][shift]

            # Verifying the required number of employees per shift
            if len(employees) != SHIFT_CAPACITY:
                errors.append(
                    f"{DAYS[day]} {SHIFTS[shift]}: "
                    f"{len(employees)}/2 employees."
                )

            # Checking duplicate daily assignments and employee availability
            for employee in employees:
                if employee in assigned_today:
                    errors.append(
                        f"Employee {employee + 1} has multiple "
                        f"shifts on {DAYS[day]}."
                    )

                assigned_today.add(employee)
                work_days[employee] += 1

                # Detecting assignments on unavailable days
                if preferences[employee][day] == UNAVAILABLE:
                    errors.append(
                        f"Employee {employee + 1} is unavailable "
                        f"on {DAYS[day]}."
                    )

    # Checking the maximum weekly working-day limit
    for employee, count in enumerate(work_days):
        if count > MAX_WORK_DAYS:
            errors.append(
                f"Employee {employee + 1} exceeds five workdays."
            )

    # Returning workday totals and validation errors
    return work_days, errors


def main():
    # Displaying application instructions and scheduling rules
    print("EMPLOYEE SHIFT SCHEDULER")
    print("M = Morning, A = Afternoon, E = Evening, O = Unavailable")
    print("Bonus: enter all three shifts in priority order, e.g. M,E,A.")
    print("Single choices are allowed; other shifts tie as fallbacks.")
    print("Each shift has capacity two (the required minimum).")
    print("Each employee can work at most five days per week.\n")

    # Collecting and validating the total employee count
    while True:
        try:
            total = int(input("Enter number of employees (minimum 9): "))
            if total >= 9:
                break
            print("Please enter at least nine employees.")
        except ValueError:
            print("Please enter a valid whole number.")

    # Initializing employee names, preferences, and shift-choice mappings
    names = []
    preferences = []

    # Collecting unique employee names and daily shift preferences
    for employee in range(total):
        while True:
            name = input(
                f"\nEnter employee {employee + 1} name: "
            ).strip()

            # Checking for nonempty and unique employee names
            if name and name.casefold() not in {
                existing.casefold() for existing in names
            }:
                break

            print("Enter a unique, nonempty name.")

        names.append(name)
        daily_preferences = []

        # Collecting and validating availability for each weekday
        for day in DAYS:
            while True:
                choice = input(
                    f"{name}, {day} [M/A/E, ranked M,E,A, or O]: "
                ).strip().upper()

                # Recording valid shift preferences or unavailability
                try:
                    daily_preferences.append(parse_preference(choice))
                    break
                except ValueError as error:
                    print(error)

        preferences.append(daily_preferences)

    # Generating the weekly employee shift schedule
    schedule = create_schedule(preferences)

    # Handling unsuccessful scheduling attempts
    if schedule is None:
        print("\nA complete schedule could not be found.")
        print("Check availability or add more employees.")
        print("No incomplete schedule is being reported as successful.")
        return

    # Displaying the completed weekly shift schedule
    print("\nFINAL EMPLOYEE SHIFT SCHEDULE")

    for day in range(len(DAYS)):
        print(f"\n{DAYS[day]}")

        for shift in range(len(SHIFTS)):
            employees = schedule[day][shift]

            # Converting assigned employee numbers into employee names
            assigned_names = ", ".join(
                names[employee] for employee in employees
            )

            # Displaying shift assignments and staffing capacity
            print(
                f"  {SHIFTS[shift]:<10}: {assigned_names} "
                f"({len(employees)}/2)"
            )

    # Calculating and displaying employee workday totals
    work_days, errors = validate_schedule(schedule, preferences)

    print("\nEMPLOYEE WORKDAY SUMMARY")
    for name, count in zip(names, work_days):
        print(f"  {name}: {count} working days")

    # Counting assignments matching employee shift preferences
    preferred = sum(
        preferences[employee][day][0] == shift
        for day in range(len(DAYS))
        for shift in range(len(SHIFTS))
        for employee in schedule[day][shift]
    )

    # Calculating the total number of scheduled assignments
    total_assignments = sum(work_days)

    # Displaying preference-matching assignment statistics
    print(f"\nPreferred assignments: {preferred}/{total_assignments}")

    print("\nRANKED PREFERENCE AND CONFLICT SUMMARY")
    ranks = [0, 0, 0]
    for day in range(len(DAYS)):
        for shift in range(len(SHIFTS)):
            for employee in schedule[day][shift]:
                rank = preference_rank(preferences[employee][day], shift)
                ranks[rank] += 1
                if rank:
                    print(f"  {names[employee]}, {DAYS[day]}: "
                          f"{SHIFTS[preferences[employee][day][0]]} requested -> "
                          f"{SHIFTS[shift]} assigned (fallback rank {rank + 1})")
    print(f"Rank 1: {ranks[0]} | Rank 2/fallback: {ranks[1]} | Rank 3: {ranks[2]}")
    print("Ranks are improved for each day's selected employees; "
          "global optimality is not claimed.")

    # Displaying final schedule validation results
    print("\nSCHEDULING VALIDATION")

    if not errors:
        print("PASS: All 21 shifts have two employees.")
        print("PASS: No employee works multiple shifts per day.")
        print("PASS: No employee works more than five days.")
        print("PASS: Unavailable days are respected.")
    else:
        # Reporting scheduling validation errors
        print("Validation errors found:")
        for error in errors:
            print(error)


# Starting the scheduling application
if __name__ == "__main__":
    main()
