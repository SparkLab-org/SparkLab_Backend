package com.sparkLab.study.activity.repository;

import com.sparkLab.study.activity.entity.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {

    List<QnaReply> findByQna_QnaIdOrderByCreateTimeAsc(Long qnaId);
}
