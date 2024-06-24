package com.alura.screenmatch.principal;

import com.alura.screenmatch.excepcion.ErrorEnConversionDeDuracionException;
import com.alura.screenmatch.modelos.Titulo;
import com.alura.screenmatch.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrincipalConBusqueda {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner lectura = new Scanner(System.in);
        List<Titulo> titulos = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while(true){
            System.out.println("Escriba el nombre de la película: ");
            var busqueda = lectura.nextLine();

            if(busqueda.equalsIgnoreCase("salir")){
                break;
            }

            String direccion = "http://www.omdbapi.com/?t="+busqueda+"&apikey=35a94f24";

            String url = direccion.replace(" ", "+");

            try {

                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url)).build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println(response.body());

                String json = response.body();

                TituloOmdb miTituloOmdb = gson.fromJson(json, TituloOmdb.class);
                System.out.println(miTituloOmdb);

                Titulo miTitulo = new Titulo(miTituloOmdb);
                System.out.println(miTitulo);

                titulos.add(miTitulo);
            } catch (NumberFormatException e){
                System.out.println("Ocurrio un error: ");
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Ocurrio un error en la dirección, verifique la URI.");
            } catch(ErrorEnConversionDeDuracionException e){
                System.out.println(e.getMessage());
            }
        }

        System.out.println(titulos);

        FileWriter escritura = new FileWriter("titulos.json");
        escritura.write(gson.toJson(titulos));
        escritura.close();

        System.out.println("Finalizó la ejecución del programa");
    }
}
