package callGraph

import models.*

object CallGraphExamples {

  val getUserAssignedTasks = Method("getUserAssignedTasks")
  val getUserTasks = Method("getUserTasks")

  val TaskAllocationController =
    Type("TaskAllocationController",
      methods = List(
        getUserTasks,
        getUserAssignedTasks
      )
    )

  val getAssignedTaskUnitsToUser = Method("getAssignedTaskUnitsToUser")
  val getUserTaskUnitsByProjectId = Method("getUserTaskUnitsByProjectId")
  val getUserTaskUnits = Method("getUserTaskUnits")
  val createProjectSurveySkillAttribute = Method("createProjectSurveySkillAttribute")
  val getUserNewProjectTaskUnits = Method("getUserNewProjectTaskUnits")
  val getTasksForUserInProject = Method("getTasksForUserInProject")
  val getUserSuperRatingTaskUnits = Method("getUserSuperRatingTaskUnits")

  val TaskAllocationServiceImpl =
    Type("TaskAllocationServiceImpl",
      methods = List(
        getAssignedTaskUnitsToUser,
        getUserTaskUnitsByProjectId,
        getUserTaskUnits,
        createProjectSurveySkillAttribute,
        getUserNewProjectTaskUnits,
        getTasksForUserInProject,
        getUserSuperRatingTaskUnits
      )
    )

  val updateMetricsSuperRatingStatus = Method("updateMetricsSuperRatingStatus")
  val MetricsServiceImpl =
    Type("MetricsServiceImpl",
      methods = List(
        updateMetricsSuperRatingStatus
      )
    )

  val pairs =
    List(
      getUserAssignedTasks              -> getAssignedTaskUnitsToUser,
      getUserTasks                      -> getUserTaskUnitsByProjectId,
      getUserTasks                      -> getUserTaskUnits,
      getUserTaskUnitsByProjectId       -> createProjectSurveySkillAttribute,
      getUserTaskUnits                  -> getUserNewProjectTaskUnits,
      createProjectSurveySkillAttribute -> getTasksForUserInProject,
      getUserNewProjectTaskUnits        -> getTasksForUserInProject,
      getTasksForUserInProject          -> getUserSuperRatingTaskUnits,
      getUserSuperRatingTaskUnits       -> updateMetricsSuperRatingStatus,
    )

  val callGraphExample =
    CallGraph(
      pairs = pairs,
      namesSpaces = List(
        TaskAllocationController,
        TaskAllocationServiceImpl,
        MetricsServiceImpl
      )
    )
}


