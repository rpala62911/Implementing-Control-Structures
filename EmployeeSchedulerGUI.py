"""Tkinter interface for the Python employee shift scheduler."""

import sys
import tkinter as tk
from tkinter import messagebox, ttk

import EmployeeScheduler as scheduler


PREFERENCE_OPTIONS = ("M", "A", "E", "M,E,A", "M,A,E", "A,M,E",
                      "A,E,M", "E,M,A", "E,A,M", "O")


class SchedulerGUI:
    """Collect employee preferences and display the generated weekly schedule."""

    def __init__(self, root):
        self.root = root
        self.root.title("Employee Shift Scheduler")
        self.root.geometry("1180x760")
        self.root.minsize(980, 650)

        self.employee_count = tk.IntVar(value=9)
        self.name_entries = []
        self.preference_boxes = []

        self._configure_style()
        self._build_layout()
        self.build_employee_form()

    def _configure_style(self):
        style = ttk.Style(self.root)
        style.theme_use("clam")
        style.configure("TFrame", background="#f7f8fa")
        style.configure("TLabelframe", background="#f7f8fa")
        style.configure("TLabelframe.Label", background="#f7f8fa",
                        foreground="#1f2937", font=("Arial", 11, "bold"))
        style.configure("TLabel", background="#f7f8fa", foreground="#1f2937",
                        font=("Arial", 10))
        style.configure("Title.TLabel", font=("Arial", 20, "bold"),
                        foreground="#183153")
        style.configure("Subtitle.TLabel", font=("Arial", 10),
                        foreground="#4b5563")
        style.configure("Accent.TButton", font=("Arial", 10, "bold"),
                        foreground="white", background="#2563eb")
        style.map("Accent.TButton", background=[("active", "#1d4ed8")])
        style.configure("Treeview", rowheight=27, font=("Arial", 10),
                        background="white", fieldbackground="white")
        style.configure("Treeview.Heading", font=("Arial", 10, "bold"),
                        foreground="#1f2937", background="#e5e7eb")

    def _build_layout(self):
        outer = ttk.Frame(self.root, padding=14)
        outer.pack(fill="both", expand=True)

        ttk.Label(outer, text="Employee Shift Scheduler",
                  style="Title.TLabel").pack(anchor="w")
        ttk.Label(
            outer,
            text=("Seven-day schedule • two employees per shift • five-day maximum • "
                  "ranked shift preferences"),
            style="Subtitle.TLabel",
        ).pack(anchor="w", pady=(2, 12))

        controls = ttk.Frame(outer)
        controls.pack(fill="x", pady=(0, 10))
        ttk.Label(controls, text="Employees:").pack(side="left")
        ttk.Spinbox(controls, from_=9, to=30, width=5,
                    textvariable=self.employee_count).pack(side="left", padx=(6, 8))
        ttk.Button(controls, text="Build Input Form",
                   command=self.build_employee_form).pack(side="left")
        ttk.Button(controls, text="Load Sample Data",
                   command=self.load_sample).pack(side="left", padx=8)
        ttk.Button(controls, text="Generate Schedule", style="Accent.TButton",
                   command=self.generate_schedule).pack(side="right")

        panes = ttk.Panedwindow(outer, orient="vertical")
        panes.pack(fill="both", expand=True)

        input_group = ttk.Labelframe(panes, text="Employee names and daily preferences",
                                     padding=8)
        output_group = ttk.Labelframe(panes, text="Generated weekly schedule",
                                      padding=8)
        panes.add(input_group, weight=3)
        panes.add(output_group, weight=2)

        self.canvas = tk.Canvas(input_group, background="#f7f8fa",
                                highlightthickness=0)
        input_scroll = ttk.Scrollbar(input_group, orient="vertical",
                                     command=self.canvas.yview)
        self.form = ttk.Frame(self.canvas)
        self.form.bind("<Configure>",
                       lambda _event: self.canvas.configure(
                           scrollregion=self.canvas.bbox("all")))
        self.canvas.create_window((0, 0), window=self.form, anchor="nw")
        self.canvas.configure(yscrollcommand=input_scroll.set)
        self.canvas.pack(side="left", fill="both", expand=True)
        input_scroll.pack(side="right", fill="y")

        columns = ("day", "morning", "afternoon", "evening")
        self.schedule_table = ttk.Treeview(output_group, columns=columns,
                                           show="headings", height=7)
        for column, heading, width in (
            ("day", "Day", 110), ("morning", "Morning", 250),
            ("afternoon", "Afternoon", 250), ("evening", "Evening", 250)
        ):
            self.schedule_table.heading(column, text=heading)
            self.schedule_table.column(column, width=width, anchor="w")
        self.schedule_table.pack(fill="x")

        self.summary = tk.Text(output_group, height=7, wrap="word", relief="flat",
                               background="white", foreground="#1f2937",
                               font=("Arial", 10), padx=8, pady=6)
        self.summary.pack(fill="both", expand=True, pady=(8, 0))
        self.summary.insert("1.0", "Enter employee information, then select Generate Schedule.")
        self.summary.configure(state="disabled")

    def build_employee_form(self):
        try:
            count = int(self.employee_count.get())
        except (ValueError, tk.TclError):
            messagebox.showerror("Invalid employee count", "Enter a whole number.")
            return
        if count < 9:
            messagebox.showerror("Invalid employee count",
                                 "At least nine employees are required.")
            return

        for widget in self.form.winfo_children():
            widget.destroy()
        self.name_entries.clear()
        self.preference_boxes.clear()

        ttk.Label(self.form, text="#", width=3).grid(row=0, column=0, padx=3, pady=3)
        ttk.Label(self.form, text="Employee name", width=18).grid(
            row=0, column=1, padx=3, pady=3)
        for day, name in enumerate(scheduler.DAYS):
            ttk.Label(self.form, text=name[:3], width=9).grid(
                row=0, column=day + 2, padx=3, pady=3)

        for employee in range(count):
            ttk.Label(self.form, text=str(employee + 1), width=3).grid(
                row=employee + 1, column=0, padx=3, pady=2)
            name_entry = ttk.Entry(self.form, width=18)
            name_entry.grid(row=employee + 1, column=1, padx=3, pady=2)
            self.name_entries.append(name_entry)
            daily_boxes = []
            for day in range(7):
                box = ttk.Combobox(self.form, values=PREFERENCE_OPTIONS,
                                   state="readonly", width=8)
                box.set("M,E,A")
                box.grid(row=employee + 1, column=day + 2, padx=3, pady=2)
                daily_boxes.append(box)
            self.preference_boxes.append(daily_boxes)
        self.canvas.yview_moveto(0)

    def load_sample(self):
        self.employee_count.set(9)
        self.build_employee_form()
        names = ("Arjun", "Mei", "Kenji", "Priya", "Minjun",
                 "Linh", "Ananya", "Haruto", "Jia")
        rankings = ("M,E,A", "M,A,E", "A,M,E", "E,A,M", "A,E,M",
                    "E,M,A", "M,A,E", "E,A,M", "A,M,E")
        for employee, name in enumerate(names):
            self.name_entries[employee].insert(0, name)
            for day, box in enumerate(self.preference_boxes[employee]):
                box.set(rankings[(employee + day) % len(rankings)])

    def _read_input(self):
        names = [entry.get().strip() for entry in self.name_entries]
        if any(not name for name in names):
            raise ValueError("Every employee must have a name.")
        if len({name.casefold() for name in names}) != len(names):
            raise ValueError("Employee names must be unique.")
        preferences = []
        for daily_boxes in self.preference_boxes:
            preferences.append([
                scheduler.parse_preference(box.get()) for box in daily_boxes
            ])
        return names, preferences

    def generate_schedule(self):
        try:
            names, preferences = self._read_input()
        except ValueError as error:
            messagebox.showerror("Check employee information", str(error))
            return

        schedule = scheduler.create_schedule(preferences)
        if schedule is None:
            messagebox.showwarning(
                "Schedule unavailable",
                "A complete schedule could not be generated. Add employees or availability.",
            )
            return

        work_days, errors = scheduler.validate_schedule(schedule, preferences)
        for item in self.schedule_table.get_children():
            self.schedule_table.delete(item)
        for day in range(7):
            row = [scheduler.DAYS[day]]
            for shift in range(3):
                assigned = ", ".join(names[e] for e in schedule[day][shift])
                row.append(f"{assigned} (2/2)")
            self.schedule_table.insert("", "end", values=row)

        rank_counts = [0, 0, 0]
        fallbacks = []
        for day in range(7):
            for shift in range(3):
                for employee in schedule[day][shift]:
                    rank = scheduler.preference_rank(preferences[employee][day], shift)
                    rank_counts[rank] += 1
                    if rank:
                        fallbacks.append(
                            f"{names[employee]} on {scheduler.DAYS[day]}: "
                            f"{scheduler.SHIFTS[preferences[employee][day][0]]} → "
                            f"{scheduler.SHIFTS[shift]} (rank {rank + 1})"
                        )

        status = "PASS — all scheduling rules validated" if not errors else "Validation errors found"
        summary_lines = [
            status,
            "Workdays: " + "; ".join(
                f"{name} {days}" for name, days in zip(names, work_days)),
            (f"Preference totals: rank 1 = {rank_counts[0]}, "
             f"rank 2/fallback = {rank_counts[1]}, rank 3 = {rank_counts[2]}"),
        ]
        if fallbacks:
            summary_lines.append("Fallback examples: " + "; ".join(fallbacks[:3]))
        if errors:
            summary_lines.extend(errors)
        self.summary.configure(state="normal")
        self.summary.delete("1.0", "end")
        self.summary.insert("1.0", "\n".join(summary_lines))
        self.summary.configure(state="disabled")


def main():
    root = tk.Tk()
    app = SchedulerGUI(root)
    if "--demo" in sys.argv:
        app.load_sample()
        app.generate_schedule()
    if "--self-test" in sys.argv:
        root.update_idletasks()
        assert len(app.schedule_table.get_children()) == 7
        assert "PASS" in app.summary.get("1.0", "end")
        root.destroy()
        print("PASS: GUI created seven schedule rows and displayed validation results")
        return
    root.mainloop()


if __name__ == "__main__":
    main()
