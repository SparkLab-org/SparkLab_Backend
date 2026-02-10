package com.sparkLab.study.activity.service;

import com.sparkLab.study.activity.constant.QnaStatus;
import com.sparkLab.study.activity.dto.qna.*;
import com.sparkLab.study.activity.entity.Qna;
import com.sparkLab.study.activity.entity.QnaReply;
import com.sparkLab.study.activity.repository.QnaRepository;
import com.sparkLab.study.activity.repository.QnaReplyRepository;
import com.sparkLab.study.common.constant.Subject;
import com.sparkLab.study.common.exception.NotOwnerException;
import com.sparkLab.study.common.exception.ParentResourceNotFoundException;
import com.sparkLab.study.user.entity.Mentee;
import com.sparkLab.study.user.repository.MenteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final QnaReplyRepository qnaReplyRepository;
    private final MenteeRepository menteeRepository;
    private final NotificationService notificationService;

    @Transactional
    public QnaResponse create(QnaCreateRequest request, Long menteeId) {
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new ParentResourceNotFoundException("Mentee", menteeId));
        Qna qna = new Qna();
        qna.setSubject(request.getSubject());
        qna.setTitle(request.getTitle());
        qna.setContent(request.getContent());
        qna.setAttachmentUrl(request.getAttachmentUrl());
        qna.setMentee(mentee);
        qna.setStatus(QnaStatus.PENDING);
        Qna saved = qnaRepository.save(qna);
        notificationService.notifyMentorQuestionCreated(mentee, saved.getQnaId());
        return toDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<QnaListResponse> list(Long menteeId, Long mentorId, Subject subject, String sort) {
        List<Qna> list;
        if (menteeId != null) {
            list = subject != null
                    ? qnaRepository.findByMentee_MenteeIdAndSubjectOrderByCreateTimeDesc(menteeId, subject)
                    : qnaRepository.findByMentee_MenteeIdOrderByCreateTimeDesc(menteeId);
        } else if (mentorId != null) {
            list = subject != null
                    ? qnaRepository.findByMentee_Mentor_MentorIdAndSubjectOrderByCreateTimeDesc(mentorId, subject)
                    : qnaRepository.findByMentee_Mentor_MentorIdOrderByCreateTimeDesc(mentorId);
        } else {
            list = qnaRepository.findAll();
            if ("latest".equals(sort)) {
                list = list.stream().sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime())).collect(Collectors.toList());
            }
        }
        if (menteeId != null && !"latest".equals(sort)) {
            list = list.stream().sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime())).collect(Collectors.toList());
        }
        return list.stream().map(this::toListResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QnaResponse getOne(Long qnaId, Long menteeId, Long mentorId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ParentResourceNotFoundException("Qna", qnaId));
        if (menteeId != null && (qna.getMentee() == null || !qna.getMentee().getMenteeId().equals(menteeId))) {
            throw new NotOwnerException("Qna", qnaId);
        }
        if (mentorId != null && (qna.getMentee() == null || qna.getMentee().getMentor() == null || !qna.getMentee().getMentor().getMentorId().equals(mentorId))) {
            throw new NotOwnerException("Qna", qnaId);
        }
        return toDetailResponse(qna);
    }

    @Transactional
    public QnaResponse update(Long qnaId, QnaUpdateRequest request, Long menteeId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ParentResourceNotFoundException("Qna", qnaId));
        if (qna.getMentee() == null || !qna.getMentee().getMenteeId().equals(menteeId)) {
            throw new NotOwnerException("Qna", qnaId);
        }
        if (request.getTitle() != null) qna.setTitle(request.getTitle());
        if (request.getContent() != null) qna.setContent(request.getContent());
        if (request.getAttachmentUrl() != null) qna.setAttachmentUrl(request.getAttachmentUrl());
        return toDetailResponse(qnaRepository.save(qna));
    }

    @Transactional
    public void delete(Long qnaId, Long menteeId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new ParentResourceNotFoundException("Qna", qnaId));
        if (qna.getMentee() == null || !qna.getMentee().getMenteeId().equals(menteeId)) {
            throw new NotOwnerException("Qna", qnaId);
        }
        qnaRepository.delete(qna);
    }

    QnaResponse toDetailResponse(Qna qna) {
        List<QnaReplyResponse> replies = qnaReplyRepository.findByQna_QnaIdOrderByCreateTimeAsc(qna.getQnaId())
                .stream()
                .map(this::toReplyResponse)
                .collect(Collectors.toList());
        return QnaResponse.builder()
                .qnaId(qna.getQnaId())
                .subject(qna.getSubject())
                .title(qna.getTitle())
                .content(qna.getContent())
                .attachmentUrl(qna.getAttachmentUrl())
                .menteeId(qna.getMentee() != null ? qna.getMentee().getMenteeId() : null)
                .status(qna.getStatus())
                .statusDisplayName(qna.getStatus().getDisplayName())
                .answeredAt(qna.getAnsweredAt())
                .createTime(qna.getCreateTime())
                .updateTime(qna.getUpdateTime())
                .replies(replies)
                .build();
    }

    private QnaListResponse toListResponse(Qna qna) {
        return QnaListResponse.builder()
                .qnaId(qna.getQnaId())
                .subject(qna.getSubject())
                .title(qna.getTitle())
                .content(qna.getContent())
                .status(qna.getStatus())
                .statusDisplayName(qna.getStatus().getDisplayName())
                .createTime(qna.getCreateTime())
                .build();
    }

    QnaReplyResponse toReplyResponse(QnaReply reply) {
        return QnaReplyResponse.builder()
                .qnaReplyId(reply.getQnaReplyId())
                .mentorId(reply.getMentor() != null ? reply.getMentor().getMentorId() : null)
                .content(reply.getContent())
                .createTime(reply.getCreateTime())
                .updateTime(reply.getUpdateTime())
                .build();
    }
}
