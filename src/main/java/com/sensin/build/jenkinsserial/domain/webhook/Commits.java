package com.sensin.build.jenkinsserial.domain.webhook;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Commits {

    private String id;

    private String message;

    private Date timestamp;

    private String url;

    private Author author;

    private List<String> added;

    private List<String> modified;

    private List<String> removed;

}