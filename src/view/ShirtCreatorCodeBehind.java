package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.scene.canvas.GraphicsContext;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.shirt.Shirt;
import model.shirt.TShirt;
import model.shirt_attribute.Color;
import model.shirt_attribute.Material;
import model.shirt_attribute.NeckStyle;
import model.shirt_attribute.Size;
import viewmodel.ShirtCreatorViewModel;
import java.util.Optional;
import java.util.Random;

/**
 * The ShirtCreatorCodeBehind class manages the interaction between the user and the GUI of the Shirt Creator application.
 * It handles the loading of shirt images, addition and removal of design requests, and updates the UI components according to user input.
 */
public class ShirtCreatorCodeBehind {

    private Shirt shirtAttributes;
    private Random random = new Random();
    private GraphicsContext graphicsContext;
    private BufferedImage canvas;
    private ListView<Shirt> listView;
    private ShirtCreatorViewModel viewModel;

    @FXML private ObservableList<Shirt> requests;
    @FXML private ComboBox<Double> backLengthComboBox;
    @FXML private ComboBox<Color> colorComboBox;
    @FXML private Button deleteButton;
    @FXML private TextArea designedTextArea;
    @FXML private Button editButton;
    @FXML private ComboBox<Material> materialComboBox;
    @FXML private TextField nameTextField;
    @FXML private ComboBox<Boolean> pocketComboBox;
    @FXML private TextArea presetsTextArea;
    @FXML private Button requestButton;
    @FXML private Button saveButton;
    @FXML private ImageView shirtImageView;
    @FXML private MenuBar shirtMenubar;
    @FXML private ComboBox<Double> shoulderLengthComboBox;
    @FXML private ComboBox<Size> sizeComboBox;
    @FXML private ComboBox<?> styleCombobox;
    @FXML private Button submitButton;
    @FXML private Button viewButton;
    @FXML private ComboBox<Integer> quantityComboBox;
    @FXML private ComboBox<Size> sleeveComboBox;
    @FXML private ComboBox<NeckStyle> collarCombobox;
    @FXML private ListView<TShirt> designedListView;
    @FXML private ListView<TShirt> presetsListView;
    @FXML private TextField textTextField;

    /**
     * Initializes the controller class, sets up the view model, UI components, and data bindings.
     */
    public void initialize() {
    	this.designedListView = new ListView<>();
    	this.quantityComboBox = new ComboBox<>();
    	this.sleeveComboBox = new ComboBox<>();
    	this.collarCombobox = new ComboBox<>();
        this.viewModel = new ShirtCreatorViewModel();
        bindToViewModel();
        populateComboBoxes();
        setupSelectionHandlerForListView();
        quantityComboBox.valueProperty().set(1);
    }

    /**
     * Handles loading a shirt image from the filesystem.
     * @param event The action event triggered by the user.
     */
    @FXML
    void handleLoadButton(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            graphicsContext.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    /**
     * Adds a new design request to the observable list of requests.
     * @param requestedShirt The ShirtAttributes object detailing the requested shirt.
     */
    public void addRequest(Shirt requestedShirt) {
        requests.add(requestedShirt);
    }

    /**
     * Clears all design requests from the observable list.
     */
    public void clearRequests() {
        requests.clear();
    }

    /**
     * Shows a dialog listing all current design requests.
     * @param event The action event triggered by the user.
     */
    @FXML
    void onShowRequestsButtonClick(ActionEvent event) {
        Stage stage = new Stage();
        stage.setTitle("List of Requests");
        VBox layout = new VBox(10);
        listView.setItems(requests);

        layout.getChildren().add(listView);

        Scene scene = new Scene(layout, 300, 250);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Submits a request for a new shirt design after user confirmation.
     * @param event The action event triggered by the user.
     */
    @FXML
    void onRequestButtonClick(ActionEvent event) {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Request Confirmation");
        confirmationDialog.setHeaderText("Request Shirt Design");
        confirmationDialog.setContentText("Are you sure you want to request this shirt design?");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            addRequest(new TShirt(random.nextInt(), shirtAttributes.getSize(), shirtAttributes.getMaterial(), shirtAttributes.getColor(), shirtAttributes.getSleeveLength(), shirtAttributes.getShoulderWidth(), shirtAttributes.hasPocket()));
            Alert requestConfirmationDialog = new Alert(Alert.AlertType.INFORMATION);
            requestConfirmationDialog.setTitle("Request Successful");
            requestConfirmationDialog.setHeaderText(null);
            requestConfirmationDialog.setContentText("Shirt design requested successfully.");
            requestConfirmationDialog.showAndWait();
        } else {
            Alert noConfirmationDialog = new Alert(Alert.AlertType.INFORMATION);
            noConfirmationDialog.setTitle("Request Cancelled");
            noConfirmationDialog.setHeaderText(null);
            noConfirmationDialog.setContentText("Shirt design request cancelled.");
            noConfirmationDialog.showAndWait();
        }
    }

    /**
     * Binds UI components to the view model properties. This method ensures that the UI components are updated automatically when the model changes.
     */
    private void bindToViewModel() {
    	this.designedListView.itemsProperty().bind(this.viewModel.listProperty());
		//this.pocketComboBox.valueProperty().bindBidirectional(this.viewModel.pocketProperty());
		this.nameTextField.textProperty().bindBidirectional(this.viewModel.nameProperty());
		this.quantityComboBox.valueProperty().bindBidirectional(this.viewModel.quantityProperty());
		this.sizeComboBox.valueProperty().bindBidirectional(this.viewModel.sizeProperty());
		this.sleeveComboBox.valueProperty().bindBidirectional(this.viewModel.sleeveLengthProperty());
		this.colorComboBox.valueProperty().bindBidirectional(this.viewModel.colorProperty());
		this.collarCombobox.valueProperty().bindBidirectional(this.viewModel.neckStyleProperty());
		this.materialComboBox.valueProperty().bindBidirectional(this.viewModel.materialProperty());
    }

    /**
     * Populates combo boxes with values. This method initializes combo boxes with predefined values for size, color, material, etc.
     */
    private void populateComboBoxes() {
    	//this.pocketComboBox.getItems().addAll(true, false);
		this.quantityComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9);
		this.sizeComboBox.getItems().addAll(Size.values());
		this.sleeveComboBox.getItems().addAll(Size.values());
		this.colorComboBox.getItems().addAll(Color.values());
		this.collarCombobox.getItems().addAll(NeckStyle.values());
		this.materialComboBox.getItems().addAll(Material.values());
    }

    /**
     * Sets up selection handlers for the list views. This method ensures that selecting an item from a list view updates the relevant UI components.
     */
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
						this.collarCombobox.valueProperty().setValue(newValue.getNeckStyle());
						this.materialComboBox.valueProperty().setValue(newValue.getMaterial());
						this.textTextField.setText(newValue.getShirtText());
					}
				});
    }
}
	

