package com.sensin.build.jenkinsserial.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 构建队列，每条记录是一个流程，创建->开始执行->结束
 */
@Table(name = "job_exec_queue")
@Data
@Entity
public class JobExecQueue implements Serializable {
	private static final long serialVersionUID = 1619206015986472131L;

	/**
	 * 主键自增ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	/**
	 * jenkins_project_id表ID
	 */
	@Column(name = "jenkins_project_id", nullable = false)
	private Long jenkinsProjectId;

	/**
	 * jenkins中job的构建编号
	 */
	@Column(name = "build_number")
	private Integer buildNumber;

	/**
	 * 触发事件，webhook中请求头X-Gitlab-Event字段值
	 */
	@Column(name = "trigger_event", nullable = false)
	private String triggerEvent;

	/**
	 * 状态，1-初始化，2-执行中，3-执行完成，4-放弃执行，5-执行失败
	 */
	@Column(name = "status", nullable = false)
	private Integer status = 1;

	/**
	 * 创建时间，status=1
	 */
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	/**
	 * 开始执行时间，status=2
	 */
	@Column(name = "start_exec_time")
	private Date startExecTime;

	/**
	 * 执行结束时间，status=3、4、5
	 */
	@Column(name = "end_exec_time")
	private Date endExecTime;
}