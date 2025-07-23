package com.pro.project01.v2.domain.wrongnote.service;

import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteCreateRequest;
import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteResponse;
import com.pro.project01.v2.domain.wrongnote.dto.WrongNoteUpdateRequest;

import java.util.List;

public interface WrongNoteService {

    Long create(WrongNoteCreateRequest request);

    WrongNoteResponse findById(Long id);

    List<WrongNoteResponse> findByUserId(Long userId);

    void update(Long id, WrongNoteUpdateRequest request);

    void delete(Long id);
}
