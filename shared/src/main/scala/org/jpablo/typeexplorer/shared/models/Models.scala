package org.jpablo.typeexplorer.shared.models

import zio.json.*

import scala.meta.internal.semanticdb.SymbolOccurrence

enum NamespaceKind:
  case Object
  case PackageObject
  case Package
  case Class
  case Trait
  case Unknown
//  case Other(name: String)

object NamespaceKind:
  given JsonCodec[NamespaceKind] = DeriveJsonCodec.gen

opaque type Symbol = String

object Symbol:
  def apply(value: String): Symbol = value
  def empty: Symbol = ""
  extension (s: Symbol) def toString: String = s
  given JsonCodec[Symbol] = JsonCodec.string


case class Package(name: String)

case class SymbolRange(
  startLine : Int,
  startChar : Int,
  endLine   : Int,
  endChar   : Int
)

object SymbolRange:
  def from(so: SymbolOccurrence): SymbolRange =
    val r = so.getRange
    SymbolRange(r.startLine, r.startCharacter, r.endLine, r.endCharacter)

case class Namespace(
  symbol     : Symbol,
  displayName: String,
  kind       : NamespaceKind = NamespaceKind.Class,
  methods    : List[Method] = List.empty,
  documentURI: Option[String] = None,
  range      : Option[SymbolRange] = None
):
  lazy val inTest =
    documentURI.map(_.contains("src/test"))

object Namespace:
  given JsonCodec[SymbolRange] = DeriveJsonCodec.gen
  given JsonCodec[Namespace] = DeriveJsonCodec.gen


case class Method(symbol: Symbol, displayName: String, returnType: Option[Namespace])


object Method:
  def apply(name: String, returnType: Option[Namespace] = None): Method =
    Method(Symbol(name), name, returnType)

  given JsonCodec[Method] = DeriveJsonCodec.gen

