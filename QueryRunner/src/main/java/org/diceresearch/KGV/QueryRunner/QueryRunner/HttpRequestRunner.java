package org.diceresearch.KGV.QueryRunner.QueryRunner;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class HttpRequestRunner {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final RestTemplate restTemplate;

    public HttpRequestRunner(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public static boolean isValidDBpediaURL(String url) throws Exception {

        return true;

        /*HttpGet request = new HttpGet(url);

        // add request headers
        request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            if(response.getStatusLine().getStatusCode()==200){
                return true;
            }
            return false;

            *//*HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                System.out.println(result);
            }*//*

        }*/

    }

    public  String sendHTTPGetRequest(String url){
        url = url.replace("<","").replace(">","").replace("https","http");
        return this.restTemplate.getForObject(url, String.class);
    }
}
