package inheritance

object Examples {

  val source             = Type("Source")
  val named              = Type("Named")
  val sink               = Type("Sink")
  val baseObservable     = Type("BaseObservable")
  val eventSource        = Type("EventSource")
  val signalSource       = Type("SignalSource")
  val observer           = Type("Observer")
  val observable         = Type("Observable")
  val eventBus           = Type("EventBus")
  val `var`              = Type("Var")
  val writeBus           = Type("WriteBus")
  val writableObservable = Type("WritableObservable")
  val eventStream        = Type("EventStream")
  val signal             = Type("Signal")
  val writableSignal     = Type("WritableSignal")
  val strictSignal       = Type("StrictSignal")
  val `val`              = Type("Val")

  val pairs =
    List(
      baseObservable     -> source,
      baseObservable     -> named,
      eventSource        -> source,
      signalSource       -> source,
      observer           -> named,
      observer           -> sink,
      observable         -> baseObservable,
      eventBus           -> eventSource,
      eventBus           -> named,
      eventBus           -> sink,
      `var`              -> signalSource,
      `var`              -> named,
      `var`              -> sink,
      writeBus           -> observer,
      writableObservable -> observable,
      eventStream        -> observable,
      eventStream        -> baseObservable,
      eventStream        -> eventSource,
      signal             -> observable,
      signal             -> baseObservable,
      signal             -> signalSource,
      writableSignal     -> writableObservable,
      writableSignal     -> signal,
      strictSignal       -> signal,
      `val`              -> writableSignal,
      `val`              -> strictSignal
    )

  val laminar =
    InheritanceDiagram(
      types = (pairs.map(_._1) ++ pairs.map(_._2)).distinct,
      parents = pairs.groupMap(_._1)(_._2),
      children = pairs.groupMap(_._2)(_._1)
    )

}


@main
def inheritanceExample() =
  println(Examples.laminar)