package com.sensin.build.jenkinsserial.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Gitlab项目源
 */
@Entity
@Table(name = "git_repository")
@Data
public class GitRepository implements Serializable {
	private static final long serialVersionUID = -6789526301721549472L;

	/**
	 * 主键自增ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	/**
	 * 工程路径
	 */
	@Column(name = "path_with_namespace", nullable = false)
	private String pathWithNamespace;

	/**
	 * 创建时间，status=1
	 */
	@Column(name = "create_time", nullable = false)
	private Date createTime;
}