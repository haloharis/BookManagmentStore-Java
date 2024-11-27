package Interface;

import BusinessLogic.Book;
import BusinessLogic.ManageEmployeeFacade;
import BusinessLogic.ManageSupplierFacade;
import BusinessLogic.ManageInventoryFacade;
import BusinessLogic.RegisterFacade;

import BusinessLogic.Return;
import BusinessLogic.Inventory;
import BusinessLogic.Main;
import BusinessLogic.Sale;
import BusinessLogic.Payment;
import BusinessLogic.Schedule;
import BusinessLogic.SupplierOrder;
import BusinessLogic.Supplier;
import BusinessLogic.Employee;
import BusinessLogic.SaleLineItem;
import DataBase.DataBase;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea; // Correct import for JavaFX
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import BusinessLogic.Employee;
import BusinessLogic.Schedule;
import BusinessLogic.Main.State;

public class StoreController implements Initializable {
	
	void initializeEmployeeTableView(ArrayList<Employee> list)
	{
		// Initialize Employee table columns
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colId.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        employeeTableView.setItems(FXCollections.observableArrayList(list));
	}
	
	void initializeScheduleTableView(ArrayList<Schedule> list)
	{
	    // Initialize Schedule table columns
	    colDay.setCellValueFactory(new PropertyValueFactory<>("day"));
	    colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
	    colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
	    
	    scheduleTableView.setItems(FXCollections.observableArrayList(list));
	}
	
	
	  
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) 
    {
        manageinventory = new ManageInventoryFacade(); 
        managesupplier = new ManageSupplierFacade();
        registerFacade = new RegisterFacade();
        
        manageEmployeeFacade = new ManageEmployeeFacade();
        
        if (Main.currentState.equals(State.EMPLOYEE))
        {
            employeeList = new ArrayList<>();
            employeeList= DataBase.Instance().getAllEmployees();
            initializeEmployeeTableView(employeeList);

            ObservableList<String> roles = FXCollections.observableArrayList("Manager", "Developer", "Designer");
            selectRole.setItems(roles);
            
            ObservableList<Employee> observableEmployeeList = FXCollections.observableArrayList(employeeList);
            employeeTableView.setItems(observableEmployeeList);
            

            employeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection!= null) {
                    ArrayList<Schedule> schedules = newSelection.getScheduleList();
                    initializeScheduleTableView(schedules);
                } else {
                    scheduleTableView.getItems().clear();
                }
            });

            employeeTableView.setItems(observableEmployeeList);
            employeeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        }
        
    	
        

        if (Main.currentState.equals(State.POS))
        {
    		initializeSaleTableColumns();
    		SaleAnchorPane.setVisible(false);
        	ReturnAnchorPane.setVisible(false);
        	ISBNField.setOnAction(this::handleComboBoxSelection);
        	populateComboBox();
        	SaleIDField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    int saleID = Integer.parseInt(newValue); 
                    updateReturnTable(saleID); 
                }
            });
        }
        if(Main.currentState.equals(State.INVENTORY))
        {

        	CurrentAnchorPane.setVisible(false);
        	initializeTableColumns();
            addInputValidation();

            BookTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) 
            {
            	populateFields(newSelection);
                makeFieldsEditable(false);
            }

          });
        }
        if(Main.currentState.equals(State.SUPPLIER))
        {
        	suppliercurrentBtn.setVisible(false);
        	SuppplierINfoLabel.setVisible(false);
        	CurrentSupplierBookAnchorPane.setVisible(false);
        	CurrentSupplierAnchorPane.setVisible(false);
        	initializeSupplierTableColumns();
    
            SupplierTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) 
                {
                    int supplierId = newSelection.getId();
                    makeSupplierFieldsEditable(false);
    
                    initalizeSupplierBookTableColumns(supplierId);
                }
            });
        }
        
        if(Main.currentState.equals(State.CANCELORDER))
        {
            initializeSupplierOrderTableColumns();
            OrderTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) 
                {
                    SupplierOrder selectedOrder = (SupplierOrder) newSelection;
                    initalizeSupplierOrderBookTableColumns(selectedOrder);
                    OrderInfo.setText(selectedOrder.toString()); // Set the text of OrderInfo to the selected order
                } else {
                    OrderInfo.clear(); 
                }
            });
            
            cancelBtn.setOnAction(event -> {
                if (OrderTableView.getSelectionModel().getSelectedItem()!= null) {
                    SupplierOrder selectedOrder = (SupplierOrder) OrderTableView.getSelectionModel().getSelectedItem();
                    String orderId = selectedOrder.getOrderId();

                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Cancel Order");
                    confirmationAlert.setHeaderText("Are you sure you want to cancel order " + orderId + "?");

                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        manageinventory.cancelOrder(orderId);
                        OrderTableView.refresh();
                        OrderInfo.clear();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "No Order Selected", "Please select an order to cancel.");
                }
            });
        }
        
        if(Main.currentState.equals(State.PLACEORDER))
        {
            ObservableList<Book> tempBookOrderList = FXCollections.observableArrayList();

          orderidField.setText(String.valueOf(manageinventory.assignOrderId()));
          orderidField.setEditable(false);
  
          supplierBox.setItems(FXCollections.observableArrayList(managesupplier.getSuppliers()));
  
          supplierBox.setOnAction(event -> {
        	    Supplier selectedSupplier = supplierBox.getValue();
        	    if (selectedSupplier!= null) {
        	        SupplierInfo.setText(selectedSupplier.getName() + "\n" + selectedSupplier.getContactInfo());
        	        bookBox.setItems(FXCollections.observableArrayList(selectedSupplier.getBookList()));
        	        
        	        tempBookOrderList.clear();
        	        initializeOrderTableColumns(tempBookOrderList);
        	        updateTotalPriceLabel(tempBookOrderList);
        	    }
        	});
  
          TextFormatter<Integer> textFormatter = new TextFormatter<>(change -> {
              String newText = change.getControlNewText();
              if (newText.matches("\\d*")) {
                  return change;
              }
              return null;
          });
          quantityFieldForBook.setTextFormatter(textFormatter);
  
          
          addBookInOrderBtn.setOnAction(event -> {
              Book selectedBook = bookBox.getValue();
              String quantityText = quantityFieldForBook.getText();
  
              if (selectedBook == null) {
                  showAlert(Alert.AlertType.ERROR, "No Book Selected", "Please select a book to add to the order.");
                  return;
              }
  
              if (quantityText == null || quantityText.trim().isEmpty() || !quantityText.matches("\\d+")) {
                  showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Please enter a valid quantity.");
                  return;
              }
  
              int quantity = Integer.parseInt(quantityText);
              if (quantity <= 0) {
                  showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be greater than zero.");
                  return;
              }
  
              Optional<Book> existingBook = tempBookOrderList.stream()
                      .filter(book -> book.equals(selectedBook))
                      .findFirst();
  
              if (existingBook.isPresent()) {
                  Book bookToUpdate = existingBook.get();
                  bookToUpdate.setQuantity(bookToUpdate.getQuantity() + quantity);
                  BookOrderTableView.refresh();
              } else {
                  selectedBook.setQuantity(quantity);
                  tempBookOrderList.add(selectedBook);
              }
  
  
              initializeOrderTableColumns(tempBookOrderList);
              updateTotalPriceLabel(tempBookOrderList);
  
              bookBox.getSelectionModel().clearSelection();
              quantityFieldForBook.clear();
          });
          
          removeBookInOrder.setOnAction(event -> {
              Book selectedBook = BookOrderTableView.getSelectionModel().getSelectedItem();
              
              if (selectedBook != null) {
                  tempBookOrderList.remove(selectedBook);
                  updateTotalPriceLabel(tempBookOrderList);
              } else {
                  showAlert(Alert.AlertType.ERROR, "No Book Selected", "Please select a book to remove from the order.");
              }
          });
          
          placeOrderBtn.setOnAction(event -> {
        	    Supplier selectedSupplier = supplierBox.getValue();
        	    List<Book> orderedBooks = tempBookOrderList;
        	    double totalPrice = Double.parseDouble(TotlaPriceLabel.getText());
        	    manageinventory.placeOrder(selectedSupplier, orderedBooks, totalPrice);

        	    tempBookOrderList.clear();
        	    initializeOrderTableColumns(tempBookOrderList);
        	    updateTotalPriceLabel(tempBookOrderList);

        	    showAlert(Alert.AlertType.CONFIRMATION, "Order Placed", "Your Order Has Been Confirmed");

        	    Main.currentState = State.PAYMENT;
        	    FXMLLoader loader = new FXMLLoader(getClass().getResource("PaymentToSupplier.fxml"));
        	    Parent root = null;
        	    try {
        	        root = loader.load();
        	    } catch (IOException e) {
        	        e.printStackTrace();
        	    }  
        	    StoreController mainController = loader.getController();
        	    mainController.setStage(stage);

        	    Scene scene = new Scene(root);
        	    stage.setScene(scene);
        	    stage.show();
        	});
          

        }       
        if (Main.currentState.equals(State.PAYMENT)) {
            paymentmethodanchorpane.setVisible(false);
            ConfirmPaymentBtn.setVisible(false);

            SupplierOrder supplierOrder = manageinventory.getLastSupplierOrder();
            OrderDetails.setText(supplierOrder.toString());
            TotalProceConfirm.setText("Total Price: " + supplierOrder.getTotalAmount());

            PaymentGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    if (cashToggle.isSelected()) {
                        paymentmethodanchorpane.setVisible(false);
                        ConfirmPaymentBtn.setVisible(true);
                    } else if (creditToggle.isSelected()) {
                        datePickerPayment.setVisible(true);
                        expirayDateLabel.setVisible(true);
                        cvvField.setVisible(true);
                        cvvPaymentFields.setVisible(true);
                        paymentmethodanchorpane.setVisible(true);
                        ConfirmPaymentBtn.setVisible(true);
                    } else if (bankToggle.isSelected()) {
                        datePickerPayment.setVisible(false);
                        expirayDateLabel.setVisible(false);
                        cvvField.setVisible(false);
                        cvvPaymentFields.setVisible(false);
                        paymentmethodanchorpane.setVisible(true);
                        ConfirmPaymentBtn.setVisible(true);
                    }
                }
            });
            numberField.setTextFormatter(new TextFormatter<>(c ->
            {
                if (c.getControlNewText().matches("\\d{0,5}"))
                {
                    return c;
                } else
                {
                    return null;
                }
            }));

            cvvPaymentFields.setTextFormatter(new TextFormatter<>(c ->
            {
                if (c.getControlNewText().matches("\\d{0,3}"))
                {
                    return c;
                } else
                {
                    return null;
                }
            }));
            ConfirmPaymentBtn.setOnAction(event -> 
            {
                
                manageinventory.getLastSupplierOrder().getPayment().setTotalAmount(manageinventory.getLastSupplierOrder().getTotalAmount());

                if (cashToggle.isSelected())
                {
                	manageinventory.getLastSupplierOrder().getPayment().setDetails("Cash");
                	manageinventory.getLastSupplierOrder().getPayment().setDate(LocalDate.now().toString());
                    
                    showAlert(Alert.AlertType.CONFIRMATION, "Payment Confirmed", "Your payment has been confirmed.");
                }
                else if (creditToggle.isSelected())
                {
                    if (numberField.getText().isEmpty() || numberField.getText().length() != 5 || cvvPaymentFields.getText().isEmpty() || cvvPaymentFields.getText().length() != 3 || datePickerPayment.getValue() == null)
                    {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid credit card details.");
                        return;
                    }
                    manageinventory.getLastSupplierOrder().getPayment().makeCreditCardPayment(numberField.getText(), paymentNameField.getText(), datePickerPayment.getValue().toString(), cvvPaymentFields.getText());
                    manageinventory.getLastSupplierOrder().getPayment().setDate(LocalDate.now().toString());

                    showAlert(Alert.AlertType.CONFIRMATION, "Payment Confirmed", "Your payment has been confirmed.");
                }
                else if (bankToggle.isSelected())
                {
                    if (numberField.getText().isEmpty() || numberField.getText().length() != 5 || paymentNameField.getText().isEmpty())
                    {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid bank transfer details.");
                        return;
                    }
                    manageinventory.getLastSupplierOrder().getPayment().makeBankTransfer(numberField.getText(), paymentNameField.getText());
                    manageinventory.getLastSupplierOrder().getPayment().setDate(LocalDate.now().toString());

                    showAlert(Alert.AlertType.CONFIRMATION, "Payment Confirmed", "Your payment has been confirmed.");
                }

                manageinventory.confirmPayment(manageinventory.getLastSupplierOrder(), manageinventory.getLastSupplierOrder().getPayment());

                try {
                    Main.currentState = State.CANCELORDER;
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplierOrders.fxml"));
                    Parent root = loader.load();
                    StoreController mainController = loader.getController();
                    mainController.setStage(stage);

                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }


    }
    
	
	  @FXML
	    private TextField idField;

	    @FXML
	    private TextField nameField;
	    

	    @FXML
	    private TextField searchField;

	 @FXML
	    private TableView<Employee> employeeTableView;

	    @FXML
	    private TableColumn<Employee, String> colName;

	    @FXML
	    private TableColumn<Employee, String> colId;

	    @FXML
	    private TableColumn<Employee, String> colRole;

	    @FXML
	    private TableView<Schedule> scheduleTableView;

	    @FXML
	    private TableColumn<Schedule, String> colDay;

	    @FXML
	    private TableColumn<Schedule, String> colStartTime;

	    @FXML
	    private TableColumn<Schedule, String> colEndTime;

	    private ArrayList<Employee> employeeList;
	    private ManageEmployeeFacade manageEmployeeFacade;

	    private int nextEmployeeId = 1;

	 @FXML
	    private Button CompleteSaleBn;

	    @FXML
	    private Button ConfirmReturnBn;

	    @FXML
	    private Button EnterBookBn;
	    
	    @FXML
	    private ComboBox<String> ISBNField;

	    @FXML
	    private Button LogOutBn;

	    @FXML
	    private Button NewSaleBn;

	    @FXML
	    private Button POSBackBtn;
	    
	    @FXML
	    private Button addEmployeeBtn;


	    @FXML
	    private TableView<SaleLineItem> POSTable;

	    @FXML
	    private Button ReturnBookBn;

	    @FXML
	    private TableView<SaleLineItem> ReturnTable;

	    @FXML
	    private ComboBox<String> SaleIDField;

	    @FXML
	    private TableColumn<SaleLineItem, Integer> pColItemNum;

	    @FXML
	    private TableColumn<SaleLineItem, Double> pColPrice;

	    @FXML
	    private TableColumn<SaleLineItem, Integer> pColQuantity;

	    @FXML
	    private TableColumn<SaleLineItem, String> pColTitle;

	    @FXML
	    private Label pauthor;

	    @FXML
	    private Label pisbn;

	    @FXML
	    private Label pprice;

	    @FXML
	    private Label ptitle;
	    
	    @FXML
	    private Label saleTotal;

	    @FXML
	    private TableColumn<SaleLineItem, Integer> rColItemNum;

	    @FXML
	    private TableColumn<SaleLineItem, Double> rColPrice;

	    @FXML
	    private TableColumn<SaleLineItem, Integer> rColQuantity;

	    @FXML
	    private TableColumn<SaleLineItem, String> rColTitle;
	    @FXML
	    private ComboBox<String> selectRole;
	    
	    @FXML
	    private Button addTimeSlotBtn;
	    
	    @FXML
	    private Button deleteEmployeeBtn;

	    @FXML
	    private Button editEmployeeBtn;
	    
	    @FXML
	    private Button viewEmployeeBtn;
	    
	    @FXML
	    private AnchorPane ReturnAnchorPane;
	    
	    @FXML
	    private AnchorPane SaleAnchorPane;
	    
	    @FXML
	    private TextField SaleItemQuantity;
	    

	    @FXML
	    public void viewEmployee() {
	        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
	        if (selectedEmployee != null) {
	            ArrayList<Schedule> schedules = selectedEmployee.getScheduleList();
	            scheduleTableView.setItems(FXCollections.observableArrayList(schedules));
	            displayEmployeeDetails(selectedEmployee, FXCollections.observableArrayList(schedules));
	        } else {
	            scheduleTableView.getItems().clear();
	        }
	    }

    private String generateEmployeeId() {
        String formattedId = String.format("EMP%03d", nextEmployeeId);
        nextEmployeeId++;
        return formattedId;
    }

    @FXML
    public void addEmployee() {
        String name = nameField.getText();
        String ID = idField.getText();
        String role = selectRole.getValue();

        if (name.isEmpty() || ID.isEmpty() || role == null || role.isEmpty()) {
            showAlert(AlertType.ERROR, "Alert", "Please fill all the fields");
            return;
        }

        Employee newEmployee = manageEmployeeFacade.createNewProfile(ID, name, role);
        employeeList.add(newEmployee);
        initializeEmployeeTableView(employeeList);
        employeeTableView.refresh();
        clearFields1();
    }

    @FXML
    public void deleteEmployee() {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            manageEmployeeFacade.deleteProfile(selectedEmployee.getEmployeeID());
            employeeList.remove(selectedEmployee);
            employeeTableView.setItems(FXCollections.observableArrayList(employeeList));
            employeeTableView.refresh();
        } else {
            showAlert(AlertType.ERROR, "Error", "Please select an employee to delete");
        }
    }


    @FXML
    public void editEmployee() {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            String name = nameField.getText();
            String ID = idField.getText();
            String role = selectRole.getValue();

            if (name.isEmpty() || ID.isEmpty() || role == null || role.isEmpty()) {
                showAlert(AlertType.ERROR, "Alert", "Please fill all the fields");
                return;
            }

            selectedEmployee.setFullName(name);
            selectedEmployee.setRole(role);
            manageEmployeeFacade.modifyProfile(selectedEmployee);
            employeeTableView.setItems(FXCollections.observableArrayList(employeeList));

            employeeTableView.refresh();
        } else {
            showAlert(AlertType.ERROR, "Error", "Please select an employee to edit");
        }
    }
    
    private void clearFields1() {
        nameField.clear();
        idField.clear();
        selectRole.getSelectionModel().clearSelection();
    }
    @FXML
    public void addTimeSlots() {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        if (selectedEmployee!= null) {
            // Create a dialog window
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Time Slot");

            // Set the header text
            dialog.setHeaderText("Enter Time Slot Details");

            // Create a grid pane for the dialog content
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            // Create a combo box for the day
            ComboBox<String> dayComboBox = new ComboBox<>();
            dayComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
            dayComboBox.getSelectionModel().selectFirst();

            // Create text fields for start time and end time
            TextField startTimeFieldDialog = new TextField();
            TextField endTimeFieldDialog = new TextField();

            // Add labels and fields to the grid pane
            grid.add(new Label("Day:"), 0, 0);
            grid.add(dayComboBox, 1, 0);
            grid.add(new Label("Start Time:"), 0, 1);
            grid.add(startTimeFieldDialog, 1, 1);
            grid.add(new Label("End Time:"), 0, 2);
            grid.add(endTimeFieldDialog, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Add buttons to the dialog
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Wait for the user to close the dialog
            Optional<ButtonType> result = dialog.showAndWait();

            // Process the result if OK button is clicked
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String day = dayComboBox.getSelectionModel().getSelectedItem();
                String startTime = startTimeFieldDialog.getText();
                String endTime = endTimeFieldDialog.getText();

                if (day == null || startTime.isEmpty() || endTime.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                    return;
                }

                // Check if the start time and end time are in the correct format
                if (!isValidTime(startTime) ||!isValidTime(endTime)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid time format. Please use HH:mm.");
                    return;
                }

                Schedule newSchedule = manageEmployeeFacade.setScheduleForEmp(selectedEmployee.getEmployeeID(), day, startTime, endTime);

                // Check if a schedule already exists for the specified day
                boolean scheduleExists = false;
                for (Schedule schedule : selectedEmployee.getScheduleList()) {
                    if (schedule.getDay().equalsIgnoreCase(day)) {
                        // Replace the existing schedule with the new values
                        schedule.setStartTime(startTime);
                        schedule.setEndTime(endTime);
                        scheduleExists = true;
                        break;
                    }
                }

                // If no schedule exists for the specified day, add a new schedule
                if (!scheduleExists) 
                {
                    selectedEmployee.getScheduleList().add(newSchedule);
                }

                // Refresh the scheduleTableView to reflect the changes
                scheduleTableView.setItems(FXCollections.observableArrayList(selectedEmployee.getScheduleList()));
                scheduleTableView.refresh();
                employeeTableView.refresh();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an employee.");
        }
    }

    // Method to check if a time string is in the correct format
    private boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    @FXML
    public void deleteTimeSlot() {
        Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
        Schedule selectedSchedule = scheduleTableView.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null && selectedSchedule != null) {
            selectedEmployee.getScheduleList().remove(selectedSchedule);
            scheduleTableView.refresh();
        } else {
            showAlert("Error", "Please select an employee and a time slot to delete.");
        }
    }

    @FXML
    public void searchEmployee() {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Employee> filteredList = FXCollections.observableArrayList();

        for (Employee employee : employeeList) {
            if (employee.getFullName().toLowerCase().contains(searchText) ||
                    employee.getEmployeeID().toLowerCase().contains(searchText) ||
                    employee.getRole().toLowerCase().contains(searchText)) {
                filteredList.add(employee);
            }
        }

        employeeTableView.setItems(filteredList);
    }

    private void displayEmployeeDetails(Employee selectedEmployee, ObservableList<Schedule> schedules) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Employee Details");
        alert.setHeaderText(null);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.addRow(0, new Label("Employee ID:"), new Label(selectedEmployee.getEmployeeID()));
        gridPane.addRow(1, new Label("Name:"), new Label(selectedEmployee.getFullName()));
        gridPane.addRow(2, new Label("Role:"), new Label(selectedEmployee.getRole()));

        TableView<Schedule> scheduleTableView = new TableView<>();
        TableColumn<Schedule, String> colDay = new TableColumn<>("Day");
        colDay.setCellValueFactory(cellData -> cellData.getValue().dayProperty());
        TableColumn<Schedule, String> colStartTime = new TableColumn<>("Start Time");
        colStartTime.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        TableColumn<Schedule, String> colEndTime = new TableColumn<>("End Time");
        colEndTime.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());

        scheduleTableView.getColumns().addAll(colDay, colStartTime, colEndTime);
        scheduleTableView.setItems(schedules);

        gridPane.addRow(3, new Label("Schedule:"), scheduleTableView);

        alert.getDialogPane().setContent(gridPane);
        alert.showAndWait();
    }
    
    private Stage stage;
    

    @FXML
    private MenuItem cancelOrder;
    
    @FXML
    private MenuItem manageSuppliers;

    @FXML
    private MenuItem manageinventory1;

    @FXML
    private MenuItem placeorder;

    @FXML
    private Button AddBookBtn;

    @FXML
    private TableView<Book> BookTableView;

    @FXML
    private AnchorPane CurrentAnchorPane;

    @FXML
    private Button DeleteBookBtn;

    @FXML
    private Button EditBookBtn;

    @FXML
    private Label Label5;

    @FXML
    private Label Label6;

    @FXML
    private TextField SearchField;

    @FXML
    private Button ViewBookBtn;

    @FXML
    private TextField authorField;

    @FXML
    private TableColumn<Book, String> colTitle;

    @FXML
    private TableColumn<Book, String> colAuthor;

    @FXML
    private TableColumn<Book, String> colISBN;

    @FXML
    private TableColumn<Book, String> colPublisher;

    @FXML
    private TableColumn<Book, Integer> colQuantity;

    @FXML
    private TableColumn<Book, Double> colPrice;

    @FXML
    private Button currentBtn;

    @FXML
    private Label headingAnchor;

    @FXML
    private TextField isbnField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField publisherField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField titleField;
    private FilteredList<Book> filteredData;
    
    private RegisterFacade registerFacade;

    @FXML
    void CancelOrder(ActionEvent event) throws IOException 
    {
    	Main.currentState = State.CANCELORDER;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplierOrders.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void ManageInventory(ActionEvent event) throws IOException 
    {
    	Main.currentState = State.INVENTORY;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void ManageSuppliers(ActionEvent event) throws IOException 
    {
    	Main.currentState = State.SUPPLIER;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Supplier.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void PlaceOrderFromInventory(ActionEvent event) throws IOException 
    {
    	Main.currentState = State.PLACEORDER;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PlaceOrderToSupplier.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    
    
    private ManageInventoryFacade manageinventory;
    private ManageSupplierFacade managesupplier;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
   

    @FXML
    void ClickSearchField(MouseEvent event) 
    {
        Label5.setText("Searching...");

        filteredData = new FilteredList<>(FXCollections.observableArrayList(Inventory.Instance().getBookList()), b -> true);
        SearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }
                String searchWord = newValue.toLowerCase();

                if (book.getAuthor().toLowerCase().indexOf(searchWord) > -1) {
                    return true;
                } else if (book.getISBN().toLowerCase().indexOf(searchWord) > -1) {
                    return true;
                } else if (book.getTitle().toLowerCase().indexOf(searchWord) > -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        SortedList<Book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(BookTableView.comparatorProperty());
        BookTableView.setItems(sortedData);

    }

    @FXML
    void addBook(ActionEvent event) {
    	makeFieldsEditable(true);

        CurrentAnchorPane.setVisible(true);
        headingAnchor.setText("Adding Book");
        currentBtn.setVisible(true);
        currentBtn.setText("Add Book");
        clearFields1();
        makeFieldsEditable(true);
    }


    @FXML
    void deleteBook(ActionEvent event) {

        CurrentAnchorPane.setVisible(true);
        headingAnchor.setText("Deleting Book");
        currentBtn.setText("Delete Book");
        currentBtn.setVisible(true);

        Book selectedBook = BookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
        	makeFieldsEditable(false);

            populateFields(selectedBook);
            makeFieldsEditable(false);
        }
    }

    @FXML
    void editBook(ActionEvent event) {
        Book selectedBook = BookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            CurrentAnchorPane.setVisible(true);
            headingAnchor.setText("Editing Book");
            currentBtn.setText("Edit Book");
            currentBtn.setVisible(true);
            populateFields(selectedBook);

            titleField.setEditable(true);
            authorField.setEditable(true);
            publisherField.setEditable(true);
            quantityField.setEditable(true);
            priceField.setEditable(true);

            isbnField.setEditable(false); // Set the ISBN field as uneditable
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to edit.");
        }
    }


    @FXML
    void viewBook(ActionEvent event) {

        Book selectedBook = BookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
        	makeFieldsEditable(false);

            CurrentAnchorPane.setVisible(true);
            headingAnchor.setText("Viewing Book");
            currentBtn.setVisible(false);
            populateFields(selectedBook);
            makeFieldsEditable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to view.");
        }
    }
    

@FXML
void completeAction(ActionEvent event) {
    if (currentBtn.getText().equals("Add Book")) {

        String errorMessage = validateFields();
        if (!errorMessage.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "ERROR", errorMessage);
        } else {
            Book newBook = null;
            newBook = manageinventory.addNewBook(isbnField.getText(), titleField.getText(), authorField.getText(), publisherField.getText(), Integer.parseInt(quantityField.getText()), Double.parseDouble(priceField.getText()));

            if (newBook != null) {
                showAlert(Alert.AlertType.INFORMATION, "Book Added", "The book was added successfully.");
                BookTableView.getItems().add(newBook);
                BookTableView.refresh();

                clearFields1();
                
                CurrentAnchorPane.setVisible(false); // Hide the anchor pane
            } else {
                showAlert(Alert.AlertType.ERROR, "ERROR", "A book with the same ISBN already exists.");
            }
        }
    } else if (currentBtn.getText().equals("Delete Book")) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this book?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Book selectedBook = BookTableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                manageinventory.removeBook(selectedBook);
                BookTableView.getItems().remove(selectedBook);
                clearFields1();
                BookTableView.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Book Deleted", "The book was deleted successfully.");
                CurrentAnchorPane.setVisible(false); 
                BookTableView.refresh();

            } else {
                showAlert(Alert.AlertType.ERROR, "ERROR", "No book selected for deletion.");
            }
        }
    } else if (currentBtn.getText().equals("Edit Book")) {
    	
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to edit this book?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Book selectedBook = BookTableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                selectedBook.setTitle(titleField.getText());
                selectedBook.setAuthor(authorField.getText());
                selectedBook.setISBN(isbnField.getText());
                selectedBook.setPublisher(publisherField.getText());
                selectedBook.setQuantity(Integer.parseInt(quantityField.getText()));
                selectedBook.setPrice(Double.parseDouble(priceField.getText()));
                manageinventory.updateBook(selectedBook);
                BookTableView.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Book Edited", "Book details updated successfully!");
                clearFields1();
                CurrentAnchorPane.setVisible(false); // Hide the anchor pane
            } else {
                showAlert(Alert.AlertType.ERROR, "ERROR", "No book selected for editing.");
            }
        }
    }
}
    
    
    
    
  
    private void initializeTableColumns() {
    	colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        BookTableView.setItems(FXCollections.observableArrayList(Inventory.Instance().getBookList()));

    }

    private void addInputValidation() {
        quantityField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });

        priceField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d") && !event.getCharacter().equals(".")
                    || (event.getCharacter().equals(".") && priceField.getText().contains("."))) {
                event.consume();
            }
        });
    }

    private void populateFields(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getISBN());
        publisherField.setText(book.getPublisher());
        quantityField.setText(String.valueOf(book.getQuantity()));
        priceField.setText(String.valueOf(book.getPrice()));
    }

    private void makeFieldsEditable(boolean editable) {
        titleField.setEditable(editable);
        authorField.setEditable(editable);
        isbnField.setEditable(editable);
        publisherField.setEditable(editable);
        quantityField.setEditable(editable);
        priceField.setEditable(editable);
    }


    private void clearFields() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        publisherField.clear();
        quantityField.clear();
        priceField.clear();
    }
    
    public void populateComboBox() {
    	ArrayList<Book> blist = Inventory.Instance().getBookList();
        List<String> isbnList = new ArrayList<>();
        for (Book book : blist) {
            isbnList.add(book.getISBN());
        }

        ISBNField.setItems(FXCollections.observableArrayList(isbnList));
        
        
    }
    
    private void handleComboBoxSelection(ActionEvent event) {
        // Get the selected item from the ComboBox
        String selectedOption = ISBNField.getSelectionModel().getSelectedItem();
        
        // Set the label text based on the selected option
        if (selectedOption != null) {
        	String isbn = ISBNField.getSelectionModel().getSelectedItem();
        	Book b = Inventory.Instance().findBook(isbn);
            pisbn.setText(isbn);
            ptitle.setText(b.getTitle());
            pauthor.setText(b.getAuthor());
            String stringValue = String.valueOf(b.getPrice());
            pprice.setText(stringValue);
        } else {
        	pisbn.setText("isbn");
            ptitle.setText("title");
            pauthor.setText("author");
            pprice.setText("0.00"); // Clear the label if nothing is selected
        }
    }

    
    @FXML
    void initializeSale(ActionEvent event) {
    	ReturnAnchorPane.setVisible(false);
    	SaleAnchorPane.setVisible(true);
        registerFacade.initializeSale();
    	
    }
    
    @FXML
    void enterBookDetails(ActionEvent event) {
    	 if (SaleItemQuantity.getText().isEmpty() || SaleItemQuantity.getText() == null || ISBNField.getSelectionModel().isEmpty()) {
             Alert alert = new Alert(AlertType.WARNING);
             alert.setTitle("Validation Error");
             alert.setHeaderText(null);
             alert.setContentText("Please enter all fields");
             alert.showAndWait();
             
         }
    	 else
    	 {
    		 String isbn = ISBNField.getSelectionModel().getSelectedItem();

    	    	String text = SaleItemQuantity.getText();
    	        try {
    	            int quantity = Integer.parseInt(text);
    	            registerFacade.enterBookDetails(isbn, quantity);
    	            
    	            String total = String.valueOf(registerFacade.getSale().getTotal());
    	            saleTotal.setText(total);
    	            
    	            POSTable.setItems(FXCollections.observableArrayList(registerFacade.getSale().getSaleLineItems()));

    	        } catch (NumberFormatException e) {
    	            System.out.println("Invalid input: Not an integer");
    	        }
    	 }
    	
    }
    
    @FXML
    void endSale(ActionEvent event) {
    	  // Check if salesItemQuantity TextField is empty
   	 if (SaleItemQuantity.getText().isEmpty() || SaleItemQuantity.getText() == null || ISBNField.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter all fields");
            alert.showAndWait();
            return;
        }
        else {   
        	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        	alert.setTitle("Sale Completed");
            alert.setHeaderText(null);
            alert.setContentText("Sale completed!\n"
            		+ "Total Amount: $" + saleTotal.getText());
            alert.showAndWait();
            
            registerFacade.endSale();
        	SaleItemQuantity.clear();
        	POSTable.getItems().clear();
        	ISBNField.getSelectionModel().clearSelection();
        	saleTotal.setText("0.00");
        }
 
    	
    }





    @FXML
    void logOut(ActionEvent event) {

    }

    
    private void updateReturnTable(int saleID) {
        try {
            // Clear existing items in the table
            ReturnTable.getItems().clear();

            // Fetch the sale using the findSale function
            Sale sale = DataBase.Instance().findSale(saleID);

            // If the sale is found
            if (sale != null) {
                // Populate the table with the sale line items from the fetched sale
                for (SaleLineItem saleLineItem : sale.getSaleLineItems()) {
                    ReturnTable.getItems().add(saleLineItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    @FXML
    void returnBook(ActionEvent event) {
    	SaleAnchorPane.setVisible(false);
    	ReturnAnchorPane.setVisible(true);
    	
    	List<String> saleIDs = DataBase.Instance().getAllSaleIDs();
        SaleIDField.setItems(FXCollections.observableArrayList(saleIDs));
        
    }
    
    @FXML
    void confirmReturn(ActionEvent event) {
        // Get selected sale line item from the ReturnTable
        SaleLineItem selectedSaleLineItem = ReturnTable.getSelectionModel().getSelectedItem();
        if (selectedSaleLineItem == null) {
            // Show an error message if no sale line item is selected
            showAlert(Alert.AlertType.ERROR, "Error", "No sale line item selected", "Please select a sale line item.");
            return;
        }

        // Extract necessary information
        String isbn = selectedSaleLineItem.getBookISBN();
        int quantity = selectedSaleLineItem.getQuantity();
        int saleId = Integer.parseInt(SaleIDField.getValue());

        // Call registerFacade.returnBook with the extracted information
        registerFacade.returnBook(isbn, quantity, saleId);

        // Calculate refund amount (assuming subtotal is the refund amount)
        double refundAmount = selectedSaleLineItem.getSubTotal();

        // Show confirmation popup with refund amount
        showAlert(Alert.AlertType.INFORMATION, "Refund Completed", "Refund completed!", "Refund amount: $" + refundAmount);

        // Clear ComboBox and ReturnTable
        SaleIDField.getSelectionModel().clearSelection();
        ReturnTable.getItems().clear();
    }

    // Helper method to show an Alert
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    
    
    private void initializeSaleTableColumns() {
    	pColTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        pColQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pColPrice.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        pColItemNum.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(POSTable.getItems().indexOf(column.getValue()) + 1));
        pColItemNum.setSortable(false);  

        rColItemNum.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(ReturnTable.getItems().indexOf(column.getValue()) + 1));
        rColPrice.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        rColQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        rColTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
    }


    private String validateFields() {
        StringBuilder errorMessage = new StringBuilder();
        if (titleField.getText() == null || titleField.getText().isEmpty()) {
            errorMessage.append("Title is required.\n");
        }
        if (authorField.getText() == null || authorField.getText().isEmpty()) {
            errorMessage.append("Author is required.\n");
        }
        if (isbnField.getText() == null || isbnField.getText().isEmpty()) {
            errorMessage.append("ISBN is required.\n");
        }
        if (publisherField.getText() == null || publisherField.getText().isEmpty()) {
            errorMessage.append("Publisher is required.\n");
        }
        if (quantityField.getText() == null || quantityField.getText().isEmpty()) {
            errorMessage.append("Quantity is required.\n");
        } else {
            try {
                Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("Quantity must be a number.\n");
            }
        }
        if (priceField.getText() == null || priceField.getText().isEmpty()) {
            errorMessage.append("Price is required.\n");
        } else {
            try {
                Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("Price must be a number.\n");
            }
        }
        return errorMessage.toString();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
  
    private void initializeSupplierOrderTableColumns() {
        colOrderIDCancelOrder.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colSupplierIDCancelOrder.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colStatusCancelOrder.setCellValueFactory(new PropertyValueFactory<>("status"));
        coltotalAmountCancelOrder.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        ArrayList<SupplierOrder> orders = manageinventory.getOrderlist();
        OrderTableView.setItems(FXCollections.observableArrayList(orders));
    }

    private void initalizeSupplierOrderBookTableColumns(SupplierOrder selectedOrder) {
        colBookISBNCancelOrder.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        colBookTitleCancelOrder.setCellValueFactory(new PropertyValueFactory<>("title"));
        colBookAuthorCancelOrder.setCellValueFactory(new PropertyValueFactory<>("author"));
        colPublisherCancelOrderokPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colBookPriceCancelOrder.setCellValueFactory(new PropertyValueFactory<>("price"));
        colBookQuantityCancelOrder.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotalPriceCancelOrder.setCellValueFactory(data -> {
            Book book = data.getValue();
            double totalPrice = book.getPrice() * book.getQuantity();
            return new SimpleDoubleProperty(totalPrice).asObject();
        });
        
        ArrayList<Book> orderBooks = selectedOrder.getOrderBookList();
        OrderBookTableView.setItems(FXCollections.observableArrayList(orderBooks));
    }
    
    
private void initializeOrderTableColumns(ObservableList<Book> tempBookOrderList) {
    colTempTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    colTempAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
    colTempPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
    colTempPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    colTempQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    colTempISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));

    colTempTotalPrice.setCellValueFactory(data -> {
        Book book = data.getValue();
        double totalPrice = book.getPrice() * book.getQuantity();
        return new SimpleDoubleProperty(totalPrice).asObject();
    });


    BookOrderTableView.setItems(tempBookOrderList);
}


private void updateTotalPriceLabel(ObservableList<Book> tempBookOrderList) {
 double totalPrice = tempBookOrderList.stream()
         .mapToDouble(book -> book.getPrice() * book.getQuantity())
         .sum();
 
 DecimalFormat decimalFormat = new DecimalFormat("#.###");
 String formattedTotalPrice = decimalFormat.format(totalPrice);

 TotlaPriceLabel.setText(formattedTotalPrice);
}

    private void clearOrderFields() {
        bookBox.getSelectionModel().clearSelection();
        quantityFieldForBook.clear();
        TotlaPriceLabel.setText("");
    }

    
    
    private void initalizeSupplierBookTableColumns(int id) 
    {   
    	colSupplierBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSupplierBookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colSupplierBookISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        colSupplierBookPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colSupplierBookPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        SupplierBookTableView.setItems(FXCollections.observableArrayList(DataBase.Instance().getBooksBySupplier(id)));
    }
    
    

        @FXML
        private Button AddSupplierBtn;

        @FXML
        private Button SupplierBackBtn;
        

        @FXML
        private Button placeorderBackBtn;
        @FXML
        private AnchorPane CurrentSupplierAnchorPane;

        @FXML
        private AnchorPane CurrentSupplierBookAnchorPane;


        @FXML
        private Button EditSupplierBtn;

        @FXML
        private TextField SupplierBookSearchField;

        @FXML
        private TableView<Book> SupplierBookTableView;

        @FXML
        private TextField SupplierNameField;

        @FXML
        private TextField SupplierSearchField;

        @FXML
        private Label SupplierStatusLabel;

        @FXML
        private TableView<Supplier> SupplierTableView;

        @FXML
        private Text SuppplierINfoLabel;

        @FXML
        private Button ViewSupplierBtn;

        @FXML
        private Button addBookSupplierBtn;

        @FXML
        private Button addSupplierBooksBtn;

        @FXML
        private TextField authorField1;

        @FXML
        private Button backBtn;

        @FXML
        private TableColumn<Supplier, String> colContccInfo;

        @FXML
        private TableColumn<Book, String> colSupplierBookAuthor;

        @FXML
        private TableColumn<Book, String> colSupplierBookISBN;

        @FXML
        private TableColumn<Book, Double> colSupplierBookPrice;

        @FXML
        private TableColumn<Book, String> colSupplierBookPublisher;

        @FXML
        private TableColumn<Book, String> colSupplierBookTitle;

        @FXML
        private TableColumn<Supplier, Integer> colSupplierID;

        @FXML
        private TableColumn<Supplier, String> colSupplierName;

        @FXML
        private TextField contactInfoField;

        @FXML
        private Label addbooklabel;
        @FXML
        private Label headingAnchor1;

        @FXML
        private Label headingSupplierAnchor;

        @FXML
        private ComboBox<Book> isbnField1;

        @FXML
        private TextField priceField1;

        @FXML
        private TextField publisherField1;


        @FXML
        private TextField supplierIDField;

        @FXML
        private Button suppliercurrentBtn;

        @FXML
        private TextField titleField1;

        private Supplier currentSupplier;

        @FXML
        void SupplierGoBack(ActionEvent event) throws IOException 
        {
        	Main.currentState = State.INVENTORY;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
            Parent root = loader.load();

            StoreController mainController = loader.getController();

            mainController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

        @FXML
        void PlaceOrderGoBack(ActionEvent event) throws IOException {
        	Main.currentState = State.INVENTORY;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
            Parent root = loader.load();

            StoreController mainController = loader.getController();

            mainController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        }

        private void initializeSupplierTableColumns() {
            colSupplierID.setCellValueFactory(new PropertyValueFactory<>("id"));
            colSupplierName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colContccInfo.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));

            List<Supplier> suppliers = DataBase.Instance().getSuppliers();
            SupplierTableView.setItems(FXCollections.observableArrayList(suppliers));
        }
        
       

        private void initializeBookTableColumns(List<Book> bookList) {
            colSupplierBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colSupplierBookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
            colSupplierBookISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
            colSupplierBookPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
            colSupplierBookPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            
            SupplierBookTableView.setItems(FXCollections.observableArrayList(bookList));
        }


        @FXML
        void addSupplier(ActionEvent event) 
        {
        	List<Book> inventoryBooks = Inventory.Instance().getBookList();
        	isbnField1.setItems(FXCollections.observableArrayList(inventoryBooks));
        	SupplierTableView.getItems().clear();
        	SupplierBookTableView.getItems().clear();
        	SupplierNameField.clear();
        	contactInfoField.clear();
        	headingSupplierAnchor.setText("ADDING SUPPLIER");
        	suppliercurrentBtn.setText("ADD SUPPLIER");
            CurrentSupplierAnchorPane.setVisible(true);
            SupplierNameField.setEditable(true);
            contactInfoField.setEditable(true);
            
            SupplierNameField.setEditable(true);
            contactInfoField.setEditable(true);
            
            List<Supplier> suppliers = DataBase.Instance().getSuppliers();
            int newId = suppliers.isEmpty() ? 1 : suppliers.get(suppliers.size() - 1).getId() + 1;
            supplierIDField.setText(String.valueOf(newId));
            supplierIDField.setEditable(false);
        }

        private List<Book> supplierBooks = new ArrayList<>();

        @FXML
        void AddSupplierBooks(ActionEvent event) 
        {
        	    if (!SupplierNameField.getText().isEmpty() && !contactInfoField.getText().isEmpty()) {
                currentSupplier = new Supplier(Integer.parseInt(supplierIDField.getText()), SupplierNameField.getText(), contactInfoField.getText());
                CurrentSupplierAnchorPane.setVisible(false);
                CurrentSupplierBookAnchorPane.setVisible(true);
                
                SuppplierINfoLabel.setText("Supplier ID: "+currentSupplier.getId()+", Supplier: " + currentSupplier.getName());
                SuppplierINfoLabel.setVisible(true);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Incomplete Fields", "Please fill in all fields.");
            }
        }

        @FXML
        void AddBookSupplier(ActionEvent event) 
        {
            suppliercurrentBtn.setVisible(true);
            Book selectedBook = isbnField1.getValue();
            if (selectedBook!= null &&!titleField1.getText().isEmpty() &&!authorField1.getText().isEmpty()
                    &&!publisherField1.getText().isEmpty() &&!priceField1.getText().isEmpty()) {
                Book newBook = new Book(selectedBook.getISBN(), titleField1.getText(), authorField1.getText(),
                        publisherField1.getText(), 0, Double.parseDouble(priceField1.getText()));
                
                for (Book book : supplierBooks) {
                    if (book.getISBN().equals(newBook.getISBN()) &&
                        book.getTitle().equals(newBook.getTitle()) &&
                        book.getAuthor().equals(newBook.getAuthor()) &&
                        book.getPublisher().equals(newBook.getPublisher()) &&
                        book.getPrice() == newBook.getPrice()) {
                        showAlert(Alert.AlertType.ERROR, "Book Already Exists", "The book is already in the supplier's book list.");
                        return;
                    }
                }
                supplierBooks.add(newBook);
                initializeBookTableColumns(supplierBooks);
                titleField1.clear();
                authorField1.clear();
                isbnField1.getSelectionModel().clearSelection();
                publisherField1.clear();
                priceField1.clear();
            }
        }
        @FXML
        void completeSupplierAction(ActionEvent event) {
            if (suppliercurrentBtn.getText().equals("ADD SUPPLIER")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Add Supplier");
                alert.setContentText("Are you sure you want to add this supplier?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    int id = Integer.parseInt(supplierIDField.getText());
                    String name = SupplierNameField.getText();
                    String contactInfo = contactInfoField.getText();
                    managesupplier.addSupplier(id, name, contactInfo, supplierBooks);
                    initializeSupplierTableColumns();
                    supplierBooks.clear();
                    suppliercurrentBtn.setVisible(false);
                    CurrentSupplierAnchorPane.setVisible(false);
                    CurrentSupplierBookAnchorPane.setVisible(false);
                    SuppplierINfoLabel.setVisible(false);
                }
            } else if (suppliercurrentBtn.getText().equals("EDIT SUPPLIER")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Edit Supplier");
                alert.setContentText("Are you sure you want to edit this supplier?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    int id = Integer.parseInt(supplierIDField.getText());
                    String name = SupplierNameField.getText();
                    String contactInfo = contactInfoField.getText();
                    managesupplier.editSupplier(id, name, contactInfo, supplierBooks);
                    initializeSupplierTableColumns();
                    supplierBooks.clear();
                    suppliercurrentBtn.setVisible(false);
                    CurrentSupplierAnchorPane.setVisible(false);
                    CurrentSupplierBookAnchorPane.setVisible(false);
                    SuppplierINfoLabel.setVisible(false);

                }
            } 
        }

        @FXML
        void editSupplier(ActionEvent event) 
        {
        	
            suppliercurrentBtn.setText("EDIT SUPPLIER");
            suppliercurrentBtn.setVisible(true);

        	List<Book> inventoryBooks = Inventory.Instance().getBookList();
            isbnField1.setItems(FXCollections.observableArrayList(inventoryBooks));
            
            headingSupplierAnchor.setText("EDITING SUPPLIER");
            CurrentSupplierAnchorPane.setVisible(true);
            SupplierNameField.setEditable(true);
            contactInfoField.setEditable(true);
            
            Supplier selectedSupplier = SupplierTableView.getSelectionModel().getSelectedItem();
            if (selectedSupplier != null) 
            {
                CurrentSupplierAnchorPane.setVisible(true);
                addbooklabel.setVisible(true);
                addSupplierBooksBtn.setVisible(true);
                populateSupplierFields(selectedSupplier);
                makeSupplierFieldsEditable(true);
                
                supplierBooks = managesupplier.getSupplierBooks(selectedSupplier.getId());
                initializeBookTableColumns(supplierBooks);
            } 
            else 
            {
                showAlert(Alert.AlertType.INFORMATION, "Incomplete Fields", "Please fill in all fields.");
            }
        }

        @FXML
        void viewSupplier(ActionEvent event) {
            Supplier selectedSupplier = SupplierTableView.getSelectionModel().getSelectedItem();
            if (selectedSupplier != null) 
            {
                populateSupplierFields(selectedSupplier);
                makeSupplierFieldsEditable(false);
                CurrentSupplierAnchorPane.setVisible(true);
                addSupplierBooksBtn.setVisible(false);
                addbooklabel.setVisible(false);


            } 
            else 
            {
                showAlert(Alert.AlertType.INFORMATION, "Incomplete Fields", "Please fill in all fields.");
            }
        }

        private void populateSupplierFields(Supplier supplier) {
            supplierIDField.setText(String.valueOf(supplier.getId()));
            SupplierNameField.setText(supplier.getName());
            contactInfoField.setText(supplier.getContactInfo());
        }

        private void makeSupplierFieldsEditable(boolean editable) {
            SupplierNameField.setEditable(editable);
            contactInfoField.setEditable(editable);
        }

        @FXML
        void GoBack(ActionEvent event) {
            CurrentSupplierBookAnchorPane.setVisible(false);
            CurrentSupplierAnchorPane.setVisible(true);
            SuppplierINfoLabel.setVisible(false);
        }

        @FXML
        void ClickSupplierBookSearchField(ActionEvent event) {
            // Implement search functionality if needed
        }

        @FXML
        void ClickSupplierSearchField(ActionEvent event) {
            // Implement search functionality if needed
        }
    
        @FXML
        void AddBookForSupplier(ActionEvent event) {
            String selectedText = isbnField1.getSelectionModel().getSelectedItem().toString();
            for (Book book : isbnField1.getItems()) {
                String bookText = book.toString();
                if (bookText.equals(selectedText)) {
                    titleField1.setText(book.getTitle());
                    authorField1.setText(book.getAuthor());
                    publisherField1.setText(book.getPublisher());
                    priceField1.setText(String.valueOf(book.getPrice()));
                    break;
                }
            }
        }



    @FXML
    private TableView<Book> BookOrderTableView;

    @FXML
    private Label MainHeading;

    @FXML
    private AnchorPane PlaceOrderAnchorPane;

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private Label StatusLabel;

    @FXML
    private TextArea SupplierInfo;
    @FXML
    private Button addBookInOrderBtn;

    @FXML
    private ComboBox<Book> bookBox;

    @FXML
    private TextField orderidField;

    @FXML
    private Button placeOrderBtn;

    @FXML
    private TextField quantityFieldForBook;

    @FXML
    private Label status;

    @FXML
    private Button removeBookInOrder;

    @FXML
    private TableColumn<Book, String> colTempAuthor;

    @FXML
    private TableColumn<Book, String> colTempISBN;

    @FXML
    private TableColumn<Book, Double> colTempPrice;

    @FXML
    private TableColumn<Book, String> colTempPublisher;

    @FXML
    private TableColumn<Book, Integer> colTempQuantity;

    @FXML
    private TableColumn<Book, String> colTempTitle;

    @FXML
    private TableColumn<Book, Double> colTempTotalPrice;
    
    @FXML
    private Label TotlaPriceLabel;
    @FXML
    private ComboBox<Supplier> supplierBox;
    

    @FXML
    void RemoveBookInOrder(ActionEvent event) {

    }



    @FXML
    void AddBookInOrder(ActionEvent event) {

    }

    @FXML
    void ChooseBooks(ActionEvent event) {

    }

    @FXML
    void ChooseSupplier(ActionEvent event) {

    }

    @FXML
    void PlaceOrder(ActionEvent event) {
       
    }
    
    @FXML
    private Button ConfirmPaymentBtn;

    @FXML
    private TextArea OrderDetails;

    @FXML
    private Label PMainHeading;

    @FXML
    private Label PStatusLabel;

    @FXML
    private AnchorPane PaymentAchorPane;

    @FXML
    private ToggleGroup PaymentGroup;

    @FXML
    private Label Pstatus;

    @FXML
    private Text TotalProceConfirm;

    @FXML
    private Text TotalProceConfirm1;

    @FXML
    private RadioButton bankToggle;

    @FXML
    private RadioButton cashToggle;

    @FXML
    private RadioButton creditToggle;

    @FXML
    private Label cvvField;

    @FXML
    private TextField cvvPaymentFields;

    @FXML
    private DatePicker datePickerPayment;

    @FXML
    private Label expirayDateLabel;

    @FXML
    private TextField numberField;

    @FXML
    private TextField paymentNameField;

    @FXML
    private AnchorPane paymentmethodanchorpane;

    @FXML
    void ConfirmPayment(ActionEvent event) {

    }

    @FXML
    private Label MStatusLabel;

    @FXML
    private Label Mstatus;

    @FXML
    private Button caheirBtn;

    @FXML
    private Button inventoryManagerBtn;

    @FXML
    private Label mMainHeading;

    @FXML
    private Button storeManagerBtn;
    
    @FXML
    private Button loginBtn;

    private String user;
    @FXML
    void Cashier(ActionEvent event) {
    	user = "cashier";
    	Boolean loggedin = Login(user);
    	

    }
    
    @FXML
    void Login(ActionEvent event) throws IOException {

    	if(user.equals("inventorymanager"))
    	{
    		Main.currentState = State.INVENTORY;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
            Parent root = loader.load();

            StoreController mainController = loader.getController();

            mainController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    	}
    	else if (user.equals("cashier"))
    	{
    		Main.currentState = State.POS;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("POS.fxml"));
            Parent root = loader.load();

            StoreController mainController = loader.getController();

            mainController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    	}
    	else if (user.equals("storemanager"))
    	{

    		Main.currentState = State.EMPLOYEE;
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee.fxml"));
            Parent root = loader.load();

            StoreController mainController = loader.getController();

            mainController.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    	}
    	
    }

    @FXML
    void InventoryManager(ActionEvent event) {
    	user = "inventorymanager";

    	Boolean loggedin = Login(user);

    }

    @FXML
    void StoreManager(ActionEvent event) {
    	user = "storemanager";

    	Boolean loggedin = Login(user);

    }
    
    Boolean Login(String type)
    {
    	return true;
    }
    
    @FXML
    private Label CLabel;

    @FXML
    private TextField OrderBookSearchField;

    @FXML
    private TableView<Book> OrderBookTableView;

    @FXML
    private TextArea OrderInfo;

    @FXML
    private TextField OrderSearchFields;

    @FXML
    private TableView<SupplierOrder> OrderTableView;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button SupplierOrderBackBtn;
    
    @FXML
    private Button InventoryBackBtn;
    @FXML
    private TableColumn<Book, String> colBookAuthorCancelOrder;

    @FXML
    private TableColumn<Book, String> colBookISBNCancelOrder;

    @FXML
    private TableColumn<Book, Double> colBookPriceCancelOrder;

    @FXML
    private TableColumn<Book, Integer> colBookQuantityCancelOrder;

    @FXML
    private TableColumn<Book, String> colBookTitleCancelOrder;

    @FXML
    private TableColumn<SupplierOrder, String> colOrderIDCancelOrder;

    @FXML
    private TableColumn<Book, String> colPublisherCancelOrderokPublisher;

    @FXML
    private TableColumn<SupplierOrder, String> colStatusCancelOrder;

    @FXML
    private TableColumn<SupplierOrder, Integer> colSupplierIDCancelOrder;

    @FXML
    private TableColumn<Book, Double> colTotalPriceCancelOrder;

    @FXML
    private TableColumn<SupplierOrder, Double> coltotalAmountCancelOrder;

    @FXML
    void CancelSupplierOrder(ActionEvent event) {

    }

    @FXML
    void ClickOrderBookSearchField(ActionEvent event) {
        Label5.setText("Searching...");

        FilteredList<SupplierOrder> filteredData = new FilteredList<>(FXCollections.observableArrayList(manageinventory.getOrderlist()), so -> true);
        OrderBookSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(so -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }
                String searchWord = newValue.toLowerCase();

                if (so.getOrderId().toLowerCase().indexOf(searchWord) > -1) {
                    return true;
                }else if (so.getSupplier().getName().toLowerCase().indexOf(searchWord) > -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        SortedList<SupplierOrder> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(OrderTableView.comparatorProperty());
        OrderTableView.setItems(sortedData);
    }

    @FXML
    void ClickOrderSearchField(ActionEvent event) {
        Label5.setText("Searching...");

        SupplierOrder currentSupplierOrder = OrderTableView.getSelectionModel().getSelectedItem(); // Get the selected SupplierOrder
        if (currentSupplierOrder != null) {
            FilteredList<Book> filteredData = new FilteredList<>(FXCollections.observableArrayList(currentSupplierOrder.getOrderBookList()), b -> true);
            OrderSearchFields.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(b -> {
                    if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                        return true;
                    }
                    String searchWord = newValue.toLowerCase();

                    if (b.getISBN().toLowerCase().indexOf(searchWord) > -1) {
                        return true;
                    } else if (b.getTitle().toLowerCase().indexOf(searchWord) > -1) {
                        return true;
                    } else if (b.getAuthor().toLowerCase().indexOf(searchWord) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                });
            });
            SortedList<Book> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(BookTableView.comparatorProperty());
            BookTableView.setItems(sortedData);
        }
    }
    
    @FXML
    void SupplierOrdersGoBack(ActionEvent event) throws IOException 
    {
    	Main.currentState = State.INVENTORY;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void POSGoBack(ActionEvent event) throws IOException {
    	Main.currentState = State.STORE;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private Button EmployeeBackBtn;
    
    @FXML
    void EmployeeGoback(ActionEvent event) throws IOException {
    	Main.currentState = State.STORE;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void InventoryGoBack(ActionEvent event) throws IOException {
    	Main.currentState = State.STORE;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();

        StoreController mainController = loader.getController();

        mainController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

}
