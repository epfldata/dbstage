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



# Fields requirements

want to be able to completely remove the wrapping+unwrapping code manipulating fields,
such as when treating them as monoids;
this is important because, for example, we want to programmatically compile String-monoid-based field aggregations
in query code to use a more efficient StringBuilder impl

can make @field expand into an opaque/translucent type alias;
can have a companion object that specifies the type is a Wrapper, which allows to derive instances such as for Monoid;
the main annoyance is that we don't get a way to automatically attach implicits to fields anymore (there is no Field object where to put them)



# Push/Pull

There's no need to have both capabilities in staged world;
indeed, in staged world it is easy to _compile_ a `ClosedCode[Iterator[T]]` to an efficient loop,
because the code fragment contains all the info that's needed to do the streamlining


# Queries Interface

It is not actually necessary that the yielded expression of a for-comprehension be a Monoid –
it is sufficient that it be a Magma with an associated Zero/Identity element;
unfortunately, neither Cats nor Scalaz has a type class for that.


It would be good to use custom CommutativeMonoid and IdempotentMonoid classes in order to indicate these properties
(as opposed to having the propery be encoded in a non-extensible set of stagedMonoid representations)

Additionally, that could allow us to set in place the same restrictions as the monoid comprehension calculus,
which seem to be the right way to desing such interface



# TODO

Implement different strategies for automatic parallelization



Implement advanced multi-staging: given a filtered query source, always compile and execute it to retrive the first, say, 64 elements,
gathering information about that source (its number of elements and their nature or whether it has more than 64),
allowing for more informed query compilation decisions.  
This could be enormously beneficial for cases where we have small collections with a few different values



Use `obj[Field]` to select, and `obj[Field].value` or `obj[Field].v` to access the wrapped value...


## TODO next

repr filter predicates in useful NF  
move predicates up to outermost input possible (cf. pred on order stated in lineitem pred)






# Bugs encountered so far while developing

runtime compilation failed after I used `code"???"` upcasted to `Code[MyThing,C]`, where `apply` was selected on that thing (no upcasts were inserted so I got the error "apply not a member of Nothing") 

I @embedded some code that ended up having references to a private member of a class, failing runtime compilation

impl stuf that I forgot to complete: I was replacing the pred predicate of a filter instead of composing it




# Related

Haskell's `TransformListComp` extension & related  
https://www.reddit.com/r/haskell/comments/7le8h5/some_notes_about_how_i_write_haskell/drn2ktn/  
https://www.schoolofhaskell.com/school/to-infinity-and-beyond/pick-of-the-week/guide-to-ghc-extensions/list-and-comprehension-extensions

