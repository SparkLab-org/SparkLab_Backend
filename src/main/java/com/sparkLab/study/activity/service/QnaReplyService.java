package com.sparkLab.study.activity.service;

import com.sparkLab.study.activity.dto.qna.QnaReplyCreateRequest;
import com.sparkLab.study.activity.dto.qna.QnaReplyResponse;
import com.sparkLab.study.activity.entity.Qna;
import com.sparkLab.study.activity.entity.QnaReply;
import com.sparkLab.study.activity.repository.QnaRepository;
import com.sparkLab.study.activity.repository.QnaReplyRepository;
import com.sparkLab.study.common.exception.NotOwnerException;
import com.sparkLab.study.common.exception.ParentResourceNotFoundException;
import com.sparkLab.study.user.entity.Mentor;
import com.sparkLab.study.user.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaReplyService {

    private final QnaRepository qnaRepository;
    private final QnaReplyRepository qnaReplyRepository;
    private final MentorRepository mentorRepository;
    private final QnaService qnaService;
    private final NotificationService notificationService;

    @Transactional
    public QnaReplyResponse create(Long qnaId, QnaReplyCreateRequest request, Long mentorId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ParentResourceNotFoundException("Qna", qnaId));
        if (qna.getMentee() == null || qna.getMentee().getMentor() == null
                || !qna.getMentee().getMentor().getMentorId().equals(mentorId)) {
            throw new NotOwnerException("Qna", qnaId);
        }
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new ParentResourceNotFoundException("Mentor", mentorId));
        QnaReply reply = new QnaReply();
        reply.setQna(qna);
        reply.setMentor(mentor);
        reply.setContent(request.getContent());
        QnaReply saved = qnaReplyRepository.save(reply);
        qna.markAnswered();
        qnaRepository.save(qna);
        notificationService.notifyMenteeQuestionAnswered(qna.getMentee(), qnaId);
        return qnaService.toReplyResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QnaReplyResponse> list(Long qnaId, Long menteeId, Long mentorId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ParentResourceNotFoundException("Qna", qnaId));
        if (menteeId != null && (qna.getMentee() == null || !qna.getMentee().getMenteeId().equals(menteeId))) {
            throw new NotOwnerException("Qna", qnaId);
        }
        if (mentorId != null && (qna.getMentee() == null || qna.getMentee().getMentor() == null || !qna.getMentee().getMentor().getMentorId().equals(mentorId))) {
            throw new NotOwnerException("Qna", qnaId);
        }
        return qnaReplyRepository.findByQna_QnaIdOrderByCreateTimeAsc(qnaId).stream()
                .map(qnaService::toReplyResponse)
                .collect(Collectors.toList());
    }
}
