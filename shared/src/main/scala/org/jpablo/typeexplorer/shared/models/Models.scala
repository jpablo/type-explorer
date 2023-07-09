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

opaque type Symbol = String

object Symbol:
  def apply(value: String): Symbol = value
  def empty: Symbol = ""
  extension (s: Symbol) def toString: String = s
  given JsonCodec[Symbol] = JsonCodec.string
  given JsonFieldDecoder[Symbol] = JsonFieldDecoder.string
  given JsonFieldEncoder[Symbol] = JsonFieldEncoder.string


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
  symbol        : Symbol,
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

  def fullPath: Option[String] =
    (basePath, documentURI) match
      case (Some(basePath), Some(documentURI)) =>
        Some(basePath.split("/").dropRight(1).mkString("/") + "/" + documentURI)
      case _ => None


case class Method(symbol: Symbol, displayName: String, returnType: Option[Namespace]) derives JsonCodec


object Method:
  def apply(name: String, returnType: Option[Namespace] = None): Method =
    Method(Symbol(name), name, returnType)

