

# UI
# Compile ScalaJs code to JS
sbt "ui/fullLinkJS"
# Bundle JS code with Vite
npm run build

 Server
sbt "backend/Universal/packageBin"
#sbt "backend/stage"
