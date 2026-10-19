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
- Development Environment: Console application; commands work from a terminal in VS Code.
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
- Preferred assignment counts, where implemented.
- Scheduling validation messages or an explanation of why a complete schedule could not be generated.
A successful validation should confirm that every shift has two employees, no employee works multiple shifts on the same day, no employee exceeds five working days, and unavailable days are respected.

## Conclusion

The Employee Shift Scheduler demonstrates the practical use of control structures in Python and Java. Both implementations apply conditional logic, iteration, data structures, randomized selection, and validation to manage weekly employee assignments. The project illustrates how similar scheduling requirements can be implemented using the distinct language features of Python and Java while accounting for employee preferences, availability, and workday restrictions.

## Updates to the original implementation

The original program structure is retained: input loops, names, schedule storage,
randomized attempts, daily and weekly eligibility checks, independent validation,
and output by day and shift. These changes address the identified gaps:

- Java now shuffles candidates before sorting, avoiding entry-order bias.
- Both versions prefer fewer assigned workdays when preference ranks tie.
- Both versions try all 720 orders of each day's six selected employees to choose
  the smallest total preference-rank penalty.
- After 100 unsuccessful randomized attempts, a replacement-chain routine revises
  attendance across days. It can move a fully booked employee only after finding
  a replacement for their earlier assignment. This removes reliance on random
  retries for feasibility.
- Java duplicate names are checked without regard to letter case.

The assumption is a capacity of exactly two employees per shift. This satisfies
the specified minimum and defines when a preferred shift is full. Available
employees can work any shift; preferences are soft, and O is a hard day off.

## Bonus ranked preferences

Enter a complete ordering such as M,E,A (Morning first, Evening second,
Afternoon third), a single M/A/E choice, or O. A full ranking must contain each
shift exactly once. For a single choice, both unlisted shifts tie as fallbacks;
the application does not invent a second preference.

Every assigned fallback is listed with employee name, day, requested shift, and
assigned shift. Rank totals follow the listing. Staffing and work limits always
take priority. Ranks are optimized for each day's chosen six employees; the
application does not claim a globally optimal weekly preference score.

This implements the optional ranked-preference feature in both languages.

## Optional graphical interface

The Python version also includes a Tkinter interface for both input and output.
It collects employee names and seven daily ranked preferences in a scrollable
form, validates names and selections, and displays the final week in a schedule
table with workday totals, preference ranks, fallbacks, and validation status.
The original console program remains available and unchanged in how it is run.

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

## Automated checks

```sh
python test_scheduler.py
python -m py_compile EmployeeScheduler.py EmployeeSchedulerGUI.py
python EmployeeSchedulerGUI.py --demo --self-test
javac EmployeeScheduler.java EmployeeSchedulerTest.java
java EmployeeSchedulerTest
```

The tests cover identical first choices, varied unavailable days, impossible
weekly capacity, malformed rankings, and validation of deliberately broken output.
The repair tests compare 120 generated cases per language with an independent
capacity calculation over all subsets of the seven days.

## Reproducing output for the report

`sample-input.txt` contains nine fictional employees, all requesting Morning first,
with different second choices. Run interactively or redirect this file:

```sh
python EmployeeScheduler.py < sample-input.txt
java EmployeeScheduler < sample-input.txt
```

Randomized selection means names and totals can vary between executions.
Open `Assignment4.code-workspace` to use the supplied light theme. Capture the
real editor and terminal, including the complete week, workday totals, ranked
input, and fallback/validation output. Do not use the previous illustrated IDE
images as screenshots.
