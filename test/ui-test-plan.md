# Minty Level 4 UI Test Plan

These tests run with Java 25. Each test starts a fresh instance of Minty and compares the complete console output exactly.

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
  Now you have 1 tasks in the list.
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

## TC2: Reject an empty todo and an unknown command

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

## TC3: Preserve multi-word date and time strings

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
  Now you have 1 tasks in the list.
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

## TC4: Continue after malformed commands

Aim: Verify that empty input, invalid task numbers, and incomplete deadline or event commands produce helpful errors without crashing or losing existing tasks.

### Input

```text

mark
mark two
mark 1
todo valid task
unmark 0
unmark 2
deadline
deadline /by Friday
deadline submit report /by
event
event meeting /to 4pm /from 2pm
event meeting /from 2pm
event /from 2pm /to 4pm
event meeting /from /to 4pm
event meeting /from 2pm /to
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
  Sorry, I don't understand that command.
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
  Got it. I've added this task:
    [T][ ] valid task
  Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
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
  Here are the tasks in your list:
  1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC5: Preserve state across interleaved successes and errors

Aim: Verify that rejected add and status commands do not change task count, ordering, task details, or completion states while later valid commands still work.

### Input

```text
todo first
deadline second /by Friday
mark 2
todo
unmark bananas
deadline missing separator
event incomplete /from Monday
list
unmark 2
deadline /by Sunday
event third /from Monday /to Tuesday
mark 4
blah
mark 3
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
  Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [D][ ] second (by: Friday)
  Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
  Nice! I've marked this task as done:
    [D][X] second (by: Friday)
____________________________________________________________
____________________________________________________________
  Hmm, a todo needs a description.
____________________________________________________________
____________________________________________________________
  The task number must be a whole number.
____________________________________________________________
____________________________________________________________
  A deadline needs a /by date or time.
____________________________________________________________
____________________________________________________________
  An event needs a /to date or time.
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] first
  2.[D][X] second (by: Friday)
____________________________________________________________
____________________________________________________________
  OK, I've marked this task as not done yet:
    [D][ ] second (by: Friday)
____________________________________________________________
____________________________________________________________
  Hmm, a deadline needs a description.
____________________________________________________________
____________________________________________________________
  Got it. I've added this task:
    [E][ ] third (from: Monday to: Tuesday)
  Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
  That task number is not in your list.
____________________________________________________________
____________________________________________________________
  Sorry, I don't understand that command.
____________________________________________________________
____________________________________________________________
  Nice! I've marked this task as done:
    [E][X] third (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] first
  2.[D][ ] second (by: Friday)
  3.[E][X] third (from: Monday to: Tuesday)
____________________________________________________________
____________________________________________________________
  Bye. Hope to see you again soon!
____________________________________________________________
```

## TC6: Handle numeric boundaries and surrounding whitespace

Aim: Verify that descriptions are trimmed, valid signed or zero-padded task numbers select the intended task, and non-positive, overflowing, or missing task numbers leave state unchanged.

### Input

```text
todo       trimmed task
mark +1
unmark 01
mark -1
mark 0
mark 2147483648
unmark
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
  Now you have 1 tasks in the list.
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
