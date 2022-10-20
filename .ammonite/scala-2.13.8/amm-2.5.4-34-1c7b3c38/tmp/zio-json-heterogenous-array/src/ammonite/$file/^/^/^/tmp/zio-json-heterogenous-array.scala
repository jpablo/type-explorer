
package ammonite
package $file.^.^.^.tmp
import _root_.ammonite.interp.api.InterpBridge.{
  value => interp
}
import _root_.ammonite.interp.api.InterpBridge.value.{
  exit,
  scalaVersion
}
import _root_.ammonite.interp.api.IvyConstructor.{
  ArtifactIdExt,
  GroupIdExt
}
import _root_.ammonite.compiler.CompilerExtensions.{
  CompilerInterpAPIExtensions,
  CompilerReplAPIExtensions
}
import _root_.ammonite.runtime.tools.{
  browse,
  grep,
  time,
  tail
}
import _root_.ammonite.compiler.tools.{
  desugar,
  source
}
import _root_.mainargs.{
  arg,
  main
}
import _root_.ammonite.repl.tools.Util.{
  PathRead
}
import _root_.ammonite.repl.ReplBridge.value.{
  codeColorsImplicit
}


object `zio-json-heterogenous-array`{
/*<script>*///> using scala "3.2"
//> using lib "dev.zio::zio-json:0.3.0"
import zio.json.*
import zio.json.ast.Json
import annotation.targetName

type A = String
type B = Int

given JsonDecoder[A | B] = 
  JsonDecoder[Json].mapOrFail(j => j.as[A] orElse j.as[B])

/*<amm>*/val res_6 = /*</amm>*/println("""["a",1]""".fromJson[List[A | B]])

extension (a: Int) def show = a.toString
extension (a: String) def show = a

class MyType

extension (a: MyType) def show = "MyType"

/*<amm>*/val res_11 = /*</amm>*/println(1.show)
/*<amm>*/val res_12 = /*</amm>*/println("a".show)
/*<amm>*/val res_13 = /*</amm>*/println((new MyType).show)/*</script>*/ /*<generated>*/
def $main() = { scala.Iterator[String]() }
  override def toString = "zio$minusjson$minusheterogenous$minusarray"
  /*</generated>*/
}
