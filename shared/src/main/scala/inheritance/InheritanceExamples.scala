package inheritance

import models.*

object InheritanceExamples {

  val baseObservable     = Type("BaseObservable", methods = List(Method("map"), Method("mapTo"), Method("flatMap")))
  val observable         = Type("Observable")
  val observer           = Type("Observer")
  val source             = Type("Source", methods = List(Method("toObservable", Some(observable))))
  val named              = Type("Named", methods = List(Method("toObserver", Some(observer))))
  val sink               = Type("Sink")
  val eventSource        = Type("EventSource")
  val signalSource       = Type("SignalSource")
  val eventBus           = Type("EventBus")
  val `var`              = Type("Var")
  val writeBus           = Type("WriteBus")
  val writableObservable = Type("WritableObservable")
  val eventStream        = Type("EventStream", methods = List(Method("filter"), Method("delay")))
  val signal             = Type("Signal", methods = List(Method("now"), Method("compose")))
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
      pairs = pairs,
      types = (pairs.map(_._1) ++ pairs.map(_._2)).distinct
    )

}


@main
def inheritanceExample() =
  println(InheritanceExamples.laminar)