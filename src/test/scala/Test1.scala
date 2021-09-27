import zio.test.*
import zio.test.Assertion.*


object Test1Spec extends DefaultRunnableSpec {

  def spec = suite("suite")(
    test("test") {
      assert(1)(equalTo(1))
    }
  )
}