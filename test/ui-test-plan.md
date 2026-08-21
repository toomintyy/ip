# Minty Level 6 UI Test Plan

These tests run with Java 25. Each test starts a fresh instance of Minty and compares the complete console output exactly.

## Coverage summary

| Behavior | Happy path | Error and boundary coverage |
| --- | --- | --- |
| `todo` | TC1, TC8 | TC4 |
| `deadline` | TC1, TC5, TC6 | TC6 |
| `event` | TC1, TC5, TC7 | TC7 |
| `list` | TC1, TC2, TC3, TC5–TC8 | TC3 checks an empty list |
| `mark` and `unmark` | TC1, TC2, TC8 | TC8 |
| `delete` | TC2 | TC3 |
| Unknown or empty command | — | TC4 |
| Startup and `bye` | TC1–TC8 | — |

The use of `ArrayList<Task>` is an implementation detail and is verified by code review rather than console output. The UI tests verify its observable add, lookup, renumbering, and deletion behavior.

## TC1: Add, mark, and list all task types

Aim: Verify that todos, deadlines, and events are created with the correct details, retain their types when marked, and appear correctly in the task list.

### Input

```text
todo borrow book
deadline do homework /by no idea :-p
event project meeting /from Mon 2pm /to 4pm
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [T][ ] borrow book
  Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [D][ ] do homework (by: no idea :-p)
  Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
  Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
  Nice! I've marked this task as done:
    [D][X] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] borrow book
  2.[D][X] do homework (by: no idea :-p)
  3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC2: Delete completed and renumbered tasks

Aim: Verify that deleting a completed middle task preserves its status in the confirmation, shifts later tasks to new indices, and can reduce the list to zero tasks.

### Input

```text
todo first
deadline second /by Friday
event third /from Monday /to Tuesday
mark 2
delete 2
mark 2
list
delete 1
delete 1
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [T][ ] first
  Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [D][ ] second (by: Friday)
  Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [E][ ] third (from: Monday to: Tuesday)
  Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
  Nice! I've marked this task as done:
    [D][X] second (by: Friday)
____________________________________________________________
____________________________________________________________
  Noted. I've removed this task:
    [D][X] second (by: Friday)
  Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
  Nice! I've marked this task as done:
    [E][X] third (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] first
  2.[E][X] third (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
  Noted. I've removed this task:
    [T][ ] first
  Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
  Noted. I've removed this task:
    [E][X] third (from: Monday to: Tuesday)
  Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC3: Handle an empty list and invalid delete arguments

Aim: Verify that listing an empty task collection is safe and that missing, non-numeric, non-positive, and out-of-range delete arguments do not change it.

### Input

```text
list
delete 1
delete
delete two
delete 0
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
____________________________________________________________
____________________________________________________________
  Please provide a task number to delete.
____________________________________________________________
____________________________________________________________
  The task number must be a whole number.
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC4: Reject an empty todo and an unknown command

Aim: Verify that Minty explains an empty todo description and an unrecognized command without adding a task or terminating the session.

### Input

```text
todo
blah
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  Hmm, a todo needs a description.
____________________________________________________________
____________________________________________________________
  Sorry, I don't understand that command.
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC5: Preserve multi-word date and time strings

Aim: Verify that deadline and event date/time values are treated as unchanged strings, including date ranges and spaces.

### Input

```text
deadline submit report /by 11/10/2019 5pm
event orientation week /from 4/10/2019 /to 11/10/2019
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [D][ ] submit report (by: 11/10/2019 5pm)
  Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
  Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[D][ ] submit report (by: 11/10/2019 5pm)
  2.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC6: Validate deadlines and recover afterward

Aim: Verify each missing deadline component produces the correct error, does not add a task, and does not prevent a later valid deadline from being stored.

### Input

```text
deadline
deadline /by Friday
deadline submit report /by
deadline valid report /by Monday 5pm
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  A deadline needs a /by date or time.
____________________________________________________________
____________________________________________________________
  Hmm, a deadline needs a description.
____________________________________________________________
____________________________________________________________
  Please say when the deadline is due after /by.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [D][ ] valid report (by: Monday 5pm)
  Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[D][ ] valid report (by: Monday 5pm)
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC7: Validate events and recover afterward

Aim: Verify every malformed event arrangement produces the correct error, does not add a task, and does not prevent a later valid event from being stored.

### Input

```text
event
event meeting /to 4pm /from 2pm
event meeting /from 2pm
event /from 2pm /to 4pm
event meeting /from /to 4pm
event meeting /from 2pm /to
event valid meeting /from Monday /to Tuesday
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  An event needs a /from date or time.
____________________________________________________________
____________________________________________________________
  Put /from before /to when adding an event.
____________________________________________________________
____________________________________________________________
  An event needs a /to date or time.
____________________________________________________________
____________________________________________________________
  Hmm, an event needs a description.
____________________________________________________________
____________________________________________________________
  Please say when the event starts after /from.
____________________________________________________________
____________________________________________________________
  Please say when the event ends after /to.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [E][ ] valid meeting (from: Monday to: Tuesday)
  Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[E][ ] valid meeting (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC8: Handle numeric boundaries and surrounding whitespace

Aim: Verify that descriptions are trimmed, valid signed or zero-padded task numbers work, and missing, non-numeric, non-positive, overflowing, or out-of-range task numbers leave state unchanged.

### Input

```text
todo       trimmed task
mark +1
unmark 01
mark
mark two
mark -1
mark 0
mark 2147483648
unmark
unmark bananas
unmark 2
deadline report /by no idea :-p
event trip /from day one /to day two
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Feeling Minty.
  What can I do for you today?
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [T][ ] trimmed task
  Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
  Nice! I've marked this task as done:
    [T][X] trimmed task
____________________________________________________________
____________________________________________________________
  OK, I've marked this task as not done yet:
    [T][ ] trimmed task
____________________________________________________________
____________________________________________________________
  Please provide a task number to mark.
____________________________________________________________
____________________________________________________________
  The task number must be a whole number.
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
____________________________________________________________
____________________________________________________________
  The task number must be a whole number.
____________________________________________________________
____________________________________________________________
  Please provide a task number to unmark.
____________________________________________________________
____________________________________________________________
  The task number must be a whole number.
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [D][ ] report (by: no idea :-p)
  Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [E][ ] trip (from: day one to: day two)
  Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] trimmed task
  2.[D][ ] report (by: no idea :-p)
  3.[E][ ] trip (from: day one to: day two)
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```
