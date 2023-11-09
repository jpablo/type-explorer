package org.jpablo.typeexplorer.shared.inheritance

import zio.test.*
import zio.json.*


object PlantumlInheritanceSpec extends ZIOSpecDefault {

  val diagram = exampleInheritanceDiagram.fromJson[InheritanceGraph].toOption.get

  def spec =
    suite("PlantumlInheritance")(
      test("export to plantuml"):
        val uml = diagram.toPlantUML(Map.empty, DiagramOptions(showFields = true))
        val expected = examplePuml.strip

        assertTrue(uml.diagram == expected)
    )
}

val examplePuml =
  """
    |@startuml
    |!pragma layout smetana
    |set namespaceSeparator none
    |skinparam class {
    |  'FontSize 20
    |  'FontName "JetBrains Mono"
    |}
    |
    |'declarations
    |
    |
    |namespace "bank.b2.core.api.kafka" as bank.b2.core.api.kafka {
    |  class "AccountClosed" as bank/b2/core/api/kafka/AccountEvent.AccountClosed# {
    |  accountId
    |' bank/b2/core/api/kafka/AccountEvent.AccountClosed#accountId.
    |  externalId
    |' bank/b2/core/api/kafka/AccountEvent.AccountClosed#externalId.
    |  metadata
    |' bank/b2/core/api/kafka/AccountEvent.AccountClosed#metadata.
    |  timestamp
    |' bank/b2/core/api/kafka/AccountEvent.AccountClosed#timestamp.
    |}
    |
    |class "AccountEvent" as bank/b2/core/api/kafka/AccountEvent# << (T, pink) >> {
    |  accountId
    |' bank/b2/core/api/kafka/AccountEvent#accountId().
    |  externalId
    |' bank/b2/core/api/kafka/AccountEvent#externalId().
    |  metadata
    |' bank/b2/core/api/kafka/AccountEvent#metadata().
    |  timestamp
    |' bank/b2/core/api/kafka/AccountEvent#timestamp().
    |}
    |
    |}
    |
    |
    |'inheritance
    |
    |"bank/b2/core/api/kafka/AccountEvent#" <|-- "bank/b2/core/api/kafka/AccountEvent.AccountClosed#"
    |@enduml
    |""".stripMargin

val exampleInheritanceDiagram =
  """
    |{
    |  "arrows": [
    |    [
    |      "bank/b2/core/api/kafka/AccountEvent.AccountClosed#",
    |      "bank/b2/core/api/kafka/AccountEvent#"
    |    ]
    |  ],
    |  "namespaces": [
    |    {
    |      "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#",
    |      "displayName": "AccountClosed",
    |      "kind": {
    |        "Class": {}
    |      },
    |      "methods": [
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#accountId.",
    |          "displayName": "accountId"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#canEqual().",
    |          "displayName": "canEqual"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#copy().",
    |          "displayName": "copy"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#equals().",
    |          "displayName": "equals"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#externalId.",
    |          "displayName": "externalId"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#hashCode().",
    |          "displayName": "hashCode"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#metadata.",
    |          "displayName": "metadata"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#productArity().",
    |          "displayName": "productArity"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#productElement().",
    |          "displayName": "productElement"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#productIterator().",
    |          "displayName": "productIterator"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#productPrefix().",
    |          "displayName": "productPrefix"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#timestamp.",
    |          "displayName": "timestamp"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent.AccountClosed#toString().",
    |          "displayName": "toString"
    |        }
    |      ],
    |      "documentURI": "b2-banking-platform-api/src/main/scala/bank/b2/core/api/kafka/AccountEvent.scala",
    |      "semanticDbUri": "/Users/jpablo/chamba/backend/b2-banking-platform-api/target/scala-2.12/meta/META-INF/semanticdb/b2-banking-platform-api/src/main/scala/bank/b2/core/api/kafka/AccountEvent.scala.semanticdb",
    |      "basePath": "/Users/jpablo/chamba/backend/b2-banking-platform-api",
    |      "range": {
    |        "startLine": 70,
    |        "startChar": 13,
    |        "endLine": 70,
    |        "endChar": 26
    |      }
    |    },
    |    {
    |      "symbol": "bank/b2/core/api/kafka/AccountEvent#",
    |      "displayName": "AccountEvent",
    |      "kind": {
    |        "Trait": {}
    |      },
    |      "methods": [
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent#accountId().",
    |          "displayName": "accountId"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent#externalId().",
    |          "displayName": "externalId"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent#metadata().",
    |          "displayName": "metadata"
    |        },
    |        {
    |          "symbol": "bank/b2/core/api/kafka/AccountEvent#timestamp().",
    |          "displayName": "timestamp"
    |        }
    |      ],
    |      "documentURI": "b2-banking-platform-api/src/main/scala/bank/b2/core/api/kafka/AccountEvent.scala",
    |      "semanticDbUri": "/Users/jpablo/chamba/backend/b2-banking-platform-api/target/scala-2.12/meta/META-INF/semanticdb/b2-banking-platform-api/src/main/scala/bank/b2/core/api/kafka/AccountEvent.scala.semanticdb",
    |      "basePath": "/Users/jpablo/chamba/backend/b2-banking-platform-api",
    |      "range": {
    |        "startLine": 59,
    |        "startChar": 12,
    |        "endLine": 59,
    |        "endChar": 24
    |      }
    |    }
    |  ]
    |}
    |
    """.stripMargin
