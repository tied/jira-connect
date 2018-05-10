package com.softwaredevtools.standbot;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.softwaredevtools.standbot.model.SlackIntegrationEntity;
import com.softwaredevtools.standbot.model.pojo.SlackIntegration;
import com.google.gson.Gson;
import com.softwaredevtools.standbot.service.SlackIntegrationService;
import com.softwaredevtools.standbot.service.StandbotAPI;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Scanned
public class StandbotController {
    private final SlackIntegrationService _slackIntegrationService;
    private final StandbotAPI _standbotAPI;
    private Gson GSON;

    public StandbotController(SlackIntegrationService slackIntegrationService, StandbotAPI standbotAPI) {
        _slackIntegrationService = slackIntegrationService;
        _standbotAPI = standbotAPI;
        GSON = new Gson();
    }

    /*
        will indicate if the plugin is installed
        will be used by the bot server to know if the add-on is up and running on the Jira server
     */
    @GET
    @AnonymousAllowed
    @Path("healthcheck")
    public Response healthcheck() {
        SlackIntegration slackIntegration = _slackIntegrationService.getSlackIntegrationForHealthCheck();
        return Response.ok(GSON.toJson(slackIntegration)).build();
    }

    @POST
    @Path("activate")
    public Response activateIntegration() {
        return Response.ok().build();
    }

    @POST
    @Path("deactivate")
    public Response deactivate() {
        return Response.ok().build();
    }

    @GET
    @Path("slack/verify")
    public Response verifySlackInstallation(@QueryParam("subdomain") String subdomain) throws Exception {
        /*
        try to get the config for slack integration
         */
        SlackIntegrationEntity slackIntegrationEntity = _slackIntegrationService.getSlackIntegration();

        if (slackIntegrationEntity == null) {
            return Response.status(500).build();
        }

        String clientKey = slackIntegrationEntity.getClientKey();
        String result = _standbotAPI.searchForSlackTeam(subdomain, clientKey);

        if (result == null) {
            return Response.status(404).build();
        } else {
            return Response.ok(result).build();
        }
    }
}