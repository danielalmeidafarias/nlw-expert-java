package com.rocketseat.certification_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.certification_nlw.modules.questions.entities.QuestionEntity;
import com.rocketseat.certification_nlw.modules.questions.repositories.QuestionRepository;
import com.rocketseat.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.rocketseat.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.rocketseat.certification_nlw.modules.students.entities.AnswerCertificationEntity;
import com.rocketseat.certification_nlw.modules.students.entities.CertificationStudentEntity;
import com.rocketseat.certification_nlw.modules.students.entities.StudentEntity;
import com.rocketseat.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import com.rocketseat.certification_nlw.modules.students.repositories.StudentyRepository;

@Service
public class StudentCertificationAnswersUseCase {

  @Autowired
  private StudentyRepository studentyRepository;

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private CertificationStudentRepository certificationStudentRepository;

  @Autowired
  private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

  public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {

    var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyHasCertificationDTO(dto.getEmail(), dto.getTechnology()));
    
    if(hasCertification) {
      throw new Exception("Você ja tirou sua certificação!");
    }

    // Buscar as alternativas das perguntas
    // - Correta ou Incorreta
    List<QuestionEntity> questionsEntity = questionRepository.findByTechnology(dto.getTechnology());
    List<AnswerCertificationEntity> answersCertifications = new ArrayList<>();

    AtomicInteger correctAnswers = new AtomicInteger(0); 

    dto.getQuestionsAnswers().stream().forEach(questionAnswer -> {
      var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionId())).findFirst()
          .get();

      var findCorrectAlternative = question.getAlternatives().stream().filter(alternative -> alternative.isCorrect()).findFirst().get();

      if(findCorrectAlternative.getId().equals(questionAnswer.getAlternativeId())) {
        questionAnswer.setCorrect(true);
        correctAnswers.incrementAndGet();
      } else {
        questionAnswer.setCorrect(false);
      }

       var answersCertificationEnntity = AnswerCertificationEntity.builder().answerId(questionAnswer.getAlternativeId()).questionId(questionAnswer.getQuestionId()).isCorrect(questionAnswer.isCorrect()).build();

       answersCertifications.add(answersCertificationEnntity);

    });

    // verificar se o estudante existe
    var student = studentyRepository.findByEmail(dto.getEmail());

    UUID student_id;

    if(student.isEmpty()) {
      var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
      studentCreated = studentyRepository.save(studentCreated);
      student_id = studentCreated.getId();
    } else {
      student_id = student.get().getId();
    }

    CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder().technology(dto.getTechnology()).studentId(student_id).grate(correctAnswers.get()).build();

    var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

    answersCertifications.stream().forEach(answerCertification -> {
      answerCertification.setCertificationId(certificationStudentEntity.getId());
      answerCertification.setCertificationStudentEntity(certificationStudentEntity);
    });   

    certificationStudentEntity.setAnswerCertificationEntities(answersCertifications);

    certificationStudentRepository.save(certificationStudentEntity);

    return certificationStudentCreated;

    // Salvar as informacoes da certificacao
  }

}
