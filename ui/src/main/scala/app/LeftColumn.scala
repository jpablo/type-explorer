package app

import com.raquo.laminar.api.L.*
import models.Type

object LeftColumn {

  val elementStream =
    MockData.typeStream.split(_.name)(renderType)

  def leftColumn =
    div(cls := "col accordion", idAttr := "te-left-column",
      children <-- elementStream
    )

  def renderType(id: String, initial: Type, typeStream: EventStream[Type]): Div =
    div(cls := "accordion-item",
      div(cls := "accordion-header",
        button(cls := "accordion-button", typ := "button",
          child.text <-- typeStream.map(_.name)
        )
      ),
      div(cls :="accordion-collapse",
        children <-- typeStream.map(p => p.methods.map(m => div(m.name)))
      )
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