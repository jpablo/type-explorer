

# UI
# Compile ScalaJs code to JS
sbt "ui/fullLinkJS"

pwd # /home/runner/work/type-explorer/type-explorer

ls -l /home/runner/work/type-explorer/type-explorer/ui/target/scala-3.3.0-RC3/ui-opt


# bien: /home/runner/work/type-explorer/type-explorer/ui/target/scala-3.3.0-RC3/ui-opt
# mal:  /home/runner/work/type-explorer/type-explorer/ui/target/scala-3.3.0-RC3/ui-opt
# Bundle JS code with Vite
npm run build

# Server
sbt "backend/Universal/packageBin"
#sbt "backend/stage"
