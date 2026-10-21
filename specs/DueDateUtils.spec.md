# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the task-list label required by the feature intent. Count total calendar days between the supplied dates; completion overrides the optional due date.

**Pre-conditions**
- `today` is a valid non-null LocalDate; `dueDate` is null or a valid LocalDate.
- `isCompleted` is a Boolean. The reference date is the supplied `today`, not the device clock.

**Post-conditions**
- Success cases
  - A completed task returns exactly `CLEAR`, including when its due date is absent. `[Unit]`
    - Tip: Given dueDate=null, today=2026-10-21, isCompleted=true / When called / Then return "CLEAR".
    - Tested by `DueDateUtilsTest.dueDateLabel_completedWithoutDueDate_returnsClear`.
  - An active task without a due date returns the empty string. `[Unit]`
    - Tip: Given dueDate=null, today=2026-10-21, isCompleted=false / When called / Then return "".
    - Tested by `DueDateUtilsTest.dueDateLabel_activeWithoutDueDate_returnsEmpty`.
  - A future due date returns `D-N`, where N is the total calendar-day difference. `[Unit]`
    - Tip: Given dueDate=2026-11-21, today=2026-10-21, isCompleted=false / When called / Then return "D-31".
    - Tested by `DueDateUtilsTest.dueDateLabel_wholeMonthAhead_returnsD31`.
  - A past due date returns `D+N`, where N is the absolute total calendar-day difference. `[Unit]`
    - Tip: Given dueDate=2026-09-21, today=2026-10-21, isCompleted=false / When called / Then return "D+30".
    - Tested by `DueDateUtilsTest.dueDateLabel_wholeMonthPast_returnsDPlus30`.
- Error cases
  - None. All inputs satisfying the pre-conditions are accepted; there is no error-specific test case.
- Edge cases
  - An active task due today returns exactly `D-Day`. `[Unit]`
    - Tip: Given dueDate=2026-10-21, today=2026-10-21, isCompleted=false / When called / Then return "D-Day".
    - Tested by `DueDateUtilsTest.dueDateLabel_today_returnsDDay`.
  - The day immediately before today is overdue by one calendar day. `[Unit]`
    - Tip: Given dueDate=2026-10-20, today=2026-10-21, isCompleted=false / When called / Then return "D+1".
    - Tested by `DueDateUtilsTest.dueDateLabel_yesterday_returnsDPlus1`.
  - The day immediately after today is one calendar day away. `[Unit]`
    - Tip: Given dueDate=2026-10-22, today=2026-10-21, isCompleted=false / When called / Then return "D-1".
    - Tested by `DueDateUtilsTest.dueDateLabel_tomorrow_returnsD1`.
  - Completion takes priority over past, present, and future due dates. `[Unit]`
    - Tip: Given today=2026-10-21, isCompleted=true and dueDate successively 2026-10-20, 2026-10-21, 2026-10-22 / When each is called / Then every result is "CLEAR".
    - Tested by `DueDateUtilsTest.dueDateLabel_completedWithDates_returnsClear`.
  - Crossing a year boundary counts all calendar days. `[Unit]`
    - Tip: Given dueDate=2027-01-01, today=2026-12-31, isCompleted=false / When called / Then return "D-1".
    - Tested by `DueDateUtilsTest.dueDateLabel_yearBoundaryFuture_returnsD1`.
  - The reverse year boundary remains overdue by one day. `[Unit]`
    - Tip: Given dueDate=2026-12-31, today=2027-01-01, isCompleted=false / When called / Then return "D+1".
    - Tested by `DueDateUtilsTest.dueDateLabel_yearBoundaryPast_returnsDPlus1`.
  - A whole year is not reduced to the day component of a period. `[Unit]`
    - Tip: Given dueDate=2027-10-21, today=2026-10-21, isCompleted=false / When called / Then return "D-365".
    - Tested by `DueDateUtilsTest.dueDateLabel_wholeYearAhead_returnsD365`.
  - A multi-month interval includes its months as calendar days. `[Unit]`
    - Tip: Given dueDate=2026-08-02, today=2026-05-31, isCompleted=false / When called / Then return "D-63".
    - Tested by `DueDateUtilsTest.dueDateLabel_multipleMonthsAhead_returnsD63`.
  - A leap day is included in the forward interval. `[Unit]`
    - Tip: Given dueDate=2024-03-01, today=2024-02-28, isCompleted=false / When called / Then return "D-2".
    - Tested by `DueDateUtilsTest.dueDateLabel_leapDayForward_returnsD2`.
  - A leap day is included in the reverse interval. `[Unit]`
    - Tip: Given dueDate=2024-02-28, today=2024-03-01, isCompleted=false / When called / Then return "D+2".
    - Tested by `DueDateUtilsTest.dueDateLabel_leapDayBackward_returnsDPlus2`.
  - The day before a leap day is exactly one calendar day away. `[Unit]`
    - Tip: Given dueDate=2024-02-29, today=2024-02-28, isCompleted=false / When called / Then return "D-1".
    - Tested by `DueDateUtilsTest.dueDateLabel_leapDayAdjacent_returnsD1`.
  - A non-leap February ends without adding a leap day. `[Unit]`
    - Tip: Given dueDate=2026-03-01, today=2026-02-28, isCompleted=false / When called / Then return "D-1".
    - Tested by `DueDateUtilsTest.dueDateLabel_nonLeapFebruary_returnsD1`.
  - Crossing a month boundary includes adjacent days correctly. `[Unit]`
    - Tip: Given dueDate=2026-11-01, today=2026-10-31, isCompleted=false / When called / Then return "D-1".
    - Tested by `DueDateUtilsTest.dueDateLabel_monthBoundaryFuture_returnsD1`.
  - The reverse month boundary includes adjacent days correctly. `[Unit]`
    - Tip: Given dueDate=2026-10-31, today=2026-11-01, isCompleted=false / When called / Then return "D+1".
    - Tested by `DueDateUtilsTest.dueDateLabel_monthBoundaryPast_returnsDPlus1`.

**Undefined behavior** (optional)
- Parsing, storage, date-picker validation, and timezone conversion are outside this function; its inputs are already calendar dates.
