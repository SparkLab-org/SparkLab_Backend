package com.sparkLab.study.activity.repository;

import com.sparkLab.study.activity.entity.Qna;
import com.sparkLab.study.common.constant.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    List<Qna> findByMentee_MenteeIdOrderByCreateTimeDesc(Long menteeId);

    List<Qna> findByMentee_MenteeIdAndSubjectOrderByCreateTimeDesc(Long menteeId, Subject subject);

    List<Qna> findByMentee_Mentor_MentorIdOrderByCreateTimeDesc(Long mentorId);

    List<Qna> findByMentee_Mentor_MentorIdAndSubjectOrderByCreateTimeDesc(Long mentorId, Subject subject);
}
