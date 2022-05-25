package app

import com.raquo.laminar.api.L.*
import models.Type

object LeftColumn {

  val elementStream: EventStream[List[Div]] = MockData.typeStream.split(_.name)(renderType)

  def leftColumn() = {
    div(cls := "col",
      children <-- elementStream
    )
  }


  def renderType(id: String, initial: Type, typeStream: EventStream[Type]): Div =
    div(
      p(child.text <-- typeStream.map(_.name))
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