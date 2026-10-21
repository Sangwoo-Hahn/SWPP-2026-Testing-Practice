# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Render the current task label and visibility in a TextView. Each binding replaces previous state, so reusing a view or toggling completion never leaves a stale label.

**Pre-conditions**
- The supplied TextView is non-null and accessed on the Android main thread.
- `task` is null or a Task whose dueDate is null or a valid LocalDate.
- The device date remains fixed for a call. Date-dependent examples use 2026-10-21; tests fix that date on an emulator or use an equivalent offset from a captured date.

**Post-conditions**
- Success cases
  - A completed task sets the label text to `CLEAR` and displays it. `[Integration]`
    - Tip: Given a TextView and a completed Task(dueDate=null) / When bound / Then text is "CLEAR" and visibility is VISIBLE.
  - An active task with no due date shows no label. `[Integration]`
    - Tip: Given a TextView and an active Task(dueDate=null) / When bound / Then text is "" and visibility is not VISIBLE.
  - An active task due today displays `D-Day`. `[Integration]`
    - Tip: Given a TextView and an active Task(dueDate=2026-10-21), with device date 2026-10-21 / When bound / Then text is "D-Day" and visibility is VISIBLE.
  - A future due date displays the total-calendar-day label. `[Integration]`
    - Tip: Given a TextView and an active Task(dueDate=2026-11-21), with device date 2026-10-21 / When bound / Then text is "D-31" and visibility is VISIBLE.
- Error cases
  - None. Every input satisfying the pre-conditions is valid. `[Integration]`
    - Tested by the success and edge-case tests below; no rejection path exists.
- Edge cases
  - Unchecking a completed task with no due date removes its old `CLEAR` label. `[Integration]`
    - Tip: Given the same TextView bound to Task(isCompleted=true,dueDate=null) / When rebound to that task with isCompleted=false / Then text is "" and visibility is not VISIBLE.
  - Checking an active task with no due date displays its `CLEAR` label. `[Integration]`
    - Tip: Given the same TextView bound to Task(isCompleted=false,dueDate=null) / When rebound to that task with isCompleted=true / Then text is "CLEAR" and visibility is VISIBLE.

**Undefined behavior** (optional)
- No layout spacing policy is imposed: an empty label may be GONE or INVISIBLE, but must have empty text and must not be VISIBLE.
- A call that crosses device midnight is excluded from date-dependent tests; UI locale styling is outside this binding contract.
