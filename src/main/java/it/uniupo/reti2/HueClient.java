package it.uniupo.reti2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class HueClient {
    // base URL for lights
    private static String lightsURL;
    // RestTemplate instance
    private static RestTemplate rest;


    public static void main(String[] args) {
        // the URL of the Philips Hue bridge
        // String baseURL = "http://172.30.1.138";
        // with the emulator, use instead:
        String baseURL = "http://localhost:8000";

        // sample username, generated by following https://developers.meethue.com/develop/get-started-2/
        // String username = "C0vPwqjJZo5Jt9Oe5HgO6sBFFMxgoR532IxFoGmx";
        // with the emulator, use instead:
        String username = "newdeveloper";

        // init static variables
        lightsURL = baseURL + "/api/" + username + "/lights/";
        rest = new RestTemplate();

        // get all lights from the Hue bridge
        Map<String, ?> allLights = rest.getForObject(lightsURL, Map.class);

        // check if any...
        if (allLights != null) {

            // iterate over the Hue lamps, turn them on with the color loop effect
            // prepare the header for the HTTP request
            HttpHeaders headers = new HttpHeaders();
            // it's a json
            headers.setContentType(MediaType.APPLICATION_JSON);
            // body for turning on a lamp with the colorloop effect
            String colorloop = "{ \"on\" : true, \"effect\" : \"colorloop\" }";
            // create the HTTP request
            HttpEntity<String> onRequest = new HttpEntity<>(colorloop, headers);

            // turn each lamp on with the colorloop effect
            for (String light : allLights.keySet()) {
                changeStatus(light, onRequest);
            }

            // wait 10 seconds...
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(10 - i);
            }

            // create a new HTTP request to turn everything off
            String off = "{ \"on\" : false }";
            HttpEntity<String> offRequest = new HttpEntity<>(off, headers);

            // iterate over the Hue lamps and turn them effectively off
            for (String light : allLights.keySet()) {
                changeStatus(light, offRequest);

            }
        }


    }

    /**
     * It changes the status of the given Philips Hue lamps.
     *
     * @param lightId the ID of the lamp
     * @param request the HTTP request (body + headers) to be sent
     */
    private static void changeStatus(String lightId, HttpEntity request) {
        String callURL = lightsURL + lightId + "/state";
        rest.put(callURL, request);
    }

}
