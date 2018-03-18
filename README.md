# DBStage – Flexible, Staged Query Compilation Playground

![stability-experimental](https://img.shields.io/badge/stability-experimental-orange.svg)

## Introduction

This repository contains a proof of concept for a configurable language-integrated runtime query compiler based on staging.
The implementation relies on the [Squid type-safe metaprogramming framework](https://github.com/epfldata/squid/) for Scala, 
which makes its code manipulation and generation capabilities fairly robust.

The main features are:
 * An expressive SQL embedded DSL (still currently missing many features), 
 with type-safe integration to normal Scala programs (LinQ-style); 
 
 * A backend implemented using powerful abstractions and Scala modular programming,
 which allows great configurability at no runtime cost: 
 experiment and combine 
 different ways to store relation tables (column store, row store, hash map), 
 different ways to index them, 
 different ways to query them (push, pull), etc.


## Step by step

### 1. define the database relations

```scala
case object Person extends Relation {
  val Id = Column[Int]("Id", primary = true)
  val Name = Column[String]("Name")
  val Age = Column[Int]("Age")
  val Sex = Column[Sex]("Sex")
}
```

### 2.a. register queries to be executed later, using a SQL-like DSL

```scala
  import Person._
  val q0 = from(Person) where ir"$Age > 18" where ir"$Sex == Male" select (Name,Age)
```

(Of course, one can write `where ir"$Age > 18 && $Sex == Male"` equivalently.)

Note that column types are checked at compile-time, but column reference consistency and ambiguities are checked at query construction time (runtime). 
For example if I had written `select (Name,Age,Salary)` it would have complained at runtime that there are no such Salary column available. (It would be easy to have a compile-time linter written in Squid to catch these errors earlier.)

### 2.b. load the data from the file system

```scala
  Person.loadDataFromFile("data/persons.csv", compileCode = true)
```

This compiles a program on-the-fly to efficiently load the data given the relation schema.

### 2.c. on-the-fly compile and execute queries

```scala
  q0.plan.foreach { case (name, age) => assert(age > 18); println(s"$name $age") }
```

Notice that the types for `name` and `age` are correctly inferred as String and Int, respectively.

Importantly, steps 2.a, 2.b and 2.c can be done in any order and can be interleaved.

Another example: all pairs of people of the same age but opposite sex:

```scala
  val m = from(Person)
  val f = from(Person)
  val q = ((m where ir"$Sex == Male") join (f where ir"$Sex == Female"))(ir"${m.Age} == ${f.Age}")
    .select (m.Age, m.Name, f.Name, m.Id, f.Id)
  q.printLines
```

Which prints the following:

| Age(0) | Name(0) | Name(1) | Id(0) | Id(1) |
| --- | --- | --- | --- | --- |
| 41 | bob parker | julia kenn | 1 | 6 |
|...|...|...|...|...|

The currently supported functionalities are:
 * Selection, projection, filtering, (hash) joins
 * Option to load data in a hashmap where the keys are the primary keys of the relation; this structure is then used to perform faster joins
 * Option to store data in column store, on a per-relation basis (if the above is not applied on the given relation)
 * User-defined functions and data types
 * Pushing and pulling are both supported
 * The type-safe DSL means one can integrate queries inside general purpose program, using DBStage as a simple Scala library
 * Engine is agnostic in the underlying data structures and row representation; tables currently use tuples and Scala ArrayBuffer/HashMap's, but we could easily experiment with off-heap memory to avoid boxing, for example.

What I'd like to have in the future:
 * Aggregations, grouping, sorting
 * Customize the storage of data optimizing for registered queries (possibly adapt it dynamically as more queries are registered)
 * Option to instrument the data loading code to add more error recovery and/or add data analytics guiding subsequent query compilation
 * Extend the SQL subset with updates, perhaps transactions
 * Handling of data on disk, and associated cache management?

