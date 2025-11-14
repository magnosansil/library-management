package com.biblioteca.controller;

import com.biblioteca.model.Student;
import com.biblioteca.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

  private final StudentRepository studentRepository;

  @Autowired
  public StudentController(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  /**
   * Criar um novo aluno
   * POST /api/students
   */
  @PostMapping
  public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
    // Verificar se já existe aluno com essa matrícula
    if (studentRepository.existsById(student.getMatricula())) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(null);
    }

    // Verificar se já existe aluno com esse CPF
    if (studentRepository.findByCpf(student.getCpf()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(null);
    }

    Student savedStudent = studentRepository.save(student);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
  }

  /**
   * Cadastrar múltiplos alunos de uma vez
   * POST /api/students/batch
   */
  @PostMapping("/batch")
  public ResponseEntity<Map<String, Object>> createStudentsBatch(@Valid @RequestBody List<Student> students) {
    Map<String, Object> response = new HashMap<>();
    List<Student> savedStudents = new ArrayList<>();
    List<Map<String, String>> errors = new ArrayList<>();

    for (Student student : students) {
      try {
        // Verificar se já existe aluno com essa matrícula
        if (studentRepository.existsById(student.getMatricula())) {
          Map<String, String> error = new HashMap<>();
          error.put("matricula", student.getMatricula());
          error.put("nome", student.getNome());
          error.put("message", "Aluno com esta matrícula já existe");
          errors.add(error);
          continue;
        }

        // Verificar se já existe aluno com esse CPF
        if (studentRepository.findByCpf(student.getCpf()).isPresent()) {
          Map<String, String> error = new HashMap<>();
          error.put("matricula", student.getMatricula());
          error.put("nome", student.getNome());
          error.put("message", "Aluno com este CPF já existe");
          errors.add(error);
          continue;
        }

        Student savedStudent = studentRepository.save(student);
        savedStudents.add(savedStudent);
      } catch (Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("matricula", student.getMatricula());
        error.put("nome", student.getNome());
        error.put("message", "Erro ao salvar: " + e.getMessage());
        errors.add(error);
      }
    }

    response.put("success", savedStudents.size());
    response.put("failed", errors.size());
    response.put("total", students.size());
    response.put("saved_students", savedStudents);

    if (!errors.isEmpty()) {
      response.put("errors", errors);
    }

    HttpStatus status = errors.isEmpty()
        ? HttpStatus.CREATED
        : (savedStudents.isEmpty() ? HttpStatus.BAD_REQUEST : HttpStatus.MULTI_STATUS);

    return ResponseEntity.status(status).body(response);
  }

  /**
   * Listar todos os alunos
   * GET /api/students
   */
  @GetMapping
  public ResponseEntity<List<Student>> getAllStudents() {
    List<Student> students = studentRepository.findAll();
    return ResponseEntity.ok(students);
  }

  /**
   * Buscar aluno por matrícula
   * GET /api/students/{matricula}
   */
  @GetMapping("/{matricula}")
  public ResponseEntity<Student> getStudentByMatricula(@PathVariable String matricula) {
    Optional<Student> student = studentRepository.findById(matricula);
    return student.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Atualizar aluno
   * PUT /api/students/{matricula}
   */
  @PutMapping("/{matricula}")
  public ResponseEntity<Student> updateStudent(@PathVariable String matricula,
      @Valid @RequestBody Student studentDetails) {
    Optional<Student> optionalStudent = studentRepository.findById(matricula);
    if (optionalStudent.isPresent()) {
      Student student = optionalStudent.get();
      // Não permite alterar a matrícula (chave primária)
      student.setNome(studentDetails.getNome());
      student.setCpf(studentDetails.getCpf());
      student.setDataNascimento(studentDetails.getDataNascimento());

      Student updatedStudent = studentRepository.save(student);
      return ResponseEntity.ok(updatedStudent);
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Excluir aluno por matrícula
   * DELETE /api/students/{matricula}
   */
  @DeleteMapping("/{matricula}")
  public ResponseEntity<Void> deleteStudent(@PathVariable String matricula) {
    if (studentRepository.existsById(matricula)) {
      studentRepository.deleteById(matricula);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
