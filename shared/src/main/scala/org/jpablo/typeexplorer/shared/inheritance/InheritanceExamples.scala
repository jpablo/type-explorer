package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, Symbol}

object InheritanceExamples {

  val baseObservable     = Namespace(Symbol("BaseObservable"), "BaseObservable", kind = NamespaceKind.Class, methods = List(Method("map"), Method("mapTo"), Method("flatMap")))
  val observable         = Namespace(models.Symbol("Observable"), "Observable")
  val observer           = Namespace(models.Symbol("Observer"), "Observer")
  val source             = Namespace(models.Symbol("Source"), "Source", methods = List(Method("toObservable", Some(observable))))
  val named              = Namespace(models.Symbol("Named"), "Named", methods = List(Method("toObserver", Some(observer))))
  val sink               = Namespace(models.Symbol("Sink"), "Sink")
  val eventSource        = Namespace(models.Symbol("EventSource"), "EventSource")
  val signalSource       = Namespace(models.Symbol("SignalSource"), "SignalSource")
  val eventBus           = Namespace(models.Symbol("EventBus"), "EventBus")
  val `var`              = Namespace(models.Symbol("Var"), "Var")
  val writeBus           = Namespace(models.Symbol("WriteBus"), "WriteBus")
  val writableObservable = Namespace(models.Symbol("WritableObservable"), "WritableObservable")
  val eventStream        = Namespace(models.Symbol("EventStream"), "EventStream", methods = List(Method("filter"), Method("delay")))
  val signal             = Namespace(models.Symbol("Signal"), "Signal", methods = List(Method("now"), Method("compose")))
  val writableSignal     = Namespace(models.Symbol("WritableSignal"), "WritableSignal")
  val strictSignal       = Namespace(models.Symbol("StrictSignal"), "StrictSignal")
  val `val`              = Namespace(models.Symbol("Val"), "Val")

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
      arrows = pairs.map { case (a, b) => a.symbol -> b.symbol },
      namespaces = (pairs.map(_._1) ++ pairs.map(_._2)).distinct
    )

}


//@main
//def inheritanceExample() =
//  println(InheritanceExamples.laminar)
