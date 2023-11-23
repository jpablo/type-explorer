package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, GraphSymbol}

object InheritanceExamples {

  val baseObservable     = Namespace(GraphSymbol("BaseObservable"), "BaseObservable", kind = NamespaceKind.Class, methods = List(Method("map"), Method("mapTo"), Method("flatMap")))
  val observable         = Namespace(GraphSymbol("Observable"), "Observable")
  val observer           = Namespace(GraphSymbol("Observer"), "Observer")
  val source             = Namespace(GraphSymbol("Source"), "Source", methods = List(Method("toObservable", Some(observable))))
  val named              = Namespace(GraphSymbol("Named"), "Named", methods = List(Method("toObserver", Some(observer))))
  val sink               = Namespace(GraphSymbol("Sink"), "Sink")
  val eventSource        = Namespace(GraphSymbol("EventSource"), "EventSource")
  val signalSource       = Namespace(GraphSymbol("SignalSource"), "SignalSource")
  val eventBus           = Namespace(GraphSymbol("EventBus"), "EventBus")
  val `var`              = Namespace(GraphSymbol("Var"), "Var")
  val writeBus           = Namespace(GraphSymbol("WriteBus"), "WriteBus")
  val writableObservable = Namespace(GraphSymbol("WritableObservable"), "WritableObservable")
  val eventStream        = Namespace(GraphSymbol("EventStream"), "EventStream", methods = List(Method("filter"), Method("delay")))
  val signal             = Namespace(GraphSymbol("Signal"), "Signal", methods = List(Method("now"), Method("compose")))
  val writableSignal     = Namespace(GraphSymbol("WritableSignal"), "WritableSignal")
  val strictSignal       = Namespace(GraphSymbol("StrictSignal"), "StrictSignal")
  val `val`              = Namespace(GraphSymbol("Val"), "Val")

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
    InheritanceGraph(
      arrows = pairs.toSet.map((a, b) => a.symbol -> b.symbol),
      namespaces = pairs.toSet.map(_._1) ++ pairs.map(_._2)
    )

}


//@main
//def inheritanceExample() =
//  println(InheritanceExamples.laminar)
