package br.pucminas.matriculas.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Terminal {
    private final Scanner scanner;

    public Terminal(Scanner scanner) {
        this.scanner = scanner;
    }

    public String ler(String rotulo) {
        System.out.print(rotulo);
        return scanner.nextLine().trim();
    }

    public int lerInt(String rotulo) {
        while (true) {
            String texto = ler(rotulo);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Informe um número inteiro.");
            }
        }
    }

    public void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    public <T> T escolher(String rotulo, List<T> itens, Function<T, String> rotuloItem) {
        if (itens.isEmpty()) {
            throw new IllegalStateException("Não há itens para selecionar.");
        }
        for (int i = 0; i < itens.size(); i++) {
            System.out.println((i + 1) + ") " + rotuloItem.apply(itens.get(i)));
        }
        while (true) {
            int opcao = lerInt(rotulo);
            if (opcao >= 1 && opcao <= itens.size()) {
                return itens.get(opcao - 1);
            }
            System.out.println("Opção inválida.");
        }
    }

    public void erro(Exception e) {
        System.out.println("Erro: " + e.getMessage());
    }
}
