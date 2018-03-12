package dbstage.example

import squid.utils._

object DesugaringOfFor extends App {{
  
  val ls = List(1,2)
  val opt = Option(1)
  
  Debug.show {
    for {
      a <- opt
      b <- opt
    //} identity(a+b)
    } yield identity(a+b)
  }
  Debug.show {
    for {
      a <- opt
      if a < 0
      b <- opt
    } yield identity(a+b)
  }
  Debug.show {
    for {
      a <- opt
      b <- opt
      if a < b
    } yield identity(a+b)
  }
  Debug.show {
    for {
      a <- opt
      x = a      // why such a so very ugly desugaring?
      b <- opt   //   -> eval order; and if it was evaluated differently it would be tricky with an `if` going afterwards
    //} identity(a+b)
    } yield identity(a+b)
  }
  Debug.show {
    for {
      a <- opt
      b <- opt
      x = a+b
    //} identity(a+b)
    } yield identity(a+b)
  }
  Debug.show {
    for {
      a <- opt
      x = a
      b <- opt
      y = b
      c <- opt
      z = c
    //} identity(a+b+c)
    } yield identity(a+b+c)
  }
  
  Debug.show {
    for {
      a <- ls
      () = print(s"a $a; ")
      b <- ls
      () = print(s"b $b; ")
    } println("~")
  }
  Debug.show {
    for {
      a <- ls
      if {print(s"a $a; "); true}
      b <- ls
      if {print(s"b $b; "); true}
    } println("~")
  }
  
  /*
  Would be much more efficient and probably less surprising to change the eval order of desugared code
  
  for { a <- opt; x = a; if x>0; b <- opt } println(b)
  ~>
  opt.foreach { a => val x = a; if (x>0) opt.foreach { b => println(b) } }
  
  for { a <- opt; x = a; if x>0; b <- opt } println(b)
  ~>
  opt.flatMap { a => val x = a; opt.withConstantFilter(x>0).map { b => println(b) } }
  
  */
  
}}
