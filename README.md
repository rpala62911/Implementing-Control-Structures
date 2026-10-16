# Assignment 4: Implementing Control Structures

## Project Overview

The project implements an Employee Shift Scheduler using Python and Java to demonstrate the application of control structures in two different programming languages. The application manages employee names, daily shift preferences, employee availability, and weekly scheduling requirements. The scheduler allocates employees to morning, afternoon, and evening shifts across seven days. Conditional statements, iterative structures, collections, arrays, and randomized selection are used to generate schedules, resolve assignment conflicts, and validate scheduling constraints.

## Application Requirements and Constraints

The application addresses the following requirements:
- Employee Input and Storage: Collecting unique employee names and daily shift preferences for Monday through Sunday.
- Shift Capacity: Assigning exactly two employees to each shift, resulting in 21 daily shift assignments across the week.
- Daily Work Limit: Restricting each employee to one shift per day.
- Weekly Work Limit: Limiting each employee to a maximum of five working days per week.
- Employee Availability: Preventing assignments on days marked as unavailable.
- Preference-Based Scheduling: Prioritizing employees whose preferred shifts match the available shift, with randomized day and shift ordering.
- Schedule Validation: Checking staffing levels, duplicate daily assignments, employee availability, and weekly working-day limits.
- Failure Handling: Reporting when a complete schedule cannot be generated within the configured scheduling attempts.

## Technologies Used

- Python: Lists, sets, dictionaries, loops, conditional statements, functions, and randomization.
- Java: Arrays, ArrayLists, HashSets, methods, loops, conditional statements, switch-style input handling, and randomization.
- Development Environment: Visual Studio Code and PowerShell.
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

The validation procedures examine shift staffing, multiple assignments on a single day, unavailable days, and weekly working-day totals. Invalid input is handled through repeated prompts, while unsuccessful scheduling attempts produce a failure message.

## How to Run the Application

### Running the Python Program

Python 3 must be installed and accessible from PowerShell.
Run the application:
python EmployeeScheduler.py
Enter the employee count, each employee's name, and a shift preference for each day. Review the generated weekly schedule and validation results.

### Running the Java Program

A Java Development Kit (JDK) is required for command-line compilation. The source filename must match the public class name.
The Java source should declare:
java
public class EmployeeScheduler
Save the source file as EmployeeScheduler.java.
Compile and execute the application:
javac EmployeeScheduler.java
java EmployeeScheduler
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
