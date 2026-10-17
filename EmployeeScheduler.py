"""
Developing a weekly employee shift scheduling system using Python randomization and preference-based assignment.
Collecting employee names and daily availability preferences for morning, afternoon, and evening shifts.
Generating a schedule while maintaining shift capacity, daily assignment limits, and weekly working-day restrictions.
Validating the completed schedule and displaying employee workday totals, preferred assignments, and scheduling results.
"""

# Employee Shift Scheduler

# Importing Library
import random

DAYS = [
    "Monday", "Tuesday", "Wednesday", "Thursday",
    "Friday", "Saturday", "Sunday"
]

SHIFTS = ["Morning", "Afternoon", "Evening"]
SHIFT_CAPACITY = 2
MAX_WORK_DAYS = 5
UNAVAILABLE = -1


def create_schedule(preferences):
    # Preparing employee counts and schedule information
    employee_count = len(preferences)

    # Repeating randomized attempts to generate a complete schedule
    for attempt in range(2000):
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
                        preferences[employee][day] != shift
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
            return schedule

    # Returning no schedule after exhausting all randomized attempts
    return None


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
    print("Each shift requires two employees.")
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
    choices = {"M": 0, "A": 1, "E": 2, "O": UNAVAILABLE}

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
                    f"{name}, {day} [M/A/E/O]: "
                ).strip().upper()

                # Recording valid shift preferences or unavailability
                if choice in choices:
                    daily_preferences.append(choices[choice])
                    break

                print("Invalid choice. Enter M, A, E, or O.")

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
        preferences[employee][day] == shift
        for day in range(len(DAYS))
        for shift in range(len(SHIFTS))
        for employee in schedule[day][shift]
    )

    # Calculating the total number of scheduled assignments
    total_assignments = sum(work_days)

    # Displaying preference-matching assignment statistics
    print(f"\nPreferred assignments: {preferred}/{total_assignments}")

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