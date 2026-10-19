"""Run with python test_scheduler.py. No third-party packages required."""
import copy
import random
import unittest
import EmployeeScheduler as scheduler


def feasible(preferences):
    # Independent capacity test over all subsets of the seven days.
    # Each employee contributes at most five distinct available days.
    for mask in range(1, 1 << 7):
        days = [d for d in range(7) if mask & (1 << d)]
        supply = sum(min(5, sum(p[d] != -1 for d in days)) for p in preferences)
        if supply < 6 * len(days):
            return False
    return True


class SchedulerTests(unittest.TestCase):
    def check_schedule(self, schedule, preferences):
        self.assertIsNotNone(schedule)
        totals, errors = scheduler.validate_schedule(schedule, preferences)
        self.assertEqual(errors, [])
        self.assertEqual(sum(totals), 42)
        for day in schedule:
            people = [e for shift in day for e in shift]
            self.assertEqual(len(people), 6)
            self.assertEqual(len(set(people)), 6)
        self.assertLessEqual(max(totals), 5)

    def test_original_failure_case_and_ranked_preferences(self):
        for seed in range(30):
            random.seed(seed)
            prefs = [[[0, 2, 1] for _ in range(7)] for _ in range(9)]
            self.check_schedule(scheduler.create_schedule(prefs), prefs)

    def test_repair_against_independent_feasibility_test(self):
        random.seed(632)
        for case in range(120):
            count = random.randrange(8, 15)
            prefs = [[random.sample([0, 1, 2], 3) if random.random() > 0.24 else -1
                      for _ in range(7)] for _ in range(count)]
            result = scheduler.repair_schedule(prefs)
            self.assertEqual(result is not None, feasible(prefs), case)
            if result is not None:
                self.check_schedule(result, prefs)

    def test_insufficient_weekly_capacity_despite_daily_coverage(self):
        # Seven available people per day are insufficient: 7 * 5 < 42.
        prefs = [[[0] for _ in range(7)] for _ in range(7)] + [[-1] * 7 for _ in range(2)]
        self.assertIsNone(scheduler.repair_schedule(prefs))

    def test_input(self):
        self.assertEqual(scheduler.parse_preference(" e, m, a "), [2, 0, 1])
        self.assertEqual(scheduler.parse_preference("M"), [0])
        self.assertEqual(scheduler.parse_preference("o"), -1)
        for invalid in ("", "M,M,E", "M,A", "O,M,E", "M,A,E,", "X"):
            with self.assertRaises(ValueError):
                scheduler.parse_preference(invalid)

    def test_bonus_and_validator(self):
        prefs = [[[i // 3, (i // 3 + 1) % 3, (i // 3 + 2) % 3]
                  for _ in range(7)] for i in range(9)]
        result = scheduler.best_daily_shifts([0, 1, 3, 4, 6, 7], 0, prefs)
        self.assertEqual(sum(scheduler.preference_rank(prefs[e][0], shift)
                             for shift, group in enumerate(result) for e in group), 0)
        valid = scheduler.create_schedule(prefs)
        broken = copy.deepcopy(valid)
        broken[0][1][0] = broken[0][0][0]
        self.assertTrue(scheduler.validate_schedule(broken, prefs)[1])


if __name__ == "__main__":
    unittest.main(verbosity=2)

