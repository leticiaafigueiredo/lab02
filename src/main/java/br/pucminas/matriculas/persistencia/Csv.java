package br.pucminas.matriculas.persistencia;

import java.util.ArrayList;
import java.util.List;

final class Csv {
    private Csv() {
    }

    static String join(Object... campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(escape(campos[i] == null ? "" : String.valueOf(campos[i])));
        }
        return sb.toString();
    }

    static List<String> split(String linha) {
        List<String> campos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean escape = false;
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (escape) {
                atual.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '|') {
                campos.add(atual.toString());
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }
        campos.add(atual.toString());
        return campos;
    }

    static String escape(String valor) {
        return valor.replace("\\", "\\\\").replace("|", "\\|").replace("\n", "\\n");
    }
}
