package com.sensin.build.jenkinsserial.mapper;

import com.sensin.build.jenkinsserial.domain.entity.JobExecQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecQueueRepository extends JpaRepository<JobExecQueue, Long>, JpaSpecificationExecutor<JobExecQueue> {

	List<JobExecQueue> findByStatus(Integer status);

	JobExecQueue findTopByStatusOrderByCreateTimeAsc(Integer status);
}