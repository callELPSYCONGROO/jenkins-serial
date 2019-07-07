package com.sensin.build.jenkinsserial.domain.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WebhookPushEventRequestBody {

    @JsonProperty("object_kind")
    private String objectKind;

    private String before;

    private String after;

    private String ref;

    @JsonProperty("checkout_sha")
    private String checkoutSha;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_username")
    private String userUsername;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("user_avatar")
    private String userAvatar;

    @JsonProperty("project_id")
    private int projectId;

    private Project project;

    private Repository repository;

    private List<Commits> commits;

    @JsonProperty("total_commits_count")
    private int totalCommitsCount;
}