documents {
  schema: SEMANTICDB4
  uri: "backend/src/main/scala/backends/graphviz/GraphvizCallGraph.scala"
  text: ""
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph().(name)"
    kind: PARAMETER
    properties: 0
    display_name: "name"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "scala/Predef.String#"
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph().(diagram)"
    kind: PARAMETER
    properties: 0
    display_name: "diagram"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "callGraph/CallGraph#"
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local0"
    kind: PARAMETER
    properties: 0
    display_name: "ns"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "models/Type#"
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local1"
    kind: LOCAL
    properties: 1024
    display_name: "subGraphs"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "scala/collection/immutable/List#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Graph#"
              }
            }
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local2"
    kind: LOCAL
    properties: 1024
    display_name: "nodes"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "scala/collection/immutable/List#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "scala/collection/immutable/Map#"
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "models/Method#"
                  }
                }
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "guru/nidi/graphviz/model/Node#"
                  }
                }
              }
            }
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local3"
    kind: LOCAL
    properties: 1024
    display_name: "combinedNodes"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "scala/collection/immutable/Map#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "models/Method#"
              }
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Node#"
              }
            }
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local4"
    kind: PARAMETER
    properties: 0
    display_name: "x$1"
    language: SCALA
    signature {
      value_signature {
        tpe {
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local5"
    kind: LOCAL
    properties: 1024
    display_name: "source"
    language: SCALA
    signature {
      value_signature {
        tpe {
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local6"
    kind: LOCAL
    properties: 1024
    display_name: "target"
    language: SCALA
    signature {
      value_signature {
        tpe {
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local7"
    kind: LOCAL
    properties: 1024
    display_name: "links"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "scala/collection/immutable/List#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Node#"
              }
            }
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph()."
    kind: METHOD
    properties: 0
    display_name: "toGraph"
    language: SCALA
    signature {
      method_signature {
        parameter_lists {
          symlinks: "backends/graphviz/GraphvizCallGraph.toGraph().(name)"
          symlinks: "backends/graphviz/GraphvizCallGraph.toGraph().(diagram)"
        }
        return_type {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Graph#"
          }
        }
      }
    }
    access {
      public_access {
      }
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph().(ns)"
    kind: PARAMETER
    properties: 0
    display_name: "ns"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "models/Type#"
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local8"
    kind: PARAMETER
    properties: 0
    display_name: "m"
    language: SCALA
    signature {
      value_signature {
        tpe {
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local9"
    kind: LOCAL
    properties: 1024
    display_name: "methods"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "scala/collection/immutable/Map#"
            type_arguments {
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Node#"
              }
            }
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "local10"
    kind: LOCAL
    properties: 1024
    display_name: "g"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Graph#"
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph()."
    kind: METHOD
    properties: 0
    display_name: "toSubgraph"
    language: SCALA
    signature {
      method_signature {
        parameter_lists {
          symlinks: "backends/graphviz/GraphvizCallGraph.toSubgraph().(ns)"
        }
        return_type {
          type_ref {
            prefix {
            }
            symbol: "scala/Tuple2#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Graph#"
              }
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "scala/collection/immutable/Map#"
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "models/Method#"
                  }
                }
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "guru/nidi/graphviz/model/Node#"
                  }
                }
              }
            }
          }
        }
      }
    }
    access {
      public_access {
      }
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph.toNode().(box)"
    kind: PARAMETER
    properties: 0
    display_name: "box"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "models/Method#"
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph.toNode()."
    kind: METHOD
    properties: 0
    display_name: "toNode"
    language: SCALA
    signature {
      method_signature {
        parameter_lists {
          symlinks: "backends/graphviz/GraphvizCallGraph.toNode().(box)"
        }
        return_type {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Node#"
          }
        }
      }
    }
    access {
      private_this_access {
      }
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph."
    kind: OBJECT
    properties: 8
    display_name: "GraphvizCallGraph"
    language: SCALA
    signature {
      class_signature {
        parents {
          type_ref {
            prefix {
            }
            symbol: "java/lang/Object#"
          }
        }
        self {
          single_type {
            prefix {
            }
            symbol: "backends/graphviz/GraphvizCallGraph."
          }
        }
        declarations {
          symlinks: "backends/graphviz/GraphvizCallGraph.`<init>`()."
          symlinks: "backends/graphviz/GraphvizCallGraph.toGraph()."
          symlinks: "backends/graphviz/GraphvizCallGraph.toSubgraph()."
          symlinks: "backends/graphviz/GraphvizCallGraph.toNode()."
        }
      }
    }
    access {
      public_access {
      }
    }
  }
  symbols {
    symbol: "local11"
    kind: LOCAL
    properties: 1024
    display_name: "g"
    language: SCALA
    signature {
      value_signature {
        tpe {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Graph#"
          }
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph$package.graphVizCallGraphExample()."
    kind: METHOD
    properties: 0
    display_name: "graphVizCallGraphExample"
    annotations {
      tpe {
        type_ref {
          prefix {
          }
          symbol: "scala/main#"
        }
      }
    }
    language: SCALA
    signature {
      value_signature {
        tpe {
          by_name_type {
            tpe {
              type_ref {
                prefix {
                }
                symbol: "java/io/File#"
              }
            }
          }
        }
      }
    }
    access {
      public_access {
      }
    }
  }
  symbols {
    symbol: "backends/graphviz/GraphvizCallGraph$package."
    kind: PACKAGE_OBJECT
    properties: 8
    display_name: "graphviz"
    language: SCALA
    signature {
      class_signature {
        parents {
          type_ref {
            prefix {
            }
            symbol: "java/lang/Object#"
          }
        }
        self {
          single_type {
            prefix {
            }
            symbol: "backends/graphviz/GraphvizCallGraph$package."
          }
        }
        declarations {
          symlinks: "backends/graphviz/GraphvizCallGraph$package.`<init>`()."
          symlinks: "backends/graphviz/GraphvizCallGraph$package.graphVizCallGraphExample()."
        }
      }
    }
    access {
    }
  }
  symbols {
    symbol: "backends/graphviz/graphVizCallGraphExample#"
    kind: CLASS
    properties: 8
    display_name: "graphVizCallGraphExample"
    language: SCALA
    signature {
      class_signature {
        parents {
          type_ref {
            prefix {
            }
            symbol: "java/lang/Object#"
          }
        }
        self {
          type_ref {
            prefix {
            }
            symbol: "backends/graphviz/graphVizCallGraphExample#"
          }
        }
        declarations {
          symlinks: "backends/graphviz/graphVizCallGraphExample#`<init>`()."
          symlinks: "backends/graphviz/graphVizCallGraphExample#main()."
        }
      }
    }
    access {
      public_access {
      }
    }
  }
  occurrences {
    range {
      start_line: 2
      start_character: 7
      end_line: 2
      end_character: 15
    }
    symbol: "backends/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 2
      start_character: 16
      end_line: 2
      end_character: 24
    }
    symbol: "backends/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 2
      start_character: 25
      end_line: 2
      end_character: 42
    }
    symbol: "backends/graphviz/GraphvizCallGraph."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 2
      start_character: 43
      end_line: 2
      end_character: 50
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 3
      start_character: 7
      end_line: 3
      end_character: 16
    }
    symbol: "callGraph/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 3
      start_character: 18
      end_line: 3
      end_character: 27
    }
    symbol: "callGraph/CallGraph."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 3
      start_character: 18
      end_line: 3
      end_character: 27
    }
    symbol: "callGraph/CallGraph#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 3
      start_character: 29
      end_line: 3
      end_character: 46
    }
    symbol: "callGraph/CallGraphExamples."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 4
      start_character: 7
      end_line: 4
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 4
      start_character: 12
      end_line: 4
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 4
      start_character: 17
      end_line: 4
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 4
      start_character: 26
      end_line: 4
      end_character: 35
    }
    symbol: "guru/nidi/graphviz/attribute/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 4
      start_character: 36
      end_line: 4
      end_character: 41
    }
    symbol: "guru/nidi/graphviz/attribute/Label#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 5
      start_character: 7
      end_line: 5
      end_character: 13
    }
    symbol: "models/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 5
      start_character: 15
      end_line: 5
      end_character: 21
    }
    symbol: "models/Method."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 5
      start_character: 15
      end_line: 5
      end_character: 21
    }
    symbol: "models/Method#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 5
      start_character: 23
      end_line: 5
      end_character: 27
    }
    symbol: "models/Type."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 5
      start_character: 23
      end_line: 5
      end_character: 27
    }
    symbol: "models/Type#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 6
      start_character: 7
      end_line: 6
      end_character: 16
    }
    symbol: "scalatags/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 6
      start_character: 17
      end_line: 6
      end_character: 21
    }
    symbol: "scalatags/Text."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 7
      start_character: 7
      end_line: 7
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 7
      start_character: 12
      end_line: 7
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 7
      start_character: 17
      end_line: 7
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 7
      start_character: 26
      end_line: 7
      end_character: 35
    }
    symbol: "guru/nidi/graphviz/attribute/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 7
      start_character: 36
      end_line: 7
      end_character: 40
    }
    symbol: "guru/nidi/graphviz/attribute/Rank#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 7
      start_character: 41
      end_line: 7
      end_character: 48
    }
    symbol: "guru/nidi/graphviz/attribute/Rank#RankDir#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 7
      end_line: 8
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 12
      end_line: 8
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 17
      end_line: 8
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 26
      end_line: 8
      end_character: 31
    }
    symbol: "guru/nidi/graphviz/model/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 33
      end_line: 8
      end_character: 38
    }
    symbol: "guru/nidi/graphviz/model/Graph#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 40
      end_line: 8
      end_character: 44
    }
    symbol: "guru/nidi/graphviz/model/Link#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 46
      end_line: 8
      end_character: 56
    }
    symbol: "guru/nidi/graphviz/model/LinkSource#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 58
      end_line: 8
      end_character: 68
    }
    symbol: "guru/nidi/graphviz/model/LinkTarget#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 70
      end_line: 8
      end_character: 74
    }
    symbol: "guru/nidi/graphviz/model/Node#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 8
      start_character: 76
      end_line: 8
      end_character: 84
    }
    symbol: "guru/nidi/graphviz/model/PortNode#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 9
      start_character: 7
      end_line: 9
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 9
      start_character: 12
      end_line: 9
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 9
      start_character: 17
      end_line: 9
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 9
      start_character: 26
      end_line: 9
      end_character: 31
    }
    symbol: "guru/nidi/graphviz/model/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 9
      start_character: 32
      end_line: 9
      end_character: 39
    }
    symbol: "guru/nidi/graphviz/model/Factory#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 10
      start_character: 7
      end_line: 10
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 10
      start_character: 12
      end_line: 10
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 10
      start_character: 17
      end_line: 10
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 10
      start_character: 26
      end_line: 10
      end_character: 35
    }
    symbol: "guru/nidi/graphviz/attribute/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 10
      start_character: 36
      end_line: 10
      end_character: 43
    }
    symbol: "guru/nidi/graphviz/attribute/Records#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 7
      end_line: 11
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 12
      end_line: 11
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 17
      end_line: 11
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 26
      end_line: 11
      end_character: 35
    }
    symbol: "guru/nidi/graphviz/attribute/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 37
      end_line: 11
      end_character: 42
    }
    symbol: "guru/nidi/graphviz/attribute/Arrow#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 44
      end_line: 11
      end_character: 49
    }
    symbol: "guru/nidi/graphviz/attribute/Color#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 51
      end_line: 11
      end_character: 55
    }
    symbol: "guru/nidi/graphviz/attribute/Rank#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 57
      end_line: 11
      end_character: 64
    }
    symbol: "guru/nidi/graphviz/attribute/Records#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 66
      end_line: 11
      end_character: 71
    }
    symbol: "guru/nidi/graphviz/attribute/Shape#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 11
      start_character: 73
      end_line: 11
      end_character: 78
    }
    symbol: "guru/nidi/graphviz/attribute/Style#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 12
      start_character: 7
      end_line: 12
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 12
      start_character: 12
      end_line: 12
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 12
      start_character: 17
      end_line: 12
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 12
      start_character: 26
      end_line: 12
      end_character: 32
    }
    symbol: "guru/nidi/graphviz/engine/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 12
      start_character: 33
      end_line: 12
      end_character: 39
    }
    symbol: "guru/nidi/graphviz/engine/Format#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 13
      start_character: 7
      end_line: 13
      end_character: 11
    }
    symbol: "guru/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 13
      start_character: 12
      end_line: 13
      end_character: 16
    }
    symbol: "guru/nidi/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 13
      start_character: 17
      end_line: 13
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 13
      start_character: 26
      end_line: 13
      end_character: 32
    }
    symbol: "guru/nidi/graphviz/engine/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 13
      start_character: 33
      end_line: 13
      end_character: 41
    }
    symbol: "guru/nidi/graphviz/engine/Graphviz#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 15
      start_character: 7
      end_line: 15
      end_character: 11
    }
    symbol: "java/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 15
      start_character: 12
      end_line: 15
      end_character: 14
    }
    symbol: "java/io/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 15
      start_character: 15
      end_line: 15
      end_character: 19
    }
    symbol: "java/io/File#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 19
      start_character: 20
      end_line: 19
      end_character: 26
    }
    symbol: "scala/Predef.String#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 19
      start_character: 14
      end_line: 19
      end_character: 18
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph().(name)"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 19
      start_character: 37
      end_line: 19
      end_character: 46
    }
    symbol: "callGraph/CallGraph#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 19
      start_character: 28
      end_line: 19
      end_character: 35
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph().(diagram)"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 19
      start_character: 49
      end_line: 19
      end_character: 54
    }
    symbol: "guru/nidi/graphviz/model/Graph#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 21
      start_character: 6
      end_line: 21
      end_character: 13
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph().(diagram)"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 21
      start_character: 14
      end_line: 21
      end_character: 25
    }
    symbol: "callGraph/CallGraph#namesSpaces."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 22
      start_character: 9
      end_line: 22
      end_character: 12
    }
    symbol: "scala/collection/immutable/List#map()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 22
      start_character: 13
      end_line: 22
      end_character: 23
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 23
      start_character: 9
      end_line: 23
      end_character: 14
    }
    symbol: "scala/collection/StrictOptimizedIterableOps#unzip()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 20
      start_character: 9
      end_line: 20
      end_character: 18
    }
    symbol: "local1"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 20
      start_character: 20
      end_line: 20
      end_character: 25
    }
    symbol: "local2"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 25
      start_character: 23
      end_line: 25
      end_character: 26
    }
    symbol: "scala/Predef.Map#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 25
      start_character: 27
      end_line: 25
      end_character: 33
    }
    symbol: "models/Method#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 25
      start_character: 35
      end_line: 25
      end_character: 39
    }
    symbol: "guru/nidi/graphviz/model/Node#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 26
      start_character: 6
      end_line: 26
      end_character: 11
    }
    symbol: "local2"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 26
      start_character: 12
      end_line: 26
      end_character: 20
    }
    symbol: "scala/collection/LinearSeqOps#foldLeft()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 26
      start_character: 21
      end_line: 26
      end_character: 24
    }
    symbol: "scala/Predef.Map."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 26
      start_character: 25
      end_line: 26
      end_character: 30
    }
    symbol: "scala/collection/immutable/Map.empty()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 26
      start_character: 34
      end_line: 26
      end_character: 36
    }
    symbol: "scala/collection/MapOps#`++`()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 25
      start_character: 8
      end_line: 25
      end_character: 21
    }
    symbol: "local3"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 29
      start_character: 30
      end_line: 29
      end_character: 37
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph().(diagram)"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 29
      start_character: 38
      end_line: 29
      end_character: 43
    }
    symbol: "callGraph/CallGraph#pairs."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 29
      start_character: 11
      end_line: 29
      end_character: 17
    }
    symbol: "local5"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 29
      start_character: 19
      end_line: 29
      end_character: 25
    }
    symbol: "local6"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 30
      start_character: 8
      end_line: 30
      end_character: 21
    }
    symbol: "local3"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 30
      start_character: 22
      end_line: 30
      end_character: 28
    }
    symbol: "local5"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 30
      start_character: 30
      end_line: 30
      end_character: 34
    }
    symbol: "guru/nidi/graphviz/model/Node#link()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 30
      start_character: 35
      end_line: 30
      end_character: 37
    }
    symbol: "guru/nidi/graphviz/model/Factory#to()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 30
      start_character: 38
      end_line: 30
      end_character: 51
    }
    symbol: "local3"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 30
      start_character: 52
      end_line: 30
      end_character: 58
    }
    symbol: "local6"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 28
      start_character: 8
      end_line: 28
      end_character: 13
    }
    symbol: "local7"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 32
      start_character: 4
      end_line: 32
      end_character: 9
    }
    symbol: "guru/nidi/graphviz/model/Factory#graph(+1)."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 32
      start_character: 10
      end_line: 32
      end_character: 14
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph().(name)"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 33
      start_character: 7
      end_line: 33
      end_character: 15
    }
    symbol: "guru/nidi/graphviz/model/Graph#directed()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 7
      end_line: 34
      end_character: 15
    }
    symbol: "guru/nidi/graphviz/model/Graph#nodeAttr()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 16
      end_line: 34
      end_character: 22
    }
    symbol: "guru/nidi/graphviz/attribute/Attributed#with(+2)."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 23
      end_line: 34
      end_character: 28
    }
    symbol: "guru/nidi/graphviz/attribute/Style#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 29
      end_line: 34
      end_character: 35
    }
    symbol: "guru/nidi/graphviz/attribute/Style#FILLED."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 37
      end_line: 34
      end_character: 42
    }
    symbol: "guru/nidi/graphviz/attribute/Shape#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 43
      end_line: 34
      end_character: 47
    }
    symbol: "guru/nidi/graphviz/attribute/Shape#RECT."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 49
      end_line: 34
      end_character: 54
    }
    symbol: "guru/nidi/graphviz/attribute/Color#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 55
      end_line: 34
      end_character: 58
    }
    symbol: "guru/nidi/graphviz/attribute/Color#rgb()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 34
      start_character: 70
      end_line: 34
      end_character: 74
    }
    symbol: "guru/nidi/graphviz/attribute/Color#fill()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 35
      start_character: 7
      end_line: 35
      end_character: 13
    }
    symbol: "guru/nidi/graphviz/model/Graph#with()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 35
      start_character: 14
      end_line: 35
      end_character: 23
    }
    symbol: "local1"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 36
      start_character: 7
      end_line: 36
      end_character: 13
    }
    symbol: "guru/nidi/graphviz/model/Graph#with()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 36
      start_character: 14
      end_line: 36
      end_character: 19
    }
    symbol: "local7"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 19
      start_character: 6
      end_line: 19
      end_character: 13
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph()."
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 38
      start_character: 21
      end_line: 38
      end_character: 25
    }
    symbol: "models/Type#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 38
      start_character: 17
      end_line: 38
      end_character: 19
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph().(ns)"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 38
      start_character: 29
      end_line: 38
      end_character: 34
    }
    symbol: "guru/nidi/graphviz/model/Graph#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 38
      start_character: 36
      end_line: 38
      end_character: 39
    }
    symbol: "scala/Predef.Map#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 38
      start_character: 40
      end_line: 38
      end_character: 46
    }
    symbol: "models/Method#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 38
      start_character: 48
      end_line: 38
      end_character: 52
    }
    symbol: "guru/nidi/graphviz/model/Node#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 40
      start_character: 6
      end_line: 40
      end_character: 8
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph().(ns)"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 40
      start_character: 9
      end_line: 40
      end_character: 16
    }
    symbol: "models/Type#methods."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 41
      start_character: 9
      end_line: 41
      end_character: 12
    }
    symbol: "scala/collection/immutable/List#map()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 41
      start_character: 13
      end_line: 41
      end_character: 14
    }
    symbol: "local8"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 41
      start_character: 18
      end_line: 41
      end_character: 19
    }
    symbol: "local8"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 41
      start_character: 20
      end_line: 41
      end_character: 22
    }
    symbol: "scala/Predef.ArrowAssoc#`->`()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 41
      start_character: 23
      end_line: 41
      end_character: 29
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toNode()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 41
      start_character: 30
      end_line: 41
      end_character: 31
    }
    symbol: "local8"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 42
      start_character: 9
      end_line: 42
      end_character: 14
    }
    symbol: "scala/collection/IterableOnceOps#toMap()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 39
      start_character: 8
      end_line: 39
      end_character: 15
    }
    symbol: "local9"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 44
      start_character: 6
      end_line: 44
      end_character: 11
    }
    symbol: "guru/nidi/graphviz/model/Factory#graph(+1)."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 44
      start_character: 12
      end_line: 44
      end_character: 14
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph().(ns)"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 44
      start_character: 15
      end_line: 44
      end_character: 19
    }
    symbol: "models/Type#name."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 45
      start_character: 9
      end_line: 45
      end_character: 16
    }
    symbol: "guru/nidi/graphviz/model/Graph#cluster()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 46
      start_character: 9
      end_line: 46
      end_character: 18
    }
    symbol: "guru/nidi/graphviz/model/Graph#graphAttr()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 46
      start_character: 19
      end_line: 46
      end_character: 25
    }
    symbol: "guru/nidi/graphviz/attribute/Attributed#with(+6)."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 46
      start_character: 26
      end_line: 46
      end_character: 31
    }
    symbol: "guru/nidi/graphviz/attribute/Label#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 46
      start_character: 32
      end_line: 46
      end_character: 34
    }
    symbol: "guru/nidi/graphviz/attribute/Label#of()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 46
      start_character: 35
      end_line: 46
      end_character: 37
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph().(ns)"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 46
      start_character: 38
      end_line: 46
      end_character: 42
    }
    symbol: "models/Type#name."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 47
      start_character: 9
      end_line: 47
      end_character: 15
    }
    symbol: "guru/nidi/graphviz/model/Graph#with()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 47
      start_character: 16
      end_line: 47
      end_character: 23
    }
    symbol: "local9"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 47
      start_character: 24
      end_line: 47
      end_character: 30
    }
    symbol: "scala/collection/MapOps#values()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 47
      start_character: 31
      end_line: 47
      end_character: 36
    }
    symbol: "scala/collection/IterableOnceOps#toSeq()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 43
      start_character: 8
      end_line: 43
      end_character: 9
    }
    symbol: "local10"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 48
      start_character: 5
      end_line: 48
      end_character: 6
    }
    symbol: "local10"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 48
      start_character: 8
      end_line: 48
      end_character: 15
    }
    symbol: "local9"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 38
      start_character: 6
      end_line: 38
      end_character: 16
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toSubgraph()."
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 51
      start_character: 26
      end_line: 51
      end_character: 32
    }
    symbol: "models/Method#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 51
      start_character: 21
      end_line: 51
      end_character: 24
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toNode().(box)"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 51
      start_character: 35
      end_line: 51
      end_character: 39
    }
    symbol: "guru/nidi/graphviz/model/Node#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 52
      start_character: 4
      end_line: 52
      end_character: 8
    }
    symbol: "guru/nidi/graphviz/model/Factory#node()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 52
      start_character: 9
      end_line: 52
      end_character: 12
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toNode().(box)"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 52
      start_character: 13
      end_line: 52
      end_character: 17
    }
    symbol: "models/Method#name."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 51
      start_character: 14
      end_line: 51
      end_character: 20
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toNode()."
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 17
      start_character: 7
      end_line: 17
      end_character: 24
    }
    symbol: "backends/graphviz/GraphvizCallGraph."
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 54
      start_character: 4
      end_line: 54
      end_character: 21
    }
    symbol: "backends/graphviz/GraphvizCallGraph."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 57
      start_character: 1
      end_line: 57
      end_character: 5
    }
    symbol: "scala/main#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 58
      start_character: 30
      end_line: 58
      end_character: 34
    }
    symbol: "java/io/File#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 59
      start_character: 10
      end_line: 59
      end_character: 17
    }
    symbol: "backends/graphviz/GraphvizCallGraph.toGraph()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 59
      start_character: 40
      end_line: 59
      end_character: 57
    }
    symbol: "callGraph/CallGraphExamples."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 59
      start_character: 58
      end_line: 59
      end_character: 74
    }
    symbol: "callGraph/CallGraphExamples.callGraphExample."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 59
      start_character: 6
      end_line: 59
      end_character: 7
    }
    symbol: "local11"
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 60
      start_character: 2
      end_line: 60
      end_character: 10
    }
    symbol: "guru/nidi/graphviz/engine/Graphviz#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 11
      end_line: 60
      end_character: 20
    }
    symbol: "guru/nidi/graphviz/engine/Graphviz#fromGraph()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 21
      end_line: 60
      end_character: 22
    }
    symbol: "local11"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 24
      end_line: 60
      end_character: 30
    }
    symbol: "guru/nidi/graphviz/engine/Graphviz#height()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 36
      end_line: 60
      end_character: 42
    }
    symbol: "guru/nidi/graphviz/engine/Graphviz#render()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 43
      end_line: 60
      end_character: 49
    }
    symbol: "guru/nidi/graphviz/engine/Format#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 50
      end_line: 60
      end_character: 64
    }
    symbol: "guru/nidi/graphviz/engine/Format#SVG_STANDALONE."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 66
      end_line: 60
      end_character: 72
    }
    symbol: "guru/nidi/graphviz/engine/Renderer#toFile()."
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 60
      start_character: 77
      end_line: 60
      end_character: 81
    }
    symbol: "java/io/File#"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 58
      start_character: 4
      end_line: 58
      end_character: 28
    }
    symbol: "backends/graphviz/GraphvizCallGraph$package.graphVizCallGraphExample()."
    role: DEFINITION
  }
  occurrences {
    range {
      start_line: 0
      start_character: 8
      end_line: 0
      end_character: 16
    }
    symbol: "backends/"
    role: REFERENCE
  }
  occurrences {
    range {
      start_line: 0
      start_character: 17
      end_line: 0
      end_character: 25
    }
    symbol: "backends/graphviz/"
    role: DEFINITION
  }
  language: SCALA
  md5: "7CF20CB7170B43B829C323DCB22FBF3C"
  synthetics {
    range {
      start_line: 21
      start_character: 6
      end_line: 23
      end_character: 14
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 21
              start_character: 6
              end_line: 23
              end_character: 14
            }
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Graph#"
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "scala/collection/immutable/Map#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "models/Method#"
              }
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Node#"
              }
            }
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 21
      start_character: 6
      end_line: 22
      end_character: 12
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 21
              start_character: 6
              end_line: 22
              end_character: 12
            }
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "scala/Tuple2#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Graph#"
              }
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "scala/collection/immutable/Map#"
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "models/Method#"
                  }
                }
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "guru/nidi/graphviz/model/Node#"
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 21
      start_character: 6
      end_line: 23
      end_character: 14
    }
    tree {
      apply_tree {
        function {
          original_tree {
            range {
              start_line: 21
              start_character: 6
              end_line: 23
              end_character: 14
            }
          }
        }
        arguments {
          type_apply_tree {
            function {
              id_tree {
                symbol: "scala/Predef.$conforms()."
              }
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "scala/Tuple2#"
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "guru/nidi/graphviz/model/Graph#"
                  }
                }
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "scala/collection/immutable/Map#"
                    type_arguments {
                      type_ref {
                        prefix {
                        }
                        symbol: "models/Method#"
                      }
                    }
                    type_arguments {
                      type_ref {
                        prefix {
                        }
                        symbol: "guru/nidi/graphviz/model/Node#"
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 26
      start_character: 6
      end_line: 26
      end_character: 20
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 26
              start_character: 6
              end_line: 26
              end_character: 20
            }
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "scala/collection/immutable/Map#"
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "models/Method#"
              }
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Node#"
              }
            }
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 26
      start_character: 21
      end_line: 26
      end_character: 30
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 26
              start_character: 21
              end_line: 26
              end_character: 30
            }
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "models/Method#"
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Node#"
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 26
      start_character: 32
      end_line: 26
      end_character: 36
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 26
              start_character: 32
              end_line: 26
              end_character: 36
            }
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Node#"
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 40
      start_character: 6
      end_line: 42
      end_character: 14
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 40
              start_character: 6
              end_line: 42
              end_character: 14
            }
          }
        }
        type_arguments {
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Node#"
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 40
      start_character: 6
      end_line: 41
      end_character: 12
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 40
              start_character: 6
              end_line: 41
              end_character: 12
            }
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "scala/Tuple2#"
            type_arguments {
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "guru/nidi/graphviz/model/Node#"
              }
            }
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 41
      start_character: 18
      end_line: 41
      end_character: 22
    }
    tree {
      type_apply_tree {
        function {
          original_tree {
            range {
              start_line: 41
              start_character: 18
              end_line: 41
              end_character: 22
            }
          }
        }
        type_arguments {
          type_ref {
            prefix {
            }
            symbol: "guru/nidi/graphviz/model/Node#"
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 41
      start_character: 18
      end_line: 41
      end_character: 19
    }
    tree {
      apply_tree {
        function {
          type_apply_tree {
            function {
              id_tree {
                symbol: "scala/Predef.ArrowAssoc()."
              }
            }
            type_arguments {
            }
          }
        }
        arguments {
          original_tree {
            range {
              start_line: 41
              start_character: 18
              end_line: 41
              end_character: 19
            }
          }
        }
      }
    }
  }
  synthetics {
    range {
      start_line: 40
      start_character: 6
      end_line: 42
      end_character: 14
    }
    tree {
      apply_tree {
        function {
          original_tree {
            range {
              start_line: 40
              start_character: 6
              end_line: 42
              end_character: 14
            }
          }
        }
        arguments {
          type_apply_tree {
            function {
              id_tree {
                symbol: "scala/`<:<`.refl()."
              }
            }
            type_arguments {
              type_ref {
                prefix {
                }
                symbol: "scala/Tuple2#"
                type_arguments {
                }
                type_arguments {
                  type_ref {
                    prefix {
                    }
                    symbol: "guru/nidi/graphviz/model/Node#"
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

