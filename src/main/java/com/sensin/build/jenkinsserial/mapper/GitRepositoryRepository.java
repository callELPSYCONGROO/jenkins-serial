package com.sensin.build.jenkinsserial.mapper;

import com.sensin.build.jenkinsserial.domain.entity.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GitRepositoryRepository extends JpaRepository<GitRepository, Long>, JpaSpecificationExecutor<GitRepository> {

	GitRepository findByPathWithNamespace(String pathWithNamespace);
}