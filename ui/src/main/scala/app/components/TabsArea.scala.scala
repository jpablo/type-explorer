package app.components


import com.raquo.laminar.api.L.*
import app.components.tabs.{semanticDBTab, inheritanceTab}
import org.jpablo.typeexplorer.TextDocumentsWithSource
import org.scalajs.dom

def tabsArea(
  documents: EventStream[List[TextDocumentsWithSource]],
  svgDiagram: EventStream[dom.Element]
) =
  List(
    div(
      idAttr := "te-tabs-header",
      ul(
          cls := "nav nav-tabs",
          role := "tablist",
          li(
              cls := "nav-item",
              role := "presentation",
              button(
                  cls := "nav-link active",
                  dataAttr("bs-toggle") := "tab",
                  dataAttr("bs-target") := "#classes-tab-pane",
                  tpe := "button",
                  role := "tab",
                  "Inheritance"
              )
          ),
          li(
              cls := "nav-item",
              a(
                  cls := "nav-link",
                  href := "#",
                  dataAttr("bs-toggle") := "tab",
                  dataAttr("bs-target") := "#semanticdb-tab-pane",
                  tpe := "button",
                  role := "tab",
                  "SemanticDB"
              )
          )
      )
    ),
    div(
      cls := "te-tabs-container",
      div(
        cls := "tab-content",
        div(
          idAttr := "classes-tab-pane",
          cls := "tab-pane fade show active",
          role := "tabpanel",
          tabIndex := 0,
          inheritanceTab(svgDiagram)
        ),
        div(
          idAttr := "semanticdb-tab-pane",
          cls := "tab-pane fade",
          role := "tabpanel",
          tabIndex := 0,
          semanticDBTab(documents)
        )
      )
    )
  )
  
