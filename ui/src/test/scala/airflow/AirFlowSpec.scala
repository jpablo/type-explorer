package airflow

import com.raquo.airstream.core.{EventStream, Observer, Signal}
import com.raquo.airstream.ownership.Subscription
import com.raquo.airstream.eventbus.{EventBus, WriteBus}
import com.raquo.airstream.ownership.{Owner, Subscription}
import com.raquo.airstream.timing.PeriodicEventStream
import zio.test.*
import zio.test.Assertion.*

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import fixtures.TestableOwner

object AirFlowSpec extends DefaultRunnableSpec {

  given owner: Owner = new TestableOwner

  def spec = suite("airflow spec")(
    test("EventStream") {
      println("----- EventStream ------")

      val intStream: EventStream[Int] = EventStream.fromValue(1)
      val stringStream: EventStream[String] = intStream.map(_.toString)
      val s1: Subscription = intStream.foreach(println)
      s1.kill()


//      val s: Subscription = intStream.addObserver(???)(???)
//      s.kill()

      val futureStream: EventStream[Int] = EventStream.fromFuture(Future.apply(1))

      // avoid
      val seqStream: EventStream[Int] = EventStream.fromSeq(List(1,2,3))

      val periodicStream: PeriodicEventStream[Int] = EventStream.periodic(100)

      val (stream: EventStream[Int], callback: (Int => Unit)) = EventStream.withCallback[Int]
      callback(1)
      stream.foreach(println)
      callback(2)

      assert(1)(equalTo(1))
    },
    test("Signal") {

      val intStream: EventStream[Int] = EventStream.fromValue(1)

      val signal: Signal[Int] = intStream.startWith(2)


      val futureSignal: Signal[Option[Int]] = Signal.fromFuture(Future.apply(1))

      // Getting Signal's current value

//      val x = intStream.withCurrentValueOf(signal).map((a, b) => ???)

      assert(1)(equalTo(1))
    },

    test("Observer") {
      val obs = Observer.apply(println)

      obs.onNext("next value")

      assert(1)(equalTo(1))
    },

    test("EventBus") {
      println("--------- EventBus ----------")
      val bus = new EventBus[Int]
      val bev: EventStream[Int] = bus.events
      val wr: WriteBus[Int] = bus.writer

      bus.emit(1)
      wr.onNext(1)

      bev.foreach(println)

      assert(1)(equalTo(1))
    }
  )
}

