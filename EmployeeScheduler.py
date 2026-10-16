# Employee Shift Scheduler

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
    # Preparing employee and schedule information
    employee_count = len(preferences)

    # Repeating randomized attempts to find a complete schedule
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

                    # Prioritizing preferred shifts and randomizing ties
                    random.shuffle(candidates)
                    candidates.sort(
                        key=lambda employee:
                        preferences[employee][day] != shift
                    )

                    if not candidates:
                        success = False
                        break

                    employee = candidates[0]
                    schedule[day][shift].append(employee)
                    assigned_today.add(employee)
                    work_days[employee] += 1

                if not success:
                    break

            if not success:
                break

        # Returning a schedule only when every shift is filled
        if success:
            return schedule

    # Returning no schedule when repeated attempts cannot fill all shifts
    return None


def validate_schedule(schedule, preferences):
    # Preparing workday counts and validation messages
    work_days = [0] * len(preferences)
    errors = []

    # Checking every day and shift
    for day in range(len(DAYS)):
        assigned_today = set()

        for shift in range(len(SHIFTS)):
            employees = schedule[day][shift]

            if len(employees) != SHIFT_CAPACITY:
                errors.append(
                    f"{DAYS[day]} {SHIFTS[shift]}: "
                    f"{len(employees)}/2 employees."
                )

            for employee in employees:
                if employee in assigned_today:
                    errors.append(
                        f"Employee {employee + 1} has multiple "
                        f"shifts on {DAYS[day]}."
                    )

                assigned_today.add(employee)
                work_days[employee] += 1

                if preferences[employee][day] == UNAVAILABLE:
                    errors.append(
                        f"Employee {employee + 1} is unavailable "
                        f"on {DAYS[day]}."
                    )

    # Checking the weekly workday limit
    for employee, count in enumerate(work_days):
        if count > MAX_WORK_DAYS:
            errors.append(
                f"Employee {employee + 1} exceeds five workdays."
            )

    return work_days, errors


def main():
    # Displaying application instructions
    print("EMPLOYEE SHIFT SCHEDULER")
    print("M = Morning, A = Afternoon, E = Evening, O = Unavailable")
    print("Each shift requires two employees.")
    print("Each employee can work at most five days per week.\n")

    # Collecting a valid employee count
    while True:
        try:
            total = int(input("Enter number of employees (minimum 9): "))
            if total >= 9:
                break
            print("Please enter at least nine employees.")
        except ValueError:
            print("Please enter a valid whole number.")

    # Collecting employee names and daily preferences
    names = []
    preferences = []
    choices = {"M": 0, "A": 1, "E": 2, "O": UNAVAILABLE}

    for employee in range(total):
        while True:
            name = input(
                f"\nEnter employee {employee + 1} name: "
            ).strip()

            if name and name.casefold() not in {
                existing.casefold() for existing in names
            }:
                break

            print("Enter a unique, nonempty name.")

        names.append(name)
        daily_preferences = []

        for day in DAYS:
            while True:
                choice = input(
                    f"{name}, {day} [M/A/E/O]: "
                ).strip().upper()

                if choice in choices:
                    daily_preferences.append(choices[choice])
                    break

                print("Invalid choice. Enter M, A, E, or O.")

        preferences.append(daily_preferences)

    # Generating the weekly schedule
    schedule = create_schedule(preferences)

    if schedule is None:
        print("\nA complete schedule could not be found.")
        print("Check availability or add more employees.")
        print("No incomplete schedule is being reported as successful.")
        return

    # Displaying the final schedule
    print("\nFINAL EMPLOYEE SHIFT SCHEDULE")

    for day in range(len(DAYS)):
        print(f"\n{DAYS[day]}")

        for shift in range(len(SHIFTS)):
            employees = schedule[day][shift]
            assigned_names = ", ".join(
                names[employee] for employee in employees
            )
            print(
                f"  {SHIFTS[shift]:<10}: {assigned_names} "
                f"({len(employees)}/2)"
            )

    # Displaying weekly workday totals
    work_days, errors = validate_schedule(schedule, preferences)

    print("\nEMPLOYEE WORKDAY SUMMARY")
    for name, count in zip(names, work_days):
        print(f"  {name}: {count} working days")

    # Counting assignments matching employee preferences
    preferred = sum(
        preferences[employee][day] == shift
        for day in range(len(DAYS))
        for shift in range(len(SHIFTS))
        for employee in schedule[day][shift]
    )
    total_assignments = sum(work_days)

    print(f"\nPreferred assignments: {preferred}/{total_assignments}")

    # Displaying final validation results
    print("\nSCHEDULING VALIDATION")

    if not errors:
        print("PASS: All 21 shifts have two employees.")
        print("PASS: No employee works multiple shifts per day.")
        print("PASS: No employee works more than five days.")
        print("PASS: Unavailable days are respected.")
    else:
        print("Validation errors found:")
        for error in errors:
            print(error)


# Starting the program
if __name__ == "__main__":
    main()