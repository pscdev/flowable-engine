package org.flowable.engine.test.api.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Task;
import org.flowable.engine.test.Deployment;

public class FormPropertyDefaultValueTest extends PluggableFlowableTestCase {

    @Deployment
    public void testDefaultValue() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("FormPropertyDefaultValueTest.testDefaultValue");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        TaskFormData formData = formService.getTaskFormData(task.getId());
        List<FormProperty> formProperties = formData.getFormProperties();
        assertEquals(4, formProperties.size());

        for (FormProperty prop : formProperties) {
            if ("booleanProperty".equals(prop.getId())) {
                assertEquals("true", prop.getValue());
            } else if ("stringProperty".equals(prop.getId())) {
                assertEquals("someString", prop.getValue());
            } else if ("longProperty".equals(prop.getId())) {
                assertEquals("42", prop.getValue());
            } else if ("longExpressionProperty".equals(prop.getId())) {
                assertEquals("23", prop.getValue());
            } else {
                fail("Invalid form property: " + prop.getId());
            }
        }

        Map<String, String> formDataUpdate = new HashMap<String, String>();
        formDataUpdate.put("longExpressionProperty", "1");
        formDataUpdate.put("booleanProperty", "false");
        formService.submitTaskFormData(task.getId(), formDataUpdate);

        assertEquals(false, runtimeService.getVariable(processInstance.getId(), "booleanProperty"));
        assertEquals("someString", runtimeService.getVariable(processInstance.getId(), "stringProperty"));
        assertEquals(42L, runtimeService.getVariable(processInstance.getId(), "longProperty"));
        assertEquals(1L, runtimeService.getVariable(processInstance.getId(), "longExpressionProperty"));
    }

    @Deployment
    public void testStartFormDefaultValue() throws Exception {
        String processDefinitionId = repositoryService.createProcessDefinitionQuery().processDefinitionKey("FormPropertyDefaultValueTest.testDefaultValue").latestVersion().singleResult().getId();

        StartFormData startForm = formService.getStartFormData(processDefinitionId);

        List<FormProperty> formProperties = startForm.getFormProperties();
        assertEquals(4, formProperties.size());

        for (FormProperty prop : formProperties) {
            if ("booleanProperty".equals(prop.getId())) {
                assertEquals("true", prop.getValue());
            } else if ("stringProperty".equals(prop.getId())) {
                assertEquals("someString", prop.getValue());
            } else if ("longProperty".equals(prop.getId())) {
                assertEquals("42", prop.getValue());
            } else if ("longExpressionProperty".equals(prop.getId())) {
                assertEquals("23", prop.getValue());
            } else {
                fail("Invalid form property: " + prop.getId());
            }
        }

        // Override 2 properties. The others should pe posted as the
        // default-value
        Map<String, String> formDataUpdate = new HashMap<String, String>();
        formDataUpdate.put("longExpressionProperty", "1");
        formDataUpdate.put("booleanProperty", "false");
        ProcessInstance processInstance = formService.submitStartFormData(processDefinitionId, formDataUpdate);

        assertEquals(false, runtimeService.getVariable(processInstance.getId(), "booleanProperty"));
        assertEquals("someString", runtimeService.getVariable(processInstance.getId(), "stringProperty"));
        assertEquals(42L, runtimeService.getVariable(processInstance.getId(), "longProperty"));
        assertEquals(1L, runtimeService.getVariable(processInstance.getId(), "longExpressionProperty"));
    }
}
