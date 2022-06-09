package app.components


import com.raquo.laminar.api.L.*
import app.components.tabs.{semanticDBTab, inheritanceTab}
import scala.meta.internal.semanticdb.TextDocument
import org.scalajs.dom

def tabsArea(
  documents: EventStream[List[TextDocument]],
  svgDiagram: EventStream[dom.Element]
) =
  div(
    idAttr := "te-tabs-area",
    ul(
        cls := "nav nav-tabs",
        role := "tablist",
        li(
            cls := "nav-item",
            role := "presentation",
            button(
                cls := "nav-link active",
                dataAttr("bs-toggle") := "tab",
                dataAttr("bs-target") := "#packages-tab-pane",
                tpe := "button",
                role := "tab",
                "Packages"
            )
        ),
        li(
            cls := "nav-item",
            role := "presentation",
            button(
                cls := "nav-link",
                dataAttr("bs-toggle") := "tab",
                dataAttr("bs-target") := "#classes-tab-pane",
                tpe := "button",
                role := "tab",
                "Inheritance"
            )
        ),
        // li(
        //     cls := "nav-item",
        //     a(
        //         cls := "nav-link",
        //         href := "#",
        //         dataAttr("bs-toggle") := "tab",
        //         "Inheritance"
        //     )
        // ),
        // li(
        //     cls := "nav-item",
        //     a(
        //         cls := "nav-link",
        //         href := "#",
        //         dataAttr("bs-toggle") := "tab",
        //         "Call Graph"
        //     )
        // ),
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
    ),
    div(
      cls := "tab-content",
      div(
        idAttr := "packages-tab-pane",
        cls := "tab-pane fade show active",
        role := "tabpanel",
        tabIndex := 0,
        "Packages ..."
      ),
      div(
        idAttr := "classes-tab-pane",
        cls := "tab-pane fade",
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
  
