package procesamiento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ConversorDeMonedas {

    private final Map<String, Double> conversionRates;

    public ConversorDeMonedas() {
        this.conversionRates = obtenerConversionRates();
    }

    private Map<String, Double> obtenerConversionRates() {
        try {
            String apiUrl = "https://v6.exchangerate-api.com/v6/eebf689e0e6c8cf79d6329c1/latest/USD";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                // Parsear la respuesta JSON
                String jsonResponse = response.toString();
                return parseJSON(jsonResponse);
            } else {
                System.out.println("Error en la solicitud HTTP: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
        }

        return new HashMap<>();
    }

    private Map<String, Double> parseJSON(String jsonString) {
        Map<String, Double> moneyExpressedInUSD = new HashMap<>();
        moneyExpressedInUSD.put("USD", 1.0);

        String[] parts = jsonString.split("\"conversion_rates\":\\{")[1].split("\\}")[0].split(",");
        for (String part : parts) {
            String[] keyValue = part.trim().split(":");
            String key = keyValue[0].replaceAll("\"", "").trim();
            double value = Double.parseDouble(keyValue[1].trim());
            moneyExpressedInUSD.put(key, value);
        }

        return moneyExpressedInUSD;
    }

    public double convertirMoneda(double monto, String monedaOrigen, String monedaDestino) {
        if (conversionRates.containsKey(monedaOrigen) && conversionRates.containsKey(monedaDestino)) {
            double tasaOrigen = conversionRates.get(monedaOrigen);
            double tasaDestino = conversionRates.get(monedaDestino);
            return monto * (tasaDestino / tasaOrigen);
        } else {
            throw new IllegalArgumentException("Las monedas especificadas no son válidas.");
        }
    }

    public String resultado(Double monto, String monedaOrigen, String monedaDestino) {
        ConversorDeMonedas conversor = new ConversorDeMonedas();
        double resultado = conversor.convertirMoneda(monto, monedaOrigen, monedaDestino);
        return monto + " " + monedaOrigen + " equivale a " + resultado + " " + monedaDestino;
    }

//    public static void main(String[] args) {
//        double cantidad = 100.0;
//        String monedaOrigen = "PEN";
//        String monedaDestino = "EUR";
//        ConversorDeMonedas cdm = new ConversorDeMonedas();
//        System.out.println(cdm.resultado(cantidad, monedaOrigen, monedaDestino));
//    }
}
