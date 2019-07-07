package com.sensin.build.jenkinsserial.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Jenkins项目表
 */
@Data
@Entity
@Table(name = "jenkins_project")
public class JenkinsProject implements Serializable {
	private static final long serialVersionUID = 7210418727445572011L;

	/**
	 * 主键自增ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	/**
	 * 源项目ID
	 */
	@Column(name = "git_repository_id", nullable = false)
	private Long gitRepositoryId;

	/**
	 * 项目名称
	 */
	@Column(name = "job", nullable = false)
	private String job;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time", nullable = false)
	private Date createTime;
}