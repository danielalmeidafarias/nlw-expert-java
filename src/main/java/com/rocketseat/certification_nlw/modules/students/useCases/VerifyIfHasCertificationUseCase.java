package com.rocketseat.certification_nlw.modules.students.useCases;

import org.springframework.stereotype.Service;

import com.rocketseat.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;

@Service
public class VerifyIfHasCertificationUseCase {

  public boolean execute(VerifyHasCertificationDTO dto) {
    if(dto.getEmail().equals("danielalmeidafarias.dev@gmail.com") && dto.getTechnology().equals("java")) {
      return true;
    }
    return false;
  }

}