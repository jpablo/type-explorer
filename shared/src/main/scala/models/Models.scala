package models

import zio.json.*

enum NamespaceKind:
  case Object
  case PackageObject
  case Package
  case Class
  case Trait
  case Unknown
  case Other(name: String)

object NamespaceKind:
  given JsonEncoder[NamespaceKind] = DeriveJsonEncoder.gen
  given JsonDecoder[NamespaceKind] = DeriveJsonDecoder.gen

opaque type Symbol = String

object Symbol:
  def apply(value: String): Symbol = value
  def empty: Symbol = ""
  given JsonEncoder[Symbol] = JsonEncoder.string
  given JsonDecoder[Symbol] = JsonDecoder.string

  extension (s: Symbol)
    def toString: String = s


case class Package(name: String)


case class Namespace(
  symbol     : Symbol,
  displayName: String,
  kind       : NamespaceKind = NamespaceKind.Class,
  methods    : List[Method] = List.empty
)

object Namespace:
  given JsonEncoder[Namespace] = DeriveJsonEncoder.gen
  given JsonDecoder[Namespace] = DeriveJsonDecoder.gen


case class Method(symbol: Symbol, displayName: String, returnType: Option[Namespace])

object Method:
  def apply(name: String, returnType: Option[Namespace] = None): Method =
    Method(Symbol(name), name, returnType)

  given JsonEncoder[Method] = DeriveJsonEncoder.gen
  given JsonDecoder[Method] = DeriveJsonDecoder.gen

