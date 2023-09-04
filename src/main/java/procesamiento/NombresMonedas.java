package procesamiento;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NombresMonedas {

    private static String obtenerRespuestaAPI(String apiURL) throws IOException {
        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        StringBuilder respuesta = new StringBuilder();
        try (Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()))) {
            while (scanner.hasNext()) {
                respuesta.append(scanner.nextLine());
            }
        }

        return respuesta.toString();
    }

    private static Map<String, Double> parsearRespuestaJSON(String respuestaJSON) {
        Map<String, Double> conversionRates = new HashMap<>();
        int inicioConversionRates = respuestaJSON.indexOf("\"conversion_rates\":{");
        if (inicioConversionRates != -1) {
            String conversionRatesJSON = respuestaJSON.substring(inicioConversionRates);
            conversionRatesJSON = conversionRatesJSON
                    .replaceAll("\"conversion_rates\":\\{", "")
                    .replaceAll("}", "");
            String[] paresConversion = conversionRatesJSON.split(",");
            for (String parConversion : paresConversion) {
                String[] partes = parConversion.split(":");
                if (partes.length == 2) {
                    String nombreMoneda = partes[0].replaceAll("\"", "").trim();
                    double tasaConversion = Double.parseDouble(partes[1].trim());
                    conversionRates.put(nombreMoneda, tasaConversion);
                }
            }
        }

        return conversionRates;
    }

    public static List<String> obtenerNombresMonedas() {
        String apiURL = "https://v6.exchangerate-api.com/v6/eebf689e0e6c8cf79d6329c1/latest/USD";
        List<String> nombresMonedas = new ArrayList<>();
        try {
            String respuestaJSON = obtenerRespuestaAPI(apiURL);
            Map<String, Double> conversionRates = parsearRespuestaJSON(respuestaJSON);
            nombresMonedas.addAll(conversionRates.keySet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nombresMonedas;
    }

//    public static void main(String[] args) {
//        List<String> nombresMonedas = obtenerNombresMonedas();
//        for (String nombreMoneda : nombresMonedas) {
//            System.out.println(nombreMoneda);
//        }
//    }
}
