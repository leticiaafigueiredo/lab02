package br.pucminas.matriculas;

import br.pucminas.matriculas.ui.ConsoleApp;

import java.nio.file.Path;

public final class Main {
    public static void main(String[] args) {
        Path dados = args.length > 0 ? Path.of(args[0]) : Path.of("data");
        new ConsoleApp(dados).executar();
    }
}
