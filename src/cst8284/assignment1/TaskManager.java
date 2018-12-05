package cst8284.assignment1;

import java.util.ArrayList;
import java.util.Date;

import cst8284.assignment1.FileUtils;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;

public class TaskManager extends Application {

	private ArrayList<ToDo> toDoArray;
	private static int currentToDoElement;
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		setPrimaryStage(primaryStage);
		primaryStage.setTitle("Tip of the Day");
		primaryStage.setScene(getSplashScreen(new Text("Algonquin College\nTask Manager\n Click to continue")));
		primaryStage.show();
	}

	public Scene getSplashScreen(Text defaultText) {
		defaultText.setStyle("-fx-font: 48px Tahoma; -fx-stroke: white; -fx-stroke-width: 5;");
		StackPane startPane = new StackPane();
		startPane.getChildren().add(defaultText);

		RotateTransition spin = new RotateTransition(Duration.millis(200), defaultText);
		spin.setCycleCount(Timeline.INDEFINITE);
		spin.setFromAngle(0);
		spin.setToAngle(350f);

		TranslateTransition bounce = new TranslateTransition(Duration.millis(2000), defaultText);
		bounce.setFromX(-200);
		bounce.setToX(+200);
		bounce.setAutoReverse(true);
		bounce.setCycleCount(Timeline.INDEFINITE);

		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(spin, bounce);
		parallelTransition.play();

		startPane.setOnMouseClicked(e -> loadNewToDoIntoScene());
		return (new Scene(startPane, 1024, 768, Color.DARKGREEN));
	}

	public Scene getToDoScene(ToDo td) {
		if (td == null) { // use null to signal initial setup, i.e. ToDo[0]
			FileUtils fUtils = new FileUtils();
			ArrayList<ToDo> tdRawArray = fUtils.getToDoArray(FileUtils.getAbsPath());
			ArrayList<ToDo> tdCompactArray = getToDoArrayWithoutEmpties(tdRawArray);
			setToDoArray(tdCompactArray);
			setToDoElement(0);
			td = getToDoArray().get(getToDoElement());
		}
		return (new Scene(getToDoPane(td)));
	}

	public BorderPane getToDoPane(ToDo td) {
		VBox vbLeft = new VBox();
		vbLeft.setMinWidth(120);

		VBox vbRight = new VBox();
		vbRight.setMinWidth(120);

		BorderPane rootNode = new BorderPane();
		rootNode.setLeft(vbLeft);
		rootNode.setRight(vbRight);
		rootNode.setBottom(getBottomPane(td, rootNode));
		rootNode.setCenter(getCenterPane(td));
		rootNode.setTop(getTopPanelMenuBar());
		return rootNode;
	}

	public GridPane getCenterPane(ToDo td) {

		GridPane gp = new GridPane();
		gp.setPadding(new Insets(50));
		gp.setPrefWidth(1200);

		gp.setVgap(10);
		gp.setHgap(40);
		gp.setStyle("-fx-font: 20px Tahoma; -fx-stroke: black; -fx-stroke-width: 1;");

		Label lblTask = new Label("Tip");
		gp.add(lblTask, 0, 0);
		TextField txfTitle = new TextField(td.getTitle());
		gp.add(txfTitle, 1, 0);

		Label lblSubject = new Label("Subject");
		gp.add(lblSubject, 0, 1);
		TextArea txaSubject = new TextArea(td.getSubject());
		gp.add(txaSubject, 1, 1);

		Label lblDate = new Label("Due Date");
		gp.add(lblDate, 0, 2);
		TextField txfDate = new TextField(td.getDueDate().toString());
		gp.add(txfDate, 1, 2);

		Label lblPriority = new Label("Priority");
		gp.add(lblPriority, 0, 3);
		ToggleGroup tglGroup = new ToggleGroup();
		RadioButton rb1 = (new RadioButton("1   "));
		rb1.setToggleGroup(tglGroup);
		RadioButton rb2 = (new RadioButton("2   "));
		rb2.setToggleGroup(tglGroup);
		RadioButton rb3 = (new RadioButton("3   "));
		rb3.setToggleGroup(tglGroup);
		int pr = td.getPriority();
		RadioButton rbSet = (pr == 1) ? rb1 : (pr == 2) ? rb2 : (pr == 3) ? rb3 : null;
		rbSet.setSelected(rbSet != null);
		HBox hRadioButtons = new HBox();
		hRadioButtons.getChildren().addAll(rb1, rb2, rb3);
		gp.add(hRadioButtons, 1, 3);
		return gp;
	}

	public HBox getBottomPane(ToDo td, BorderPane root) {

		HBox paneCtlBtns = new HBox(10);
		paneCtlBtns.setStyle("-fx-font: 50px Tahoma; -fx-stroke: black; -fx-stroke-width: 1;");
		paneCtlBtns.setAlignment(Pos.CENTER);
		paneCtlBtns.setPadding(new Insets(50));

		Button btnFirst = new Button("\u23ee"); // btnFirst.setMinSize(80, 80);
		Button btnBack = new Button("\u23ea"); // btnBack.setMinSize(80, 80);
		Button btnNext = new Button("\u23e9"); // btnNext.setMinSize(80, 80);
		Button btnLast = new Button("\u23ed"); // btnLast.setMinSize(80, 80);

		btnFirst.setOnAction(e -> {
			setToDoElement(0);
			btnBack.fire();
		});

		btnBack.setOnAction(e -> {
			int toDoElement = getToDoElement();
			setToDoElement(toDoElement <= 0 ? 0 : --toDoElement);
			btnFirst.setDisable(getToDoElement() == 0);
			btnBack.setDisable(getToDoElement() == 0);
			btnNext.setDisable(getToDoElement() == getToDoArray().size() - 1);
			btnLast.setDisable(getToDoElement() == getToDoArray().size() - 1);
			root.setCenter(getCenterPane(getToDoArray().get(getToDoElement())));
		});

		btnLast.setOnAction(e -> {
			setToDoElement(getToDoArray().size() - 1);
			btnNext.fire();
		});

		btnNext.setOnAction(e -> {
			int toDoElement = getToDoElement();
			if (toDoElement < getToDoArray().size() - 1)
				setToDoElement(++toDoElement);
			btnFirst.setDisable(toDoElement == 0);
			btnBack.setDisable(toDoElement == 0);
			btnNext.setDisable(toDoElement == getToDoArray().size() - 1);
			btnLast.setDisable(toDoElement == getToDoArray().size() - 1);
			root.setCenter(getCenterPane(getToDoArray().get(getToDoElement())));
		});

		if (getToDoElement() == 0)
			btnFirst.fire(); // set default button conditions
		paneCtlBtns.getChildren().addAll(btnFirst, btnBack, btnNext, btnLast);
		return paneCtlBtns;
	}

	private MenuBar getTopPanelMenuBar() {

		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");

		MenuItem mnuOpen = new MenuItem("Open");
		mnuOpen.setOnAction(e -> loadNewToDoIntoScene());

		MenuItem mnuSave = new MenuItem("Save");
		mnuSave.setOnAction(e -> {new Alert(AlertType.INFORMATION, "Feature not implemented in this demo");});

		MenuItem mnuAddToDo = new MenuItem("Add ToDo...");
		mnuAddToDo.setOnAction(e -> {
			ToDo tdNew = new ToDo("","Demo Test #" + (int)(Math.random() *1000), (int)(Math.random()*3+1), new Date(), false, false, false);
			getToDoArray().add(getToDoElement(), tdNew);
		    getPrimaryStage().setScene(getToDoScene(getToDoArray().get(getToDoElement())));	
		});
		
		MenuItem mnuRemoveToDo = new MenuItem("Remove ToDo...");
		mnuRemoveToDo.setOnAction(e -> {
			getToDoArray().remove(getToDoElement());
			setToDoElement(getToDoElement()==0?0:getToDoElement()-1);
			getPrimaryStage().setScene(getToDoScene(getToDoArray().get(getToDoElement())));
		});
		
		MenuItem mnuExitToDo = new MenuItem("Exit");
		mnuExitToDo.setOnAction(e -> Platform.exit());

		fileMenu.getItems().addAll(mnuOpen, mnuSave, mnuAddToDo, mnuRemoveToDo, mnuExitToDo);
		menuBar.getMenus().add(fileMenu);
		return menuBar;
	}

	private ArrayList<ToDo> getToDoArrayWithoutEmpties(ArrayList<ToDo> tdRawAr) {
		ArrayList<ToDo> NoEmptiesArray = new ArrayList<>();
		for (ToDo thisToDo : tdRawAr)
			if (!thisToDo.isEmptySet())
				NoEmptiesArray.add(thisToDo);
		return NoEmptiesArray;
	}

	private void loadNewToDoIntoScene() {
		FileUtils.getToDoFile(getPrimaryStage());
		getPrimaryStage().setScene(getToDoScene(null));
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	public void setToDoArray(ArrayList<ToDo> td) {
		toDoArray = td;
	}

	public ArrayList<ToDo> getToDoArray() {
		return this.toDoArray;
	}

	public static void setToDoElement(int currentElementNumber) {
		currentToDoElement = currentElementNumber;
	}

	public static int getToDoElement() {
		return currentToDoElement;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
