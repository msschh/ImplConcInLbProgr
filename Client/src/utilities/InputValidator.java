package utilities;

public class InputValidator {

    public static boolean isUsernameValid(String username) {
        username = username.toLowerCase().trim();

        // numele unui utilizator trebuie sa fie de cel putin 3 caractere
        if (username.length() < 3) return false;

        // primul caracter trebuie sa fie o litera
        if (!Character.isLetter(username.charAt(0))) return false;

        return !username.chars().filter(c -> !Character.isLetter(c) && !Character.isDigit(c)).findAny().isPresent();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

}
