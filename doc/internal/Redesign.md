API based on cross-stage persistence – much, much nicer to use

TODO: Modify the meaning of .compile to optimize queries?


# Front-end possibilities

could use a `@autoLift` macro on the methods of Query, to capture the arguments as code values
but this is going to work well with `flatMap` – though it would work if one uses `join` instead... but basically, no meaningful nesting allowed

so this (allowing seamless nesting in an inspectable way) seems like it would be an important advantage of the CSP/QSR-based approach


drop usage of `NoFields` in favor of simply using `Unit`!?
