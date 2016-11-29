package io.telenor.bustripper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class BusStopsCallBack implements InvocationCallback<Response> {

    private ObjectMapper mapper = new ObjectMapper();

    private TripsCallback listener;

    public BusStopsCallBack(TripsCallback callback) {
        this.listener = callback;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void completed(Response response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BusStop[] stops = mapper.readValue(response.readEntity(String.class), BusStop[].class);
            System.out.println(String.format("Got %d busstops nearby", stops.length));

            int limit = Math.min(stops.length, 10);
            CountDownLatch doneWork = new CountDownLatch(limit);

            for(int i = 0; i< limit;i++) {
                BusStop stop = stops[i];
                new Thread(new FindBusLinesForStop(stop.getId(), listener, doneWork)).start();
            }
            doneWork.await();
            listener.completed();
        } catch (IOException e) {
            listener.failedGettingTrips(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void failed(Throwable throwable) {
        listener.failedGettingTrips((IOException) throwable);
    }
}
