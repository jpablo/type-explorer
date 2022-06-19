package models

//import io.circe.*, io.circe.generic.semiauto.*

enum NamespaceKind:
  case Object
  case PackageObject
  case Package
  case Class
  case Trait
  case Unknown
  case Other(name: String)


opaque type Symbol = String

object Symbol:
  def apply(value: String): Symbol = value
  def empty: Symbol = ""

  extension (s: Symbol)
    def toString: String = s

case class Package(name: String)

//object Package:
//  given Codec[Package] = deriveCodec


case class Namespace(
  symbol     : Symbol,
  displayName: String,
  kind       : NamespaceKind = NamespaceKind.Class,
  methods    : List[Method] = List.empty
)

//object Type:
//  given Codec[Type] = deriveCodec



case class Method(symbol: Symbol, displayName: String, returnType: Option[Namespace])

object Method:
  def apply(name: String, returnType: Option[Namespace] = None): Method =
    Method(Symbol(name), name, returnType)

//object Method:
//  given Codec[Method] = deriveCodec
