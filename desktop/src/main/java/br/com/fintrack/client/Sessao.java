package br.com.fintrack.client;
public final class Sessao {
    private static String token; private static Long usuarioId; private static String nome;
    private Sessao(){} public static String token(){return token;} public static Long usuarioId(){return usuarioId;} public static String nome(){return nome;}
    public static void iniciar(String t,Long id,String n){token=t;usuarioId=id;nome=n;} public static void encerrar(){token=null;usuarioId=null;nome=null;}
}
