/*
 * Copyright 2014 Effektif GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.effektif.workflow.impl.activity.types;

import java.util.List;

import org.joda.time.LocalDateTime;

import com.effektif.workflow.api.activities.UserTask;
import com.effektif.workflow.api.ref.UserReference;
import com.effektif.workflow.api.task.RelativeTime;
import com.effektif.workflow.api.task.Task;
import com.effektif.workflow.api.task.TaskService;
import com.effektif.workflow.api.types.ListType;
import com.effektif.workflow.api.types.TextType;
import com.effektif.workflow.api.types.UserReferenceType;
import com.effektif.workflow.api.workflow.Binding;
import com.effektif.workflow.api.xml.XmlElement;
import com.effektif.workflow.impl.WorkflowParser;
import com.effektif.workflow.impl.activity.AbstractActivityType;
import com.effektif.workflow.impl.activity.InputParameter;
import com.effektif.workflow.impl.bpmn.BpmnWriter;
import com.effektif.workflow.impl.job.Job;
import com.effektif.workflow.impl.job.JobService;
import com.effektif.workflow.impl.job.types.EscalateTaskJobType;
import com.effektif.workflow.impl.workflow.ActivityImpl;
import com.effektif.workflow.impl.workflow.BindingImpl;
import com.effektif.workflow.impl.workflowinstance.ActivityInstanceImpl;


/**
 * @author Tom Baeyens
 */
public class UserTaskImpl extends AbstractActivityType<UserTask> {
  
  public static final InputParameter<String> NAME = new InputParameter<>()
          .key("name")
          .type(new TextType());

  public static final InputParameter<List<UserReference>> ASSIGNEE = new InputParameter<>()
          .key("assignee")
          .type(new UserReferenceType());

  public static final InputParameter<List<UserReference>> CANDIDATES = new InputParameter<>()
          .key("candidates")
          .type(new ListType(new UserReferenceType()));

  public static final InputParameter<List<UserReference>> ESCALATE_TO = new InputParameter<>()
          .key("escalateTo")
          .type(new UserReferenceType());

  protected TaskService taskService;
  protected JobService jobService;
  protected BindingImpl<String> nameBinding;
  protected BindingImpl<UserReference> assigneeBinding;
  protected BindingImpl<UserReference> candidatesBinding;
  protected BindingImpl<UserReference> escalateTo;

  public UserTaskImpl() {
    super(UserTask.class);
  }
  
  @Override
  public void writeBpmn(UserTask userTask, XmlElement userTaskXml, BpmnWriter bpmnWriter) {
    bpmnWriter.setBpmnName(userTaskXml, "startEvent");
    bpmnWriter.writeBpmnAttribute(userTaskXml, "id", userTask.getId());
  }
  
  @Override
  public void parse(ActivityImpl activityImpl, UserTask userTaskApi, WorkflowParser parser) {
    super.parse(activityImpl, userTaskApi, parser);
    this.multiInstance = parser.parseMultiInstance(userTaskApi.getMultiInstance());
    this.taskService = parser.getConfiguration(TaskService.class);
    this.nameBinding = parser.parseBinding(userTaskApi.getTaskName(), NAME);
    this.assigneeBinding = parser.parseBinding(userTaskApi.getAssignee(), ASSIGNEE);
    this.candidatesBinding = parser.parseBinding(userTaskApi.getCandidates(), CANDIDATES);
    this.escalateTo = parser.parseBinding(userTaskApi.getEscalateTo(), ESCALATE_TO);
  }

  @Override
  public void execute(ActivityInstanceImpl activityInstance) {
    String taskName = activityInstance.getValue(nameBinding);
    if (taskName==null) {
      taskName = activityInstance.activity.id;
    }
    UserReference assignee = activityInstance.getValue(assigneeBinding);
    List<UserReference> candidates = activityInstance.getValues(candidatesBinding);
    if ( assignee==null 
         && candidates!=null
         && candidates.size()==1 ) {
      assignee = candidates.get(0);
    }
    
    Task task = new Task();
    task.setName(taskName);
    task.setAssignee(assignee);
    task.setCandidates(candidates);
    
    task.setActivityInstanceId(activityInstance.id);
    task.setWorkflowInstanceId(activityInstance.workflowInstance.id);
    task.setWorkflowId(activityInstance.workflow.id);
    task.setSourceWorkflowId(activityInstance.workflow.sourceWorkflowId);

    taskService.saveTask(task);

    RelativeTime escalate = activityApi.getEscalate();
    Binding<UserReference> escalateTo = activityApi.getEscalateTo();
    if (escalate!=null && escalateTo!=null) {
      LocalDateTime escalationTime = escalate.resolve();
      jobService.saveJob(new Job()        
        .duedate(escalationTime)
        .taskId(task.getId())
        .activityInstanceId(activityInstance.getId())
        .workflowInstanceId(activityInstance.getWorkflowInstance().getId())
        .workflowId(activityInstance.getWorkflowInstance().getWorkflow().getId())
        .sourceWorkflowId(activityInstance.getWorkflowInstance().getWorkflow().getSourceWorkflowId())
        .jobType(new EscalateTaskJobType())
        );
    }
  }
  
  public BindingImpl<UserReference> getEscalateTo() {
    return escalateTo;
  }
}