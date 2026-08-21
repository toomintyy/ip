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

## TC2: Preserve multi-word date and time strings

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
