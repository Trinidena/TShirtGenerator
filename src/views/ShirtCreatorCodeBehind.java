package views;

import java.io.IOException;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ModelAwareController;
import server.ShirtCredentialsManager;
import model.shirt.Shirt;
import model.shirt_attribute.Color;
import model.shirt_attribute.Material;
import model.shirt_attribute.NeckStyle;
import model.shirt_attribute.Size;
import model.user.User;
import viewmodel.ShirtCreatorViewModel;

/**
 * Controls the interaction between the user and the GUI for the Shirt Creator
 * application. Handles operations such as loading shirt images, managing design
 * requests, and updating UI components based on user input.
 * 
 * @author Trinidad Dena
 */
public class ShirtCreatorCodeBehind implements ModelAwareController {

	@FXML
	private ListView<Shirt> designedListView, requestListView;
	@FXML
	private ObservableList<Shirt> requests;
	@FXML
	private ComboBox<Size> sizeComboBox, backLengthComboBox, shoulderLengthComboBox, sleeveComboBox;
	@FXML
	private ComboBox<Color> colorComboBox;
	@FXML
	private ComboBox<Material> materialComboBox;
	@FXML
	private ComboBox<Boolean> pocketComboBox;
	@FXML
	private ComboBox<NeckStyle> collarComboBox;
	@FXML
	private TextField nameTextField, textTextField;
	@FXML
	private ImageView shirtImageView;

	private ShirtCreatorViewModel viewModel;
	
	private StringProperty creatorProperty = new SimpleStringProperty();
	private StringProperty passwordProperty = new SimpleStringProperty();
	private StringProperty roleProperty = new SimpleStringProperty();
	private ShirtCredentialsManager manager;
	private List<User> users;

	/**
	 * Constructs an instance of the ShirtCreatorCodeBehind. Initializes the view
	 * model associated with the shirt creator functionality.
	 */
	public ShirtCreatorCodeBehind() {
		this.viewModel = new ShirtCreatorViewModel();
		this.requestListView = new ListView<>();
		this.designedListView = new ListView<>();
	}

	/**
	 * Initializes the controller. Sets up the view model, UI components, and data
	 * bindings.
	 */
	@FXML
	public void initialize() {
		this.populateComboBoxes();
		this.addPresets();
		this.requests = FXCollections.observableArrayList();
		this.requestListView.setItems(this.requests);
		this.setupSelectionHandlerForListView();
		this.bindToViewModel();
		this.designedListView.setOnMouseClicked(event -> {
            Shirt selectedShirt = this.designedListView.getSelectionModel().getSelectedItem();
            if (selectedShirt != null && event.getClickCount() == 2) {
                this.showShirtDetails(selectedShirt);
            }
        }
		);
	}

	private void addPresets() {
		this.viewModel.pocketProperty().set(true);
		this.viewModel.nameProperty().set("Preset 2");
		this.viewModel.shoulderProperty().set(Size.XXL);
		this.viewModel.sizeProperty().set(Size.XXXL);
		this.viewModel.sleeveLengthProperty().set(Size.XS);
		this.viewModel.colorProperty().set(Color.BLUE);
		this.viewModel.neckStyleProperty().set(NeckStyle.V_NECK);
		this.viewModel.materialProperty().set(Material.SILK);
		this.viewModel.backLengthProperty().set(Size.XL);
		this.viewModel.textProperty().set("Big Boss");
		this.viewModel.creatorProperty().set("Default");
		this.viewModel.addShirtToListView();
		
		this.viewModel.nameProperty().set("Preset 1");
		this.viewModel.shoulderProperty().set(Size.XS);
		this.viewModel.sizeProperty().set(Size.S);
		this.viewModel.sleeveLengthProperty().set(Size.XXXL);
		this.viewModel.colorProperty().set(Color.RED);
		this.viewModel.neckStyleProperty().set(NeckStyle.SCOOP_NECK);
		this.viewModel.materialProperty().set(Material.PREMIUM_COTTON);
		this.viewModel.backLengthProperty().set(Size.M);
		this.viewModel.textProperty().set("Small Boss");
		this.viewModel.creatorProperty().set("Default");

		this.viewModel.addShirtToListView();
	}

