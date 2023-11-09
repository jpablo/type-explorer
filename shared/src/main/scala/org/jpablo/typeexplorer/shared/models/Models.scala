package org.jpablo.typeexplorer.shared.models

import zio.json.*

import scala.meta.internal.semanticdb.SymbolOccurrence

enum NamespaceKind derives JsonCodec:
  case Object
  case PackageObject
  case Package
  case Class
  case Trait
  case Unknown
//  case Other(name: String)

opaque type GraphSymbol = String

object GraphSymbol:
  def apply(value: String): GraphSymbol = value
  def empty: GraphSymbol = ""
  extension (s: GraphSymbol) def toString: String = s
  given JsonCodec[GraphSymbol] = JsonCodec.string
  given JsonFieldDecoder[GraphSymbol] = JsonFieldDecoder.string
  given JsonFieldEncoder[GraphSymbol] = JsonFieldEncoder.string


case class Package(name: String)

case class SymbolRange(
  startLine : Int,
  startChar : Int,
  endLine   : Int,
  endChar   : Int
) derives JsonCodec

object SymbolRange:
  def from(so: SymbolOccurrence): SymbolRange =
    val r = so.getRange
    SymbolRange(r.startLine, r.startCharacter, r.endLine, r.endCharacter)

case class Namespace(
  symbol        : GraphSymbol,
  displayName   : String,
  kind          : NamespaceKind       = NamespaceKind.Class,
  methods       : List[Method]        = List.empty,
  documentURI   : Option[String]      = None,
  semanticDbUri : Option[String]      = None,
  basePath      : Option[String]      = None,
  range         : Option[SymbolRange] = None
) derives JsonCodec:
  lazy val inTest =
    documentURI.exists(_.contains("src/test"))


case class Method(symbol: GraphSymbol, displayName: String, returnType: Option[Namespace]) derives JsonCodec


object Method:
  def apply(name: String, returnType: Option[Namespace] = None): Method =
    Method(GraphSymbol(name), name, returnType)

