package com.rocketseat.certification_nlw.modules.questions.controller;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.certification_nlw.modules.questions.dto.AlternativesResultDTO;
import com.rocketseat.certification_nlw.modules.questions.dto.QuestionResultDTO;
import com.rocketseat.certification_nlw.modules.questions.entities.AlternativesEntity;
import com.rocketseat.certification_nlw.modules.questions.entities.QuestionEntity;
import com.rocketseat.certification_nlw.modules.questions.repositories.QuestionRepository;

import lombok.Builder;

@RestController
@RequestMapping("/questions")
@Builder
public class QuestionController {

  @Autowired
  private QuestionRepository questionRepository;

  @GetMapping("technology/{technology}")
  public List<QuestionResultDTO> findByTechnology(@PathVariable String technology) {
    var result = this.questionRepository.findByTechnology(technology);

    var toMap = result.stream().map(question -> mapQuestionToDto(question))
        .collect(Collectors.toList());

    return toMap;
  }

  static QuestionResultDTO mapQuestionToDto(QuestionEntity questions) {
    var questionResultDTO = QuestionResultDTO.builder()
        .id(questions.getId())
        .technology(questions.getTechnology())
        .description(questions.getDescription()).build();

    List<AlternativesResultDTO> alternativesResultDTOs = questions.getAlternatives()
        .stream().map(alternative -> mapAlternativeDTO(alternative))
        .collect(Collectors.toList());

    questionResultDTO.setAlternatives(alternativesResultDTOs);

    return questionResultDTO;
  }

  static AlternativesResultDTO mapAlternativeDTO(AlternativesEntity alternativesResultDTO) {

    return AlternativesResultDTO.builder()
        .id(alternativesResultDTO.getId())
        .description(alternativesResultDTO.getDescription()).build();

  }

}
