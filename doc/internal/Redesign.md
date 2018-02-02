API based on cross-stage persistence – much, much nicer to use

TODO: Modify the meaning of .compile to optimize queries?


# Front-end possibilities

could use a `@autoLift` macro on the methods of Query, to capture the arguments as code values
but this is going to work well with `flatMap` – though it would work if one uses `join` instead... but basically, no meaningful nesting allowed

so this (allowing seamless nesting in an inspectable way) seems like it would be an important advantage of the CSP/QSR-based approach


drop usage of `NoFields` in favor of simply using `Unit`!?
use ~ or ~: to aggregate
have a Record[T] type class that gives useful capabilities on proper record types (ie: with labels); eg. lifting a plain tuple into the record type, and converting back


have Bag#limit(n) operation; properly optimized
	take care of the case where 'n' is dependent on some previous query! – or a query itself...


Support nested queries; eg. sum of the salaries of the richest 10 people


Data types:
	FileInput[T:Serial](separator)(implicit encoding) <: Relation[T]
	Table[T:Record[StorageTypeClass]]  // to efficiently store things
	InMemoryTable  // default impl; stores things in memory



Staged interface:
	circumvent lack of first-class polymorphism: try to have map generate an object with a path-dependent context that can be used in the type of the lambda passed to its apply method...


TODO: make relation types reflect primary keys?


TODO: Bag#groupBy should remove the grouping attributes from the row; to do that, make Access[F,R] have a field removal type result and associated removal function
=> ...or simply have the convetion that 'groupBy' (or just 'group') takes additional keys not present in the aggregation record





