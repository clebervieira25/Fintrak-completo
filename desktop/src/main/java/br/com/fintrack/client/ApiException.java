package br.com.fintrack.client; public class ApiException extends RuntimeException{public ApiException(String m){super(m);} public ApiException(String m,Throwable c){super(m,c);}}
