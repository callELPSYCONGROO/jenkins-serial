package com.sensin.build.jenkinsserial.domain.command;

import com.sensin.build.jenkinsserial.domain.group.InsertGroup;
import com.sensin.build.jenkinsserial.domain.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author 無痕剑
 * @date 2019/7/7 23:30
 */
@Data
public class ConfigGitRepositoryAndJobCommand {

	@Null(message = "非法参数", groups = InsertGroup.class)
	@NotNull(message = "缺少关键参数", groups = UpdateGroup.class)
	private Long jenkinsProjectId;

	/**
	 * 工程路径
	 */
	@NotBlank(message = "工程源路径不允许为空", groups = {InsertGroup.class, UpdateGroup.class})
	private String pathWithNamespace;

	/**
	 * 项目名称
	 */
	@NotBlank(message = "jenkins项目名称不允许为空", groups = {InsertGroup.class, UpdateGroup.class})
	private String job;
}
