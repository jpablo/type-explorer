package app.components

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import io.laminext.fetch.*
import io.laminext.fetch.circe.*
import com.raquo.airstream.core.EventStream

def leftColumn =
  accordion (
    section         = getClasses,
    sectionId       = _.name,
    sectionHeader   = _.name,
    sectionChildren = _.methods.map(m => div(m.name)),
    alwaysOpen      = true
  ) amend (
    idAttr := "te-left-column",
    cls := ("col", `accordion-flush`, open)
  )

def getClasses: EventStream[List[Type]] =
  for
    response <- Fetch.get("http://localhost:8090/classes").decode[List[Type]]
  yield
    response.data

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
