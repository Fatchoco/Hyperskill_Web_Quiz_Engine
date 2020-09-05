package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@RestController
public class QuizController {
    private static Map<Integer,Quiz> quizzes;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuizCompletionRepository qcRepository;
    @Autowired
    private JdbcUserDetailsManager jdbcUserDetailsManager;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    static {
        quizzes = new HashMap<>();
    }

    @PostMapping(value = "api/quizzes", consumes = "application/json")
    public Quiz postQuiz(@Valid @RequestBody Quiz quiz, Principal principal) {
        quiz.setAuthor(principal.getName());
        quizRepository.save(quiz);
        return quiz;
    }

    @GetMapping("api/quizzes")
    public Iterable<Quiz> getQuizzes(@RequestParam(defaultValue = "0") Integer page) {
        int pageNo = page;
        int pageSize = 10;

        Pageable paging = PageRequest.of(pageNo, pageSize);
        return quizRepository.findAll(paging);
    }

    @GetMapping("api/quizzes/completed")
    public Iterable<QuizCompletion> getQuizzesCompletion(@RequestParam(defaultValue = "0") Integer page, Principal principal) {
        int pageNo = page;
        int pageSize = 10;
        String sortBy = "completedAt";

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        return qcRepository.findAllByUser(principal.getName(), paging);
    }

    @GetMapping("api/quizzes/{id}")
    public Quiz getQuiz(@PathVariable int id) {
        Quiz q = quizRepository.findById(id);
        if (q != null) return q;

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Quiz id is not found"
        );
    }

    @DeleteMapping(path = "api/quizzes/{id}")
    public void deleteQuiz(@PathVariable int id, Principal principal){
        Quiz q = quizRepository.findById(id);

        if(q == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!q.getAuthor().equalsIgnoreCase(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        quizRepository.delete(q);
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "api/quizzes/{id}/solve")
    public String postAnswer(@RequestBody Answer answer, @PathVariable int id, Principal principal){
        Quiz q = quizRepository.findById(id);

        if (q != null) {
            System.out.println("PostAnswer: " + Arrays.toString(answer.getAnswer()));
            System.out.println("QuizAnswer: " + Arrays.toString(q.getAnswer()));
            Arrays.sort(answer.getAnswer());
            Arrays.sort(q.getAnswer());
            if (Arrays.equals(q.getAnswer(), answer.getAnswer())) {
                QuizCompletion qc = new QuizCompletion();
                qc.setUser(principal.getName());
                qc.setId(q.id);
                qc.setCompletedAt(new Date());
                qcRepository.save(qc);
                return "{\"success\":true,\"feedback\":\"Congratulations, you're right!\"}";
            } else {
                return "{\"success\":false,\"feedback\":\"Wrong answer! Please, try again.\"}";
            }
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Quiz id is not found"
        );
    }

    @PostMapping(path = "api/register")
    public String registerUser(@Valid @RequestBody MyUser myuser) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(myuser.getRoles()));
        String encodededPassword = passwordEncoder.encode(myuser.getPassword());
        User newuser = new User(myuser.getEmail(), encodededPassword, authorities);
        try {
            jdbcUserDetailsManager.createUser(newuser);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is registered");
        }
        return "User created";
    }
}
