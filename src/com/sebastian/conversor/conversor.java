package com.sebastian.conversor;

import java.util.Scanner;
import java.text.NumberFormat;
import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class conversor {

    // Metodo para obtener la tasa de conversión desde la API
    public static double obtenerTasa(String base, String destino)
            throws IOException, InterruptedException {

        String apiKey = "3df37639057e8d86fdb19f51";
        String url = "https://v6.exchangerate-api.com/v6/"
                + apiKey + "/pair/" + base + "/" + destino;

        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = cliente.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        JsonElement elemento = JsonParser.parseString(response.body());
        JsonObject objeto = elemento.getAsJsonObject();

        return objeto.get("conversion_rate").getAsDouble();
    }

    // metodo para mostrar el menu y procesar conversiones
    public static void escribirMenu() throws Exception {

        try (Scanner scanner = new Scanner(System.in)) {
            int opcion = 0;

            while (opcion != 7) {

                System.out.println("""
                        ***********************************************
                        Sea bienvenido/a al Conversor de Moneda =)

                        1) Dólar ==> Peso argentino
                        2) Peso argentino ==> Dólar
                        3) Dólar ==> Real brasileño
                        4) Real brasileño ==> Dólar
                        5) Dólar ==> Peso colombiano
                        6) Peso colombiano ==> Dólar
                        7) Salir

                        Elija una opción válida:
                        ***********************************************
                        """);

                // Validar opción del menu
                String opcionInput = scanner.nextLine();
                try {
                    opcion = Integer.parseInt(opcionInput);
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Opción inválida. Ingrese un número del 1 al 7.\n");
                    continue;
                }

                if (opcion >= 1 && opcion <= 6) {

                    double cantidad = 0;
                    boolean entradaValida = false;

                    // Validar la cantidad ingresada
                    while (!entradaValida) {
                        System.out.print("Ingrese el valor que desea convertir: ");
                        String input = scanner.nextLine();

                        // Quitar puntos como separadores de miles
                        input = input.replace(".", "");

                        // Reemplazar coma por punto para decimales
                        input = input.replace(",", ".");

                        try {
                            cantidad = Double.parseDouble(input);
                            if (cantidad < 0) {
                                System.out.println("⚠ No se permiten valores negativos.\n");
                            } else {
                                entradaValida = true;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⚠ Entrada inválida. Ingrese un número válido.\n");
                        }
                    }

                    String base = "";
                    String destino = "";

                    switch (opcion) {
                        case 1 -> { base = "USD"; destino = "ARS"; }
                        case 2 -> { base = "ARS"; destino = "USD"; }
                        case 3 -> { base = "USD"; destino = "BRL"; }
                        case 4 -> { base = "BRL"; destino = "USD"; }
                        case 5 -> { base = "USD"; destino = "COP"; }
                        case 6 -> { base = "COP"; destino = "USD"; }
                    }

                    double tasa = obtenerTasa(base, destino);
                    double resultado = cantidad * tasa;

                    // Formatear resultado estilo latinoamericano
                    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "CO"));
                    nf.setMaximumFractionDigits(2);
                    System.out.println("El valor convertido es: " + nf.format(resultado));
                    System.out.println();
                } else if (opcion != 7) {
                    System.out.println("⚠ Opción inválida. Ingrese un número del 1 al 7.\n");
                }
            }
        }

        System.out.println("Programa finalizado.");
    }
}
