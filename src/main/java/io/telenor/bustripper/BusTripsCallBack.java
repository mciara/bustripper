package io.telenor.bustripper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

/**
 * Callback from Jersey when bustrips are there.
 */
public class BusTripsCallBack implements InvocationCallback<Response> {
    ObjectMapper mapper = new ObjectMapper();
    String url;
    private TripsCallback listener;
    private CountDownLatch doneWork;

    public BusTripsCallBack(String url, TripsCallback callback, CountDownLatch doneWork) {
        this.url = url;
        this.listener = callback;
        this.doneWork = doneWork;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void completed(Response response) {
        String content = response.readEntity(String.class);

        try {
            if (!content.isEmpty()) {
                BusTrip[] trips = mapper.readValue(content, BusTrip[].class);
                HashSet<BusTrip> set = new HashSet<>(Arrays.asList(trips));
                listener.gotTrips(set);
            }
        } catch (IOException e) {
            listener.failedGettingTrips(e);
        } finally {
            doneWork.countDown();
        }
    }

    public void failed(Throwable throwable) {
        listener.failedGettingTrips((IOException) throwable);
    }
}
