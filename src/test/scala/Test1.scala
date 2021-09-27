import zio.test.*
import zio.test.Assertion.*
// import zio.test.magnolia.DeriveGen

object Test1Spec extends DefaultRunnableSpec {

  def spec = suite("suite")(
    test("test") {
      assert(1)(equalTo(1))
    },
//    testM("random") {
//      check(generators.updateMetricRequest) { x =>
//        println(x.asJson)
//        assert(x)(equalTo(x))
//      }
//    }
  )
}

//object generators {
//  val metricRequest = DeriveGen[MetricRequest]
//  val updateMetricRequest = DeriveGen[UpdateMetricRequest]
//}
