package com.sachin.documind.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sachin.documind.entity.ChatHistory;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

}
