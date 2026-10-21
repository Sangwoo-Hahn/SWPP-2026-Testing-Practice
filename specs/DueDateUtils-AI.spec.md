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
  - An active task without a due date returns the empty string. `[Unit]`
    - Tip: Given dueDate=null, today=2026-10-21, isCompleted=false / When called / Then return "".
  - A future due date returns `D-N`, where N is the total calendar-day difference. `[Unit]`
    - Tip: Given dueDate=2026-11-21, today=2026-10-21, isCompleted=false / When called / Then return "D-31".
  - A past due date returns `D+N`, where N is the absolute total calendar-day difference. `[Unit]`
    - Tip: Given dueDate=2026-09-21, today=2026-10-21, isCompleted=false / When called / Then return "D+30".
- Error cases
  - None. Every input satisfying the pre-conditions is valid. `[Unit]`
    - Tested by the success and edge-case tests below; no rejection path exists.
- Edge cases
  - An active task due today returns exactly `D-Day`. `[Unit]`
    - Tip: Given dueDate=2026-10-21, today=2026-10-21, isCompleted=false / When called / Then return "D-Day".
  - The day immediately before today is overdue by one calendar day. `[Unit]`
    - Tip: Given dueDate=2026-10-20, today=2026-10-21, isCompleted=false / When called / Then return "D+1".
  - The day immediately after today is one calendar day away. `[Unit]`
    - Tip: Given dueDate=2026-10-22, today=2026-10-21, isCompleted=false / When called / Then return "D-1".

**Undefined behavior** (optional)
- Parsing, storage, date-picker validation, and timezone conversion are outside this function; its inputs are already calendar dates.
