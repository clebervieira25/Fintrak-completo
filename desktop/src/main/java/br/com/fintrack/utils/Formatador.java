package br.com.fintrack.utils;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formatador {
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Formatador() {}
    public static String moeda(double valor) { return MOEDA.format(valor); }
    public static String data(LocalDate data) { return DATA.format(data); }
}
