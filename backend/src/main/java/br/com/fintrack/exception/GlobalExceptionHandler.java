package br.com.fintrack.exception;
import jakarta.validation.ConstraintViolationException; import org.springframework.dao.DataIntegrityViolationException; import org.springframework.http.*; import org.springframework.security.access.AccessDeniedException; import org.springframework.security.authentication.BadCredentialsException; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.stream.Collectors;
@RestControllerAdvice public class GlobalExceptionHandler{
 public record Erro(Instant timestamp,int status,String erro,String mensagem){}
 @ExceptionHandler(ResourceNotFoundException.class) @ResponseStatus(HttpStatus.NOT_FOUND) Erro notFound(ResourceNotFoundException e){return erro(404,"Recurso não encontrado",e.getMessage());}
 @ExceptionHandler({ValidationException.class,SaldoInsuficienteException.class,ConstraintViolationException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST) Erro badRequest(Exception e){return erro(400,"Requisição inválida",e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) Erro validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(Collectors.joining("; "));return erro(400,"Dados inválidos",m);}
 @ExceptionHandler({ConflitoException.class,DataIntegrityViolationException.class}) @ResponseStatus(HttpStatus.CONFLICT) Erro conflict(Exception e){return erro(409,"Conflito",e instanceof ConflitoException?e.getMessage():"A operação viola uma restrição de dados.");}
 @ExceptionHandler(BadCredentialsException.class) @ResponseStatus(HttpStatus.UNAUTHORIZED) Erro unauthorized(BadCredentialsException e){return erro(401,"Não autorizado","E-mail ou senha inválidos.");}
 @ExceptionHandler(AccessDeniedException.class) @ResponseStatus(HttpStatus.FORBIDDEN) Erro forbidden(AccessDeniedException e){return erro(403,"Acesso negado","Você não pode acessar recursos de outro usuário.");}
 @ExceptionHandler(Exception.class) @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) Erro internal(Exception e){return erro(500,"Erro interno","Ocorreu um erro inesperado.");}
 private Erro erro(int s,String e,String m){return new Erro(Instant.now(),s,e,m);}
}
