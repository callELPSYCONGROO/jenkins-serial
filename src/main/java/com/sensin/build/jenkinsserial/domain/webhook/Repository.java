package com.sensin.build.jenkinsserial.domain.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Repository {

    private String name;

    private String url;

    private String description;

    private String homepage;

    @JsonProperty("git_http_url")
    private String gitHttpUrl;

    @JsonProperty("git_ssh_url")
    private String gitSshUrl;

    @JsonProperty("visibility_level")
    private int visibilityLevel;
}