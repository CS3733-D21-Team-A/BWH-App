package edu.wpi.aquamarine_axolotls.extras;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;

import java.io.IOException;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class Directions {
	private Directions(){} //No instantiation! >:(

	//Note: All parking sites have valet (patient use only)
	public static final String VISITOR_PARKING = "80 Francis St, Boston, MA 02115";
	public static final String PATIENT_PARKING = "15-51 New Whitney St Parking"; //Can't use provided address verbatim because it goes somewhere else
	public static final String VALET_PARKING = "42.33584099869926, -71.10735590684958"; //couldn't get a reliable address, so this works
	public static final String OTHER_PARKING = "42.33533602615294, -71.10923373042405"; //couldn't get a reliable address, so this works
	public static final String EMERGENCY_ROOM = "42.33584099869926, -71.10735590684958"; //couldn't get a reliable address, so this works

	public static DirectionsLeg navigateToER(String location) throws IOException, InterruptedException, ApiException {
		return getDirections(location, EMERGENCY_ROOM);
	}

	public static DirectionsLeg navigateToClosestParking(String location) throws IOException, InterruptedException, ApiException {
		DirectionsLeg shortest = getDirections(location, VISITOR_PARKING);
		DirectionsLeg temp;
		if ((temp = getDirections(location, PATIENT_PARKING)).duration.inSeconds < shortest.duration.inSeconds) shortest = temp;
		if ((temp = getDirections(location, OTHER_PARKING)).duration.inSeconds < shortest.duration.inSeconds) shortest = temp;
		return shortest;
	}

	public static DirectionsLeg navigateToClosestValet(String location) throws IOException, InterruptedException, ApiException {
		DirectionsLeg parking = navigateToClosestParking(location);
		DirectionsLeg valet = getDirections(location,VALET_PARKING);
		return (parking.duration.inSeconds < valet.duration.inSeconds) ? parking : valet;
	}

	/**
	 * Get directions between two locations via the Googla Maps Directions API
	 * @param from Location to navigate from
	 * @param to Location to navigate to
	 * @return DirectionsResult object representing directions to the destin
	 * @throws IOException Something went wrong.
	 * @throws InterruptedException Something went wrong.
	 * @throws ApiException Something went wrong.
	 */
	private static DirectionsLeg getDirections(String from, String to) throws IOException, InterruptedException, ApiException {
		String apiKey = prefs.get(API_KEY,null);
		GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
		DirectionsApiRequest request = DirectionsApi.getDirections(context, from, to);

		return request.await().routes[0].legs[0]; //Only getting the default route; no alternatives
	}
}
