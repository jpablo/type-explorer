package app

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type

object LeftColumn {

  def leftColumn =
    accordion(
      section         = MockData.typeStream,
      sectionId       = _.name,
      sectionHeader   = _.name,
      sectionChildren = _.methods.map(m => div(m.name)),
      alwaysOpen      = true
    ).amend(
      idAttr := "te-left-column",
      cls := ("col", `accordion-flush`, open)
    )
}


object MockData {
  val types =
    List(
      callGraph.CallGraphExamples.TaskAllocationController,
      callGraph.CallGraphExamples.TaskAllocationServiceImpl,
      callGraph.CallGraphExamples.MetricsServiceImpl,
      inheritance.InheritanceExamples.baseObservable,
      inheritance.InheritanceExamples.observable,
      inheritance.InheritanceExamples.observer,
      inheritance.InheritanceExamples.source,
      inheritance.InheritanceExamples.named,
      inheritance.InheritanceExamples.eventStream,
    )

  val typeStream: EventStream[List[Type]] =
    EventStream.fromSeq(List(types))
}