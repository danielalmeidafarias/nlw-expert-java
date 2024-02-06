package com.rocketseat.certification_nlw.modules.students.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lombok - automaticamente define os getters e setters
// @Guetter
// @Setter

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerCertificationEntity {

  private UUID id;
  private UUID certificationId;
  private UUID studentId;
  private UUID questionUuid;
  private UUID answerId;
  private boolean isCorrect;

}
