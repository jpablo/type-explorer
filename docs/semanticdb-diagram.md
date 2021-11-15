
[UML Model](../semanticdb.svg)

## Generating the uml model
Download https://github.com/tssp/protoc-gen-uml

```
cd protoc-gen-uml
export PATH=$(pwd)/target/universal/stage/bin:$PATH
cd scalameta
protoc --uml_out=/tmp -I ./scalameta/common/jvm/target/protobuf_external/ -I ./semanticdb/semanticdb/ ./semanticdb/semanticdb/semanticdb.proto
plantuml -o $PWD /tmp/complete_model.puml -tsvg
```