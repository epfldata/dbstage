package gen

/* workaround so I don't get a deprecation warning on every recompilation of my DelayedInit classes... */
trait DelayedInit extends scala.DelayedInit
