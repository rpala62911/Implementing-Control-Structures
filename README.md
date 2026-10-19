# Assignment 4: Implementing Control Structures

## Project Overview

The project implements an Employee Shift Scheduler using Python and Java to demonstrate the application of control structures in two different programming languages. The application manages employee names, daily shift preferences, employee availability, and weekly scheduling requirements. The scheduler allocates employees to morning, afternoon, and evening shifts across seven days. Conditional statements, iterative structures, collections, arrays, and randomized selection are used to generate schedules, resolve assignment conflicts, and validate scheduling constraints.

## Application Requirements and Constraints

The application addresses the following requirements:
- Employee Input and Storage: Collecting unique employee names and daily shift preferences for Monday through Sunday.
- Shift Capacity: Assigning exactly two employees to each shift, resulting in 21 shifts and 42 employee assignments across the week.
- Daily Work Limit: Restricting each employee to one shift per day.
- Weekly Work Limit: Limiting each employee to a maximum of five working days per week.
- Employee Availability: Preventing assignments on days marked as unavailable.
- Preference-Based Scheduling: Prioritizing employees whose preferred shifts match the available shift, with randomized day and shift ordering.
- Schedule Validation: Checking staffing levels, duplicate daily assignments, employee availability, and weekly working-day limits.
- Failure Handling: Trying a replacement-chain repair after randomized attempts, and reporting when availability and the five-day limits make complete coverage impossible.

## Technologies Used

- Python: Lists, sets, dictionaries, loops, conditional statements, functions, and randomization.
- Java: Arrays, ArrayLists, HashSets, methods, loops, conditional statements, if-based input handling, and randomization.
- Development Environment: Console applications in Python and Java, with an optional Tkinter interface for Python. Commands work from a terminal in VS Code.
- Version Control: Git and GitHub.

## Implementation of Control Structures

### Conditional Statements

Conditional statements validate employee names, process shift preference inputs, determine employee eligibility, prioritize preferred shifts, and identify scheduling errors.

### Iterative Structures

for loops process employees, days, and shifts. while loops collect valid inputs and fill shift positions. Repeated scheduling attempts allow the application to try alternative randomized allocations when an attempt fails.

### Data Structures

Python uses lists and sets to represent the weekly schedule, employee preferences, daily assignments, and workday totals. Java uses multidimensional arrays, nested lists, and hash sets to represent equivalent information.

### Randomized Scheduling

The scheduling algorithm randomizes day and shift ordering and prioritizes employees who prefer the current shift. This approach introduces variation into candidate selection while maintaining the defined availability and workday constraints.

### Validation and Error Handling

The validation procedures examine shift staffing, multiple assignments on a single day, unavailable days, and weekly working-day totals. Invalid input is handled through repeated prompts, while a repair routine revises earlier attendance choices before reporting failure.

## How to Run the Application

### Running the Python Program

Python 3 must be installed and accessible from PowerShell.
Run the application:
- python EmployeeScheduler.py
- Enter the employee count, each employee's name, and a shift preference for each day. Review the generated weekly schedule and validation results.

### Running the Java Program

A Java Development Kit (JDK) is required for command-line compilation. The source filename must match the public class name.
Compile and execute the application:

```sh
javac EmployeeScheduler.java
java EmployeeScheduler
```
If PowerShell reports that javac is not recognized, install a JDK or configure the JDK's bin directory in the system PATH. Alternatively, open the Java source file in Visual Studio Code with a configured Java development environment and use the Run command.

## Testing and Expected Output

The application should be tested with different employee counts, shift preferences, and availability patterns.
The final output includes:
- A weekly schedule organized by day and shift.
- Employee names assigned to each shift.
- Employee working-day totals.
- Ranked-preference and fallback assignment counts.
- Scheduling validation messages or an explanation of why a complete schedule could not be generated.
A successful validation should confirm that every shift has two employees, no employee works multiple shifts on the same day, no employee exceeds five working days, and unavailable days are respected.

## Scheduling and Conflict Resolution

The programs first make randomized scheduling attempts while checking employee availability, daily assignments, and the five-day weekly limit. Employees who prefer a shift are considered before employees who listed it as a fallback. If the randomized attempts cannot finish the week, a replacement-chain routine revises earlier assignments before reporting failure. This allows the programs to resolve conflicts without accepting an incomplete or invalid schedule.

Each shift is assigned exactly two employees. This meets the required minimum and also defines when a preferred shift is full. An available employee may be assigned to another shift when the first choice cannot be used, but an unavailable day is never used.

## Bonus Ranked Preferences

Employees may enter one shift choice or a complete ranking such as M,E,A. The first code is the employee's first choice, followed by the second and third choices. The final output shows preference totals and lists any fallback assignments. Ranked preferences are supported in both the Python and Java programs.

## Optional Graphical Interface

The Python version includes a Tkinter interface for input and output. It collects employee names and seven daily preferences, checks the entries, and displays the final schedule in a weekly table. It also displays workday totals, preference results, fallback assignments, and the validation result. The console programs remain available for demonstrating control structures in both languages.

```sh
python EmployeeSchedulerGUI.py
```

Use **Load Sample Data** for a quick demonstration, then select
**Generate Schedule**. Tkinter is included with standard Python installations
that provide desktop GUI support; no third-party package is used.

To open the interface already filled with sample data and a generated schedule:

```sh
python EmployeeSchedulerGUI.py --demo
```

## Automated Checks

```sh
python test_scheduler.py
python -m py_compile EmployeeScheduler.py EmployeeSchedulerGUI.py
python EmployeeSchedulerGUI.py --demo --self-test
javac EmployeeScheduler.java EmployeeSchedulerTest.java
java EmployeeSchedulerTest
```

The tests cover repeated first choices, unavailable days, insufficient weekly capacity, invalid rankings, duplicate names, and validation of an intentionally incorrect schedule.

## Reproducing output for the report

`sample-input.txt` contains nine fictional employees with ranked shift preferences. Run either console application interactively or redirect the sample input:

```sh
python EmployeeScheduler.py < sample-input.txt
java EmployeeScheduler < sample-input.txt
```

Randomized selection means the assignments can vary between executions. The final output still follows the same staffing, availability, and weekly-limit rules.

## Conclusion

The Employee Shift Scheduler demonstrates the practical use of control structures in Python and Java. Both implementations use conditional logic, loops, collections, randomized selection, conflict resolution, and validation to manage employee assignments. The ranked-preference option and Python graphical interface extend the original console programs while keeping the required scheduling rules.
