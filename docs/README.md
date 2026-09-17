# Minty User Guide

## Reminders

Enter `reminders` or its alias `remind` to see incomplete deadlines due and events starting
today through six days later, inclusive. Both commands take no arguments.
Minty uses your computer's local date and checks it again for every query.

Results appear earliest first, preserving list order for equal dates. Their numbers refer
to the full task list, so you can use them with `mark` or `delete`.
Todos, completed tasks, overdue deadlines, and events that have already started are excluded.

For example, on September 17, 2026, a deadline due September 23 is included,
but one due September 24 is excluded:

```text
Here are your reminders for today and the next 6 days:
2.[D][ ] submit report (by: Sep 23 2026)
```

If nothing qualifies, either command responds:

```text
Here are your reminders for today and the next 6 days:
There are no incomplete deadlines or events in this period.
```

On startup, qualifying reminders also appear after the welcome message, in a separate
Minty chat bubble (or console block). No startup message appears when there are no matches.
Reminders repeat on later launches while tasks qualify. There are no background alerts.
Adding, marking, unmarking, or deleting tasks affects the next query; old chat messages remain unchanged.

`remind 2`, `reminders 14`, and `Reminders` are invalid and produce
`Sorry, I don't understand that command.`
Reminders are derived from existing dates, so the save format is unchanged.

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
