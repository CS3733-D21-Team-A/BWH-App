package edu.wpi.aquamarine_axolotls.gmaps;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import java.io.IOException;
import java.util.Scanner;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class Gmaps {
	//Credit: https://www.programcreek.com/java-api-examples/?api=com.google.maps.DirectionsApi
	public static void directionsTest(String from, String to) {
		String apiKey = prefs.get(API_KEY,null);
		Scanner sc = new Scanner(System.in);

		if (apiKey == null) {
			System.out.print("No Google Maps API key set. Please enter your API key: ");
			prefs.put(API_KEY,apiKey = sc.nextLine());
		}

		boolean tryAgain = true;

		while (tryAgain) {
			GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
			DirectionsApiRequest request = DirectionsApi.getDirections(context, from, to);

			try {
				DirectionsResult result = request.await();
				for (DirectionsRoute route : result.routes) {
					for (DirectionsLeg leg : route.legs) {
						for (DirectionsStep step : leg.steps) {
							System.out.println(step.toString());
						}
					}
				}
				tryAgain = false;
			} catch (Exception e) {
				System.out.print("Error: Invalid Google Maps API key. Please enter your API key: ");
				prefs.put(API_KEY,apiKey = sc.nextLine());
			}
		}
	}

	public static void main(String[] args) {
		directionsTest("WPI", "Brigham and Women's Hospital");
	}
}
