package br.com.fintrack.service;

import br.com.fintrack.exception.ValidationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EnvioRecuperacaoService {
    private final ObjectProvider<JavaMailSender> mailProvider;
    private final HttpClient http=HttpClient.newHttpClient();
    @Value("${app.recovery.email-from:}") private String emailFrom;
    @Value("${app.recovery.twilio.account-sid:}") private String accountSid;
    @Value("${app.recovery.twilio.auth-token:}") private String authToken;
    @Value("${app.recovery.twilio.from-number:}") private String fromNumber;
    public EnvioRecuperacaoService(ObjectProvider<JavaMailSender> mailProvider){this.mailProvider=mailProvider;}

    public void enviarEmail(String destino,String codigo){
        JavaMailSender mail=mailProvider.getIfAvailable();
        if(mail==null||emailFrom==null||emailFrom.isBlank())throw new ValidationException("O envio de e-mail ainda não foi configurado no servidor.");
        SimpleMailMessage mensagem=new SimpleMailMessage();
        mensagem.setFrom(emailFrom);mensagem.setTo(destino);mensagem.setSubject("Código de recuperação do FinTrack");
        mensagem.setText("Seu código de recuperação é: "+codigo+"\nEle expira em 10 minutos. Não compartilhe este código.");
        try{mail.send(mensagem);}catch(Exception e){throw new ValidationException("Não foi possível enviar o e-mail de recuperação.");}
    }

    public void enviarSms(String destino,String codigo){
        if(vazio(accountSid)||vazio(authToken)||vazio(fromNumber))throw new ValidationException("O envio de SMS ainda não foi configurado no servidor.");
        String corpo="To="+url(destino)+"&From="+url(fromNumber)+"&Body="+url("FinTrack: seu código de recuperação é "+codigo+". Expira em 10 minutos.");
        String credenciais=Base64.getEncoder().encodeToString((accountSid+":"+authToken).getBytes(StandardCharsets.UTF_8));
        HttpRequest requisicao=HttpRequest.newBuilder(URI.create("https://api.twilio.com/2010-04-01/Accounts/"+accountSid+"/Messages.json"))
            .header("Authorization","Basic "+credenciais).header("Content-Type","application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(corpo)).build();
        try{HttpResponse<String> resposta=http.send(requisicao,HttpResponse.BodyHandlers.ofString());if(resposta.statusCode()<200||resposta.statusCode()>=300)throw new ValidationException("Não foi possível enviar o SMS de recuperação.");}
        catch(InterruptedException e){Thread.currentThread().interrupt();throw new ValidationException("O envio do SMS foi interrompido.");}
        catch(java.io.IOException e){throw new ValidationException("Não foi possível conectar ao serviço de SMS.");}
    }
    private boolean vazio(String valor){return valor==null||valor.isBlank();}
    private String url(String valor){return URLEncoder.encode(valor,StandardCharsets.UTF_8);}
}
