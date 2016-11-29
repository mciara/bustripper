package io.telenor.bustripper;

import java.io.IOException;
import java.util.Set;

/**
 * Callback class to client app requesting some bustrips.
 */
public interface TripsCallback {

    /**
     * Got a list of trips
     * @param trips the set of bus trips found
     */
    public void gotTrips(Set<BusTrip> trips);

    /**
     * Output returned trips
     */
    public void completed();

    /**
     * Faild getting the list of trips.
     * @param io trouble found
     */
    public void failedGettingTrips(IOException io);
}
