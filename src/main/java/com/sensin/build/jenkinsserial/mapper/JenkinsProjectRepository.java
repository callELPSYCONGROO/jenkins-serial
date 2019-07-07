package com.sensin.build.jenkinsserial.mapper;

import com.sensin.build.jenkinsserial.domain.entity.JenkinsProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JenkinsProjectRepository extends JpaRepository<JenkinsProject, Long>, JpaSpecificationExecutor<JenkinsProject> {

	JenkinsProject findByJob(String job);
}