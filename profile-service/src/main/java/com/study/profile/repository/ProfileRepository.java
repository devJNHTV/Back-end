package com.study.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.study.profile.entity.Profile;

@EnableJpaRepositories
public interface ProfileRepository extends JpaRepository<Profile, String> {}
