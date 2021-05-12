package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;

public class GiftDelivery extends GenericServiceRequest {

	@FXML
	private JFXDatePicker deliveryDate;

	@FXML
	private JFXComboBox<String> giftOptions;

	@FXML
	private JFXTextArea persMessage;

	@FXML
	public void initialize() {
		super.initialize();
		requestFieldList.add(new FieldTemplate<>(
			"GIFTTYPE",
			giftOptions,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"NOTE",
			persMessage,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));
		requestFieldList.add(new FieldTemplate<>(
			"DELIVERYDATE",
			deliveryDate,
			(a) -> a.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
			(a) -> a.getValue() != null));
		serviceRequestType = SERVICEREQUEST.GIFT_DELIVERY;


		giftOptions.setItems(FXCollections.observableArrayList("Hospital T-Shirt", "Teddy Bear", "Hospital Mug"));
	}

}