	@FXML
	void handleAdd(ActionEvent event) {
		Alert newAlert = new Alert(AlertType.ERROR);
		try {
			if (!this.viewModel.addShirtToListView()) {
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
	void handleDeleteShirt(ActionEvent event) {
		this.viewModel.deleteShirt();
	}

	@FXML
	void handleEdit(ActionEvent event) {
		this.viewModel.editShirt();
	}

	/**
	 * Adds a new design request to the observable list of requests.
	 * 
	 * @param requestedShirt The ShirtAttributes object detailing the requested
	 *                       shirt.
	 */
	public void addRequest(Shirt requestedShirt) {
		this.requests.add(requestedShirt);
	}

	/**
	 * Clears all design requests from the observable list.
	 */
	public void clearRequests() {
		this.requests.clear();
	}

	/**
	 * Handles the action of requesting a new shirt design based on user input.
	 *
	 * @param event The action event triggered by the request button.
	 */
	@FXML
	void onRequestButtonClick(ActionEvent event) {
		Alert newAlert = new Alert(AlertType.INFORMATION);
		String confirmationMessage = "Shirt design requested successfully.";
		try {
			newAlert.setContentText(confirmationMessage);
			this.users = this.manager.getUsers();
			this.requests.add(this.viewModel.addShirt());
			Shirt selectedShirt = this.designedListView.getSelectionModel().getSelectedItem();
			this.manager.updateShirt(selectedShirt.getName(), "Requested", this.users.get(this.users.size() - 1).getCreatorName());
		} catch (NullPointerException nPE) {
			newAlert.setContentText(nPE.getLocalizedMessage());

		} catch (IllegalArgumentException iAE) {
			newAlert.setContentText(iAE.getLocalizedMessage());

		}

		newAlert.showAndWait();
	}
	
	@FXML
	void handleViewRequestsButton(ActionEvent event) throws IOException {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Status.fxml"));
	    Parent root = loader.load();
	    ModelAwareController controller = loader.getController();
        controller.setModel(this.manager);
	    Scene scene = new Scene(root);
	    Stage stage = new Stage();
	    stage.setScene(scene);
	    stage.show();
	}
	
	private void showShirtDetails(Shirt selectedShirt) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        Label pocketLabel = new Label("Pocket: " + selectedShirt.hasPocket());
        Label nameLabel = new Label("Name: " + selectedShirt.getName());
        Label shoulderLabel = new Label("Shoulder: " + selectedShirt.getShoulderWidth());
        Label sizeLabel = new Label("Size: " + selectedShirt.getSize().toString());
        Label backLabel = new Label("Back: " + selectedShirt.getBackLength());
        Label styleLabel = new Label("Style: " + selectedShirt.getNeckStyle());
        Label materialLabel = new Label("Material: " + selectedShirt.getMaterial());
        Label colorLabel = new Label("Color: " + selectedShirt.getColor().toString());

        root.getChildren().addAll(pocketLabel, nameLabel, shoulderLabel, sizeLabel, backLabel, styleLabel,
                materialLabel, colorLabel);

        Scene scene = new Scene(root, 300, 300);
        Stage detailStage = new Stage();
        detailStage.setTitle("Shirt Details");
        detailStage.setScene(scene);
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.show();
    }
	/**
	 * Binds UI components to the view model properties.
	 */
	private void bindToViewModel() {
		this.designedListView.itemsProperty().bindBidirectional(this.viewModel.listProperty());
		this.pocketComboBox.valueProperty().bindBidirectional(this.viewModel.pocketProperty());
		this.nameTextField.textProperty().bindBidirectional(this.viewModel.nameProperty());
		this.shoulderLengthComboBox.valueProperty().bindBidirectional(this.viewModel.shoulderProperty());
		this.sizeComboBox.valueProperty().bindBidirectional(this.viewModel.sizeProperty());
		this.sleeveComboBox.valueProperty().bindBidirectional(this.viewModel.sleeveLengthProperty());
		this.colorComboBox.valueProperty().bindBidirectional(this.viewModel.colorProperty());
		this.collarComboBox.valueProperty().bindBidirectional(this.viewModel.neckStyleProperty());
		this.materialComboBox.valueProperty().bindBidirectional(this.viewModel.materialProperty());
		this.backLengthComboBox.valueProperty().bindBidirectional(this.viewModel.backLengthProperty());
		this.textTextField.textProperty().bindBidirectional(this.viewModel.textProperty());
	}

	/**
	 * Populates combo boxes with their respective enum values.
	 */
	private void populateComboBoxes() {
		this.pocketComboBox.setItems(FXCollections.observableArrayList(true, false));
		this.sizeComboBox.setItems(FXCollections.observableArrayList(Size.values()));
		this.backLengthComboBox.setItems(FXCollections.observableArrayList(Size.values()));
		this.shoulderLengthComboBox.setItems(FXCollections.observableArrayList(Size.values()));
		this.sleeveComboBox.setItems(FXCollections.observableArrayList(Size.values()));
		this.colorComboBox.setItems(FXCollections.observableArrayList(Color.values()));
		this.materialComboBox.setItems(FXCollections.observableArrayList(Material.values()));
		this.collarComboBox.setItems(FXCollections.observableArrayList(NeckStyle.values()));
	}

	/**
	 * Sets up the selection handler for the designed list view.
	 */
	private void setupSelectionHandlerForListView() {
		this.designedListView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						this.nameTextField.setText(newValue.getName());
						this.sizeComboBox.valueProperty().setValue(newValue.getSize());
						this.materialComboBox.valueProperty().setValue(newValue.getMaterial());
						this.colorComboBox.valueProperty().setValue(newValue.getColor());
						this.sleeveComboBox.valueProperty().setValue(newValue.getSleeveLength());
						this.shoulderLengthComboBox.valueProperty().setValue(newValue.getShoulderWidth());
						this.backLengthComboBox.valueProperty().setValue(newValue.getBackLength());
						this.collarComboBox.valueProperty().setValue(newValue.getNeckStyle());
						this.pocketComboBox.valueProperty().setValue(newValue.hasPocket());
						this.textTextField.textProperty().setValue(newValue.getShirtText());
					}
				});
	}

	@Override
	public void setModel(model.ShirtCredentialsManager manager) {
		this.manager = (ShirtCredentialsManager) manager;
	}

	public void setUsername(String username) {
	    System.out.println("Setting username: " + username);
	    this.viewModel.creatorProperty().setValue(username);
	    System.out.println("Setting username: " + this.viewModel.creatorProperty().getValue());
	    this.creatorProperty.bindBidirectional(this.viewModel.creatorProperty());
	    System.out.println("Setting username: " + this.viewModel.creatorProperty().getValue());
	    System.out.println("Setting role: " + this.creatorProperty.getValue());
	}

	public void setPassword(String text) {
	    System.out.println("Setting password: " + text);
	    this.viewModel.passwordProperty().setValue(text);
	    this.passwordProperty.bindBidirectional(this.viewModel.passwordProperty());
	    System.out.println("Setting password: " + this.viewModel.passwordProperty().getValue());
	    System.out.println("Setting role: " + this.passwordProperty.getValue());
	}

	public void setRole(String value) {
	    System.out.println("Setting role: " + value);
	    this.viewModel.roleProperty().setValue(value);
		this.roleProperty.bindBidirectional(this.viewModel.roleProperty());
		System.out.println("Setting role: " + this.viewModel.roleProperty().getValue());
		System.out.println("Setting role: " + this.roleProperty.getValue());
	}
}
