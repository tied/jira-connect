package com.softwaredevtools.standbot.webpabel;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.softwaredevtools.standbot.model.SlackIntegrationEntity;
import com.softwaredevtools.standbot.service.SlackIntegrationService;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class Report implements WebPanel {

    private final TemplateRenderer templateRenderer;
    private final SlackIntegrationService _slackIntegrationService;
    private ProjectManager _projectManager;


    public Report(TemplateRenderer renderer, SlackIntegrationService slackIntegrationService) {
        _slackIntegrationService = slackIntegrationService;
        this.templateRenderer = renderer;
        _projectManager = ComponentAccessor.getProjectManager();
    }

    public String getHtml(Map<String, Object> map) {
        /*
        try to get the config for slack integration or generate a new one if not exists
         */
        SlackIntegrationEntity slackIntegrationEntity = _slackIntegrationService.generateSlackIntegrationIfNotExists();
        System.out.println("Using client key: " + slackIntegrationEntity.getClientKey());

        String projectKey = (String) map.getOrDefault("projectKey", null);

        if (projectKey == null) {
            return null;
        }

        Project project = _projectManager.getProjectObjByKey(projectKey);

        if (project == null) {
            return null;
        }

        map.put("projectId", project.getId());
        map.put("clientKey", slackIntegrationEntity.getClientKey());

        StringWriter stringWriter = new StringWriter();
        try {
            templateRenderer.render("templates/report.vm", map, stringWriter);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringWriter.toString();
    }

    public void writeHtml(Writer writer, Map<String, Object> map) throws IOException {
        writer.write(getHtml(map));
    }
}
