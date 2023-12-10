package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static final String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=DnL4bQoGggTEO2lPfahc2Nrr1qKX5qWLKaE8cp7W";

    public static void main(String[] args) throws IOException {

        var requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(30000)
                .setRedirectsEnabled(false)
                .build();

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        Nasa nasaResponse = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {});
        String url = nasaResponse.getUrl();
        HttpGet requestUrl = new HttpGet(url);
        response = httpClient.execute(requestUrl);
        String[] urlForSplit = url.split("/");
        String fileName = urlForSplit[6];
        File file = new File(fileName);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] bytes = response.getEntity().getContent().readAllBytes();
            bos.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}