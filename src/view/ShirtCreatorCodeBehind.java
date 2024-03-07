package view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.ButtonType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.scene.canvas.GraphicsContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.Color;
import model.Material;
import model.NeckStyle;
import model.ShirtAttributes;
import model.Size;
import model.TShirt;
import viewmodel.ShirtCreatorViewModel;

public class ShirtCreatorCodeBehind {

	@FXML

	private ObservableList<ShirtAttributes> requests;

	ShirtAttributes shirtAttributes;

	Random random = new Random();

	@FXML

	private ComboBox<Integer> quantityComboBox;

	@FXML
	private ComboBox<Color> colorComboBox;

	@FXML
	private ComboBox<Size> sizeComboBox;

	@FXML
	private ComboBox<Boolean> pocketComboBox;

	@FXML
	private ComboBox<Size> sleeveComboBox;

	@FXML
	private ComboBox<NeckStyle> collarComboBox;

	@FXML
	private ComboBox<Material> materialComboBox;

	@FXML
	private Button deleteButton;

	@FXML
	private Button requestButton;

	@FXML
	private Button saveButton;

	@FXML
	private Button editButton;

	@FXML
	private Button submitButton;

	@FXML
	private Button viewButton;

	@FXML
	private ListView<TShirt> designedListView;

	@FXML
	private ListView<TShirt> presetsListView;

	@FXML
	private TextField nameTextField;

	@FXML
	private TextField textTextField;

	@FXML
	private ImageView shirtImageView;

	@FXML
	private MenuBar shirtMenubar;

	private ShirtCreatorViewModel viewModel;

	private ShirtCreatorViewModel presetsViewModel;

	private GraphicsContext graphicsContext;

	private BufferedImage canvas;

	private ListView<ShirtAttributes> listView;

	@FXML
	void handleLoadButton(ActionEvent event) throws IOException {
		FileChooser fc = new FileChooser();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File selectedFile = fc.showOpenDialog(stage);
		BufferedImage img = ImageIO.read(selectedFile);

		Image image = new Image(selectedFile.toURI().toString());
		graphicsContext.drawImage(image, 0, 0, (canvas.getWidth()), (canvas.getHeight()));
	}

	public void addRequest(ShirtAttributes requestedShirt) {
		requests.add(requestedShirt);
	}

	public void clearRequests() {
		requests.clear();
	}

	@FXML
	void onShowRequestsButtonClick(ActionEvent event) {
		Stage stage = new Stage(); // Create a new stage (window)
		stage.setTitle("List of Requests");
		VBox layout = new VBox(10);
		listView.setItems(requests); // Use the items field

		layout.getChildren().add(listView);

		Scene scene = new Scene(layout, 300, 250);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}

	@FXML
	void onRequestButtonClick(ActionEvent event) {
		Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
		confirmationDialog.setTitle("Request Confirmation");
		confirmationDialog.setHeaderText("Request Shirt Design");
		confirmationDialog.setContentText("Are you sure you want to request this shirt design?");

		Optional<ButtonType> result = confirmationDialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// User chose OK
			// Place your request logic here
			addRequest(new ShirtAttributes(random.nextInt(), shirtAttributes.getSize(), shirtAttributes.getMaterial(),
					shirtAttributes.getColor(), shirtAttributes.getBackLength(), shirtAttributes.getShoulderWidth(),
					shirtAttributes.hasPocket()));
			Alert requestConfirmationDialog = new Alert(Alert.AlertType.INFORMATION); // Use INFORMATION Alert for
																						// feedback
			requestConfirmationDialog.setTitle("Request Successful");
			requestConfirmationDialog.setHeaderText(null); // No header text
			requestConfirmationDialog.setContentText("Shirt design requested successfully.");
			requestConfirmationDialog.showAndWait(); // Show the dialog and wait
		} else {
			// User chose Cancel or closed the dialog

			Alert noConfirmationDialog = new Alert(Alert.AlertType.INFORMATION); // Use INFORMATION Alert for feedback
			noConfirmationDialog.setTitle("Request Cancelled");
			noConfirmationDialog.setHeaderText(null); // No header text
			noConfirmationDialog.setContentText("Shirt design request cancelled.");
			noConfirmationDialog.showAndWait(); // Show the dialog and wait
		}
	}

	public ShirtCreatorCodeBehind() {
		this.viewModel = new ShirtCreatorViewModel();
		this.presetsViewModel = new ShirtCreatorViewModel();

	}

	private void updateUIBasedOnSize(Size size) {
		shirtAttributes.createPresetForSize(size);

		// Now update other UI components based on the preset
		materialComboBox.setValue(shirtAttributes.getMaterial());
		colorComboBox.setValue(shirtAttributes.getColor());

		// Update other fields as necessary, such as text fields for measurements
	}

	public void initialize() {
		requests = FXCollections.observableArrayList();

		this.populateComboBoxes();

		this.bindToViewModel();

		this.quantityComboBox.setValue(1);

		this.addPresets();
		this.setupSelectionHandlerForListView();
		this.setupSelectionHandlerForPresetsListView();

		shirtAttributes = new ShirtAttributes();
		listView = new ListView<>();
		sizeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				updateUIBasedOnSize(newSelection);
			}
		});
	}

	@FXML
	void handleAdd(ActionEvent event) {
		Alert newAlert = new Alert(AlertType.ERROR);

		try {
			if (!this.viewModel.addShirt()) {
				newAlert.setContentText("This name already exists");
				newAlert.showAndWait();
			}
		} catch (NullPointerException nPE) {
			newAlert.setContentText(nPE.getLocalizedMessage());

			newAlert.showAndWait();
		} catch (IllegalArgumentException iAE) {
			newAlert.setContentText(iAE.getLocalizedMessage());
			newAlert.showAndWait();

		}

	}

	@FXML
	void handleSaveButton(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Shirt Attributes");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			saveShirtAttributesToFile(file);
		}
	}

	@FXML
	void handleDelete(ActionEvent event) {

	}

	@FXML
	void handleEdit(ActionEvent event) {

	}

	@FXML
	void handleRequest(ActionEvent event) {

	}

	@FXML
	void handleViewRequests(ActionEvent event) {

	}

	private void bindToViewModel() {
		this.designedListView.itemsProperty().bind(this.viewModel.listProperty());

		this.presetsListView.itemsProperty().bind(this.presetsViewModel.listProperty());

		this.pocketComboBox.valueProperty().bindBidirectional(this.viewModel.pocketProperty());
		this.nameTextField.textProperty().bindBidirectional(this.viewModel.nameProperty());

		this.quantityComboBox.valueProperty().bindBidirectional(this.viewModel.quantityProperty());

		this.sizeComboBox.valueProperty().bindBidirectional(this.viewModel.sizeProperty());

		this.sleeveComboBox.valueProperty().bindBidirectional(this.viewModel.sleeveLengthProperty());
		this.colorComboBox.valueProperty().bindBidirectional(this.viewModel.colorProperty());

		this.collarComboBox.valueProperty().bindBidirectional(this.viewModel.neckStyleProperty());
		this.materialComboBox.valueProperty().bindBidirectional(this.viewModel.materialProperty());

	}

	private void populateComboBoxes() {
		this.pocketComboBox.getItems().addAll(true, false);
		this.quantityComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9);
		this.sizeComboBox.getItems().addAll(Size.values());
		this.sleeveComboBox.getItems().addAll(Size.values());
		this.colorComboBox.getItems().addAll(Color.values());
		this.collarComboBox.getItems().addAll(NeckStyle.values());
		this.materialComboBox.getItems().addAll(Material.values());

	}

	private void setupSelectionHandlerForListView() {
		this.designedListView.getSelectionModel().selectedItemProperty().addListener(

				(observable, oldValue, newValue) -> {
					if (newValue != null) {
						this.nameTextField.setText(newValue.getName());

						this.pocketComboBox.valueProperty().setValue(newValue.hasPocket());
						this.quantityComboBox.valueProperty().setValue(newValue.getQuantity());
						this.sizeComboBox.valueProperty().setValue(newValue.getSize());
						this.sleeveComboBox.valueProperty().setValue(newValue.getSize());
						this.colorComboBox.valueProperty().setValue(newValue.getColor());
						this.collarComboBox.valueProperty().setValue(newValue.getNeckStyle());
						this.materialComboBox.valueProperty().setValue(newValue.getMaterial());
						this.textTextField.setText(newValue.getText());

					}
				});
	}

	private void setupSelectionHandlerForPresetsListView() {
		this.presetsListView.getSelectionModel().selectedItemProperty().addListener(

				(observable, oldValue, newValue) -> {
					if (newValue != null) {
						this.nameTextField.setText(newValue.getName());

						this.pocketComboBox.valueProperty().setValue(newValue.hasPocket());
						this.quantityComboBox.valueProperty().setValue(newValue.getQuantity());
						this.sizeComboBox.valueProperty().setValue(newValue.getSize());
						this.sleeveComboBox.valueProperty().setValue(newValue.getSize());
						this.colorComboBox.valueProperty().setValue(newValue.getColor());
						this.collarComboBox.valueProperty().setValue(newValue.getNeckStyle());
						this.materialComboBox.valueProperty().setValue(newValue.getMaterial());
						this.textTextField.setText(newValue.getText());

					}
				});
	}

	private void addPresets() {
		this.presetsViewModel.pocketProperty().set(true);
		this.presetsViewModel.nameProperty().set("Preset 1");
		this.presetsViewModel.quantityProperty().set(6);
		this.presetsViewModel.colorProperty().set(Color.BLUE);
		this.presetsViewModel.sizeProperty().set(Size.XXXL);
		this.presetsViewModel.sleeveLengthProperty().set(Size.XS);
		this.presetsViewModel.neckStyleProperty().set(NeckStyle.V_NECK);
		this.presetsViewModel.materialProperty().set(Material.SILK);
		this.presetsViewModel.textProperty().set("Big Boss");

		this.presetsViewModel.addShirt();
	}

	private void saveShirtAttributesToFile(File file) {
		try (PrintWriter writer = new PrintWriter(file)) {

			writer.println("Name: " + nameTextField.getText());
			writer.println("Quantity: " + quantityComboBox.getValue());
			writer.println("Size: " + sizeComboBox.getValue());
			writer.println("SleeveLength: " + sleeveComboBox.getValue());
			writer.println("Color: " + colorComboBox.getValue());
			writer.println("Collar: " + collarComboBox.getValue());

			writer.println("Material: " + materialComboBox.getValue());

			writer.println("Has Pocket: " + (pocketComboBox.getValue()));

			Alert saveConfirmationDialog = new Alert(AlertType.INFORMATION);
			saveConfirmationDialog.setTitle("Save Successful");
			saveConfirmationDialog.setHeaderText(null);
			saveConfirmationDialog.setContentText("Shirt attributes saved successfully.");
			saveConfirmationDialog.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alert saveErrorDialog = new Alert(AlertType.ERROR);
			saveErrorDialog.setTitle("Save Error");
			saveErrorDialog.setHeaderText(null);
			saveErrorDialog.setContentText("An error occurred while saving shirt attributes.");
			saveErrorDialog.showAndWait();
		}
	}

}
