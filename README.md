

Have something like a 'StorageManager'


TODO better handling of join conditions: 
represent as a `Seq[Code[Bool]]` and move down those conditions that do not concern the join



Pull approaches:

 * Functional 'iterator' encoding: `() => Option[T]`  
   Pro: nice interface    
   Cons: options create complicated control-flow that the current IR does not handle well
   Cons: allocates options -- even if we can get rid of internal ones, the final code will still contain the type in its interface and thus options will still have to be allocated  
 
 * Natural 'imperative iterator' encoding: `() => (() => Bool, () => T)`  
   Pro: immediate mapping to Scala/Java iterators  
   Pro: no Option allocation  
   Cons: impl of `filter` is unnecessarily compliated and requires local optional variables, which sucks (even though they can be flattened to two local variables, it's still unnecessary variables)
 
 * Imperative CPS `() => (T => Unit) => Unit`  
   Where the continuation passed must be invoked exactly once unless there are no more elements in the stream  
   (This is the approach in GPCE17)  
   Pro (big): simpler impl of operators  
   Cons (big): generated code isn't nice for, eg, combinations of filter (unless we merge them before generating the code), as they will contain nested while loops; in general, the fact that `filter` need to loop seems problematic (is it really?)
 
 * Imperative CPS with boolean return `() => (T => Unit) => Bool`  
   Where the boolean indicates whether there **potentially** are still elements in the stream; the continuation passed may not be executed every time, but it will be executed at most once each time  
   Pro (big): simpler impl of operators  
   Cons: if one wants to consume exactly one element, one has to loop until some local optional variable is set by the continuation  
   Cons (minor): iterator impl very awkward: `hasNext` needs to execute the continuation, so it needs to put the value aside so `next` accesses it later 
   (corollary: can't just peek to see if a given stream has any elements without actively tryin to consume one)
     
 * Imperative CPS with two boolean returns `() => (T => Unit) => (Bool,Bool)`  
   Indicating potential elements left and whether an element was consumed or not
   Pro: avoids using a variable to know whether we've iterated or not

 * Imperative CPS + virtualized variable `() => (Var[Bool], (T => Unit) => Bool)`  
  An interesting variation of the above, but it's not clear whether it has any actual advantages over it
  






