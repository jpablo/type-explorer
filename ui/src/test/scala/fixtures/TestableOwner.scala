package fixtures

import com.raquo.airstream.ownership.{Subscription, Owner}

import scala.scalajs.js

class TestableOwner extends Owner {

  def _testSubscriptions: js.Array[Subscription] = subscriptions

  override def killSubscriptions(): Unit = {
    super.killSubscriptions()
  }
}