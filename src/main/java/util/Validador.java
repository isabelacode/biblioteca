package util;

public class Validador {

    private Validador() {
    }

    public static boolean isCpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            return false;
        }

        int[] numeros = digitos.chars().map(c -> c - '0').toArray();

        int primeiroDv = calcularDigitoVerificador(numeros, 9, 10);
        if (primeiroDv != numeros[9]) {
            return false;
        }

        int segundoDv = calcularDigitoVerificador(numeros, 10, 11);
        return segundoDv == numeros[10];
    }

    public static boolean isTelefoneValido(String telefone) {
        if (telefone == null) {
            return false;
        }
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() != 10 && digitos.length() != 11) {
            return false;
        }
        if (digitos.charAt(0) == '0') {
            return false;
        }
        return digitos.length() != 11 || digitos.charAt(2) == '9';
    }

    private static int calcularDigitoVerificador(int[] numeros, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += numeros[i] * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
