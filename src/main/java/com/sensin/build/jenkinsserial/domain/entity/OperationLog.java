package com.sensin.build.jenkinsserial.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志
 */
@Entity
@Data
@Table(name = "operation_log")
public class OperationLog implements Serializable {
	private static final long serialVersionUID = 4154258382632715746L;

	/**
	 * 主键自增ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	/**
	 * 执行状态，对应job_exec_queue的status字段
	 */
	@Column(name = "exec_status", nullable = false)
	private Integer execStatus;

	/**
	 * 构建队列ID，对应job_exec_queue的id字段
	 */
	@Column(name = "job_exec_queue_id", nullable = false)
	private Long jobExecQueueId;

	/**
	 * 执行内容描述
	 */
	@Column(name = "exec_content")
	private String execContent;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time", nullable = false)
	private Date createTime;
}