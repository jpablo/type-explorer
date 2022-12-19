package org.jpablo.typeexplorer.shared.models

import zio.json.*

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


case class Namespace(
  symbol     : Symbol,
  displayName: String,
  kind       : NamespaceKind = NamespaceKind.Class,
  methods    : List[Method] = List.empty
)

object Namespace:
  given JsonCodec[Namespace] = DeriveJsonCodec.gen


case class Method(symbol: Symbol, displayName: String, returnType: Option[Namespace])


object Method:
  def apply(name: String, returnType: Option[Namespace] = None): Method =
    Method(Symbol(name), name, returnType)

  given JsonCodec[Method] = DeriveJsonCodec.gen

