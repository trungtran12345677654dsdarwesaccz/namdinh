package example.namdinh.listTrip.service;



import example.namdinh.listTrip.dto.TripCheckinRequest;
import example.namdinh.listTrip.dto.TripCheckoutRequest;
import example.namdinh.listTrip.dto.TripResponse;

import java.util.List;

public interface TripService {

    Long initializeTrip(TripCheckinRequest request);

    void completeTrip(TripCheckoutRequest request);

    List<TripResponse> getCompletedTrips();
}