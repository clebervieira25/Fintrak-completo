package br.com.fintrack.client;
import br.com.fintrack.model.*; import com.fasterxml.jackson.core.type.TypeReference; import com.fasterxml.jackson.databind.*; import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI; import java.net.http.*; import java.util.*;

public final class ApiClient {
 private static final String BASE="http://localhost:8080"; private static final HttpClient HTTP=HttpClient.newHttpClient(); private static final ObjectMapper JSON=new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
 private ApiClient(){}
 public record TokenResponse(String token,String tipo,Long usuarioId,String nome,String email){}
 public record MensagemResponse(String mensagem){}
 public record SaldoResponse(double totalReceitas,double totalDespesas,double saldo,String inicio,String fim){}
 public static TokenResponse registrar(String nome,String email,String senha){return enviarPublico("/api/auth/register",Map.of("nome",nome,"email",email,"senha",senha),TokenResponse.class);}
 public static TokenResponse login(String email,String senha){return enviarPublico("/api/auth/login",Map.of("email",email,"senha",senha),TokenResponse.class);}
 public static MensagemResponse solicitarRecuperacao(String destino,String canal){return enviarPublico("/api/auth/recovery/request",Map.of("destino",destino,"canal",canal),MensagemResponse.class);}
 public static TokenResponse confirmarRecuperacao(String destino,String canal,String codigo,String novaSenha){return enviarPublico("/api/auth/recovery/confirm",Map.of("destino",destino,"canal",canal,"codigo",codigo,"novaSenha",novaSenha),TokenResponse.class);}
 public static List<Categoria> categorias(){return lerLista("/api/v1/usuarios/"+Sessao.usuarioId()+"/categorias",new TypeReference<>(){});}
 public static Categoria criarCategoria(String nome,String cor){return enviar("POST","/api/v1/usuarios/"+Sessao.usuarioId()+"/categorias",Map.of("nome",nome,"cor",cor),Categoria.class);}
 public static List<Transacao> transacoes(){return lerLista("/api/v1/usuarios/"+Sessao.usuarioId()+"/transacoes",new TypeReference<>(){});}
 public static Transacao salvar(Transacao t){String rota="/api/v1/usuarios/"+Sessao.usuarioId()+"/transacoes"+(t.getId()==null?"":"/"+t.getId());return enviar(t.getId()==null?"POST":"PUT",rota,t,Transacao.class);}
 public static void remover(int id){requisicao("DELETE","/api/v1/usuarios/"+Sessao.usuarioId()+"/transacoes/"+id,null);}
 public static SaldoResponse saldo(){return ler("/api/v1/usuarios/"+Sessao.usuarioId()+"/transacoes/relatorios/saldo",SaldoResponse.class);}
 private static <T>T enviarPublico(String rota,Object corpo,Class<T> tipo){return converter(requisicaoSemToken("POST",rota,corpo),tipo);}
 private static <T>T enviar(String metodo,String rota,Object corpo,Class<T> tipo){return converter(requisicao(metodo,rota,corpo),tipo);}
 private static <T>T ler(String rota,Class<T> tipo){return converter(requisicao("GET",rota,null),tipo);}
 private static <T>List<T> lerLista(String rota,TypeReference<List<T>> tipo){try{return JSON.readValue(requisicao("GET",rota,null),tipo);}catch(Exception e){throw new ApiException("Resposta inválida da API.",e);}}
 private static String requisicao(String metodo,String rota,Object corpo){return executar(metodo,rota,corpo,Sessao.token());}
 private static String requisicaoSemToken(String metodo,String rota,Object corpo){return executar(metodo,rota,corpo,null);}
 private static String executar(String metodo,String rota,Object corpo,String token){try{HttpRequest.Builder b=HttpRequest.newBuilder(URI.create(BASE+rota)).header("Content-Type","application/json");if(token!=null)b.header("Authorization","Bearer "+token);String json=corpo==null?"":JSON.writeValueAsString(corpo);b.method(metodo,corpo==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(json));HttpResponse<String> r=HTTP.send(b.build(),HttpResponse.BodyHandlers.ofString());if(r.statusCode()<200||r.statusCode()>=300)throw new ApiException(mensagem(r.body(),r.statusCode()));return r.body();}catch(ApiException e){throw e;}catch(Exception e){throw new ApiException("Não foi possível conectar à API. Inicie o backend primeiro.",e);}}
 private static String mensagem(String corpo,int status){try{return "Erro "+status+": "+JSON.readTree(corpo).path("mensagem").asText(corpo);}catch(Exception e){return "Erro "+status+" ao acessar a API.";}}
 private static <T>T converter(String json,Class<T> tipo){try{return JSON.readValue(json,tipo);}catch(Exception e){throw new ApiException("Resposta inválida da API.",e);}}
}
