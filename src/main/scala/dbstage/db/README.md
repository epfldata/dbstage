# dbStage core

## Intro

Main idea: simpler foundation for an SQL-based frontend

No particular emphasis on a language-integrated query DSL
– use SQL;
**BUT**
we can set up a neat mechanism on top to integrate SQL into a program
and give precise types to queries;
**AND** we can also make a query DSL based on the same mechanism

The idea is to use a `compileTime` Database instance
and a few macros that require it implicitly
(such as `Dynamic#selectDynamic` macros in the case the query DSL),
so that precise types can be used and query code can be generated at compile time!

The db should still be able to handle SQL dynamically



## Staging

Interesting ideas based on MSP:
have tables that are known statically (`compileTime`)
and a way to partially evaluate them;
but **ALSO** have a way to say that a table is _fixed_ after some loading time,
so that the _same_ partial evaluation and optimizations can be deferred to the runtime!

Similar with queries... have a way to register fixed queries that can be aggressively compiled
and that can influence the way data is stored



## Code-Gen

The simplest way of making use of a database is to configure it using the simple SQL-like frontend,
which could even be configured entirely with SQL commands,
and then code-gen some code for it into a Scala file in a separate project,
so that this file can be used by actual programs in a type-safe way,
and without obscure macro machinery;
it's also very nice being able to see the actual code

Note that the implementation code and the interface code should be separated
so the latter can easily be read and understood, as it has to be by all users,
whereas the impl is just for curious and advanced developers to look at

TODO: make infrastructure for generating proper Scala classes and definitions,
filling their method implementations with Squid code values




