package inheritance

import models.*

object InheritanceExamples {

  val baseObservable     = Namespace(Symbol("BaseObservable"), "BaseObservable", kind = NamespaceKind.Class, methods = List(Method("map"), Method("mapTo"), Method("flatMap")))
  val observable         = Namespace(Symbol("Observable"), "Observable")
  val observer           = Namespace(Symbol("Observer"), "Observer")
  val source             = Namespace(Symbol("Source"), "Source", methods = List(Method("toObservable", Some(observable))))
  val named              = Namespace(Symbol("Named"), "Named", methods = List(Method("toObserver", Some(observer))))
  val sink               = Namespace(Symbol("Sink"), "Sink")
  val eventSource        = Namespace(Symbol("EventSource"), "EventSource")
  val signalSource       = Namespace(Symbol("SignalSource"), "SignalSource")
  val eventBus           = Namespace(Symbol("EventBus"), "EventBus")
  val `var`              = Namespace(Symbol("Var"), "Var")
  val writeBus           = Namespace(Symbol("WriteBus"), "WriteBus")
  val writableObservable = Namespace(Symbol("WritableObservable"), "WritableObservable")
  val eventStream        = Namespace(Symbol("EventStream"), "EventStream", methods = List(Method("filter"), Method("delay")))
  val signal             = Namespace(Symbol("Signal"), "Signal", methods = List(Method("now"), Method("compose")))
  val writableSignal     = Namespace(Symbol("WritableSignal"), "WritableSignal")
  val strictSignal       = Namespace(Symbol("StrictSignal"), "StrictSignal")
  val `val`              = Namespace(Symbol("Val"), "Val")

  val pairs: List[(Namespace, Namespace)] =
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

  lazy val laminar =
    InheritanceDiagram(
      pairs = pairs.map { case (a, b) => a.symbol -> b.symbol },
      namespaces = (pairs.map(_._1) ++ pairs.map(_._2)).distinct
    )

}


//@main
//def inheritanceExample() =
//  println(InheritanceExamples.laminar)
