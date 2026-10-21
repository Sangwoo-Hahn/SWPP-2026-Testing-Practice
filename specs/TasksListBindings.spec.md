# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Render the current task label in a TextView. Each binding replaces previous state, so reusing a view or toggling completion never leaves a stale label.

**Pre-conditions**
- The supplied TextView is non-null and accessed on the Android main thread.
- `task` is a non-null Task whose dueDate is null or a valid LocalDate for the required feature cases.
- The device date remains fixed during each test. Capture T = LocalDate.now() before binding and confirm the date is unchanged afterward. Exact examples use T = 2026-10-21; executable tests use the stated day offsets from T without changing the device clock.

**Post-conditions**
- Success cases
  - A completed task sets the label text to `CLEAR` and displays it. `[Integration]`
    - Tip: Given a TextView and a completed Task(dueDate=null) / When bound / Then text is "CLEAR" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_completedWithoutDueDate_displaysClear`.
  - An active task with no due date shows no label. `[Integration]`
    - Tip: Given a TextView and an active Task(dueDate=null) / When bound / Then text is "".
    - Tested by `TasksListBindingsTest.setDueDateLabel_activeWithoutDueDate_hidesEmptyLabel`.
  - An active task due today displays `D-Day`. `[Integration]`
    - Tip: Given a TextView and an active Task(dueDate=2026-10-21), with device date 2026-10-21 / When bound / Then text is "D-Day" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_dueToday_displaysDDay`.
  - A future due date displays the total-calendar-day label. `[Integration]`
    - Tip: Given a TextView and an active Task(dueDate=T.plusDays(31)), with T=2026-10-21 and dueDate=2026-11-21 / When bound / Then text is "D-31" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_wholeMonthAhead_displaysD31`.
- Error cases
  - None. All inputs satisfying the pre-conditions are accepted; there is no error-specific test case.
- Edge cases
  - Unchecking a completed task with no due date removes its old `CLEAR` label. `[Integration]`
    - Tip: Given the same TextView bound to Task(isCompleted=true,dueDate=null) / When rebound to that task with isCompleted=false / Then text is "".
    - Tested by `TasksListBindingsTest.setDueDateLabel_uncheckWithoutDueDate_clearsPreviousClear`.
  - Checking an active task with no due date displays its `CLEAR` label. `[Integration]`
    - Tip: Given the same TextView bound to Task(isCompleted=false,dueDate=null) / When rebound to that task with isCompleted=true / Then text is "CLEAR" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_checkWithoutDueDate_displaysClear`.
  - Rebinding a dated task to a task with no due date clears stale text. `[Integration]`
    - Tip: Given device date 2026-10-21 and the same TextView displaying an active task due 2026-10-22 ("D-1") / When rebound to an active task with dueDate=null / Then text is "".
    - Tested by `TasksListBindingsTest.setDueDateLabel_removeDueDate_clearsPreviousDday`.
  - Adding a due date to an undated active task displays its D-day label. `[Integration]`
    - Tip: Given T=2026-10-21 and the same TextView with an undated active task / When rebound to dueDate=T.plusDays(1), i.e. 2026-10-22 / Then text is "D-1" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_addDueDate_displaysDday`.
  - Unchecking a dated task restores its D-day label. `[Integration]`
    - Tip: Given device date 2026-10-21 and the same TextView displaying "CLEAR" for a completed task due 2026-10-22 / When rebound with isCompleted=false / Then text is "D-1" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_uncheckWithDueDate_restoresDday`.
  - The empty/non-empty boundary includes yesterday. `[Integration]`
    - Tip: Given device date 2026-10-21 and an active task due 2026-10-20 / When bound / Then text is "D+1" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_yesterday_displaysDPlus1`.
  - The empty/non-empty boundary includes tomorrow. `[Integration]`
    - Tip: Given device date 2026-10-21 and an active task due 2026-10-22 / When bound / Then text is "D-1" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_tomorrow_displaysD1`.
  - Repeated binding is idempotent and does not depend on previous content. `[Integration]`
    - Tip: Given a TextView containing "stale" and an active task with dueDate=null / When the same task is bound twice / Then after each call text is "".
    - Tested by `TasksListBindingsTest.setDueDateLabel_repeatedEmptyBinding_remainsEmpty`.

  - A completed task with a due date displays `CLEAR`, rather than its D-day label. `[Integration]`
    - Tip: Given T=2026-10-21 and a completed Task(dueDate=T.plusDays(1)), i.e. 2026-10-22 / When bound / Then text is "CLEAR" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_completedWithDueDate_displaysClear`.
  - Checking a dated active task replaces its D-day label with `CLEAR`. `[Integration]`
    - Tip: Given T=2026-10-21 and the same TextView showing "D-1" for an active Task(dueDate=T.plusDays(1)), i.e. 2026-10-22 / When rebound with isCompleted=true / Then text is "CLEAR" and visibility is VISIBLE.
    - Tested by `TasksListBindingsTest.setDueDateLabel_checkWithDueDate_replacesDdayWithClear`.

**Undefined behavior** (optional)
- No layout spacing or empty-label visibility policy is imposed: VISIBLE, INVISIBLE, and GONE are all allowed when text is empty. Non-empty labels must be displayed.
- Behavior for task=null is outside the required feature contract because the feature intent only describes real tasks.
- A call that crosses device midnight is excluded from date-dependent tests; UI locale styling is outside this binding contract.
