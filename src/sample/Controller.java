package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sun.security.krb5.internal.crypto.Des;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private TextField poidsText, gainText, nbrText;
    @FXML
    private TextArea weightMax;
    @FXML
    private TableView<object> tableItem;
    @FXML
    private TableColumn<object, Integer> poidArea, gainArea, nbrArea;
    @FXML
    private Label sol, objs;
    @FXML
    private ComboBox<String> method;
    @FXML
    private VBox params;

    private int opt = 0;
    private int[] solObjects;

    public ObservableList<object> data = FXCollections.observableArrayList(
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nbrArea.setCellValueFactory(new PropertyValueFactory<object, Integer>("Nbr"));
        gainArea.setCellValueFactory(new PropertyValueFactory<object, Integer>("Gain"));
        poidArea.setCellValueFactory(new PropertyValueFactory<object, Integer>("Poid"));
        tableItem.setItems(data);

        ObservableList<String> items = FXCollections.observableArrayList("Heuristique",
                "Programmation dynamique",
                "Algorithme Génétique",
                "Recuit Simulé",
                "Branch and Bound");
        method.setItems(items);
    }

    public void clearVBox(VBox box) {
        box.getChildren().clear();
    }

    public void onAlgorithmChoice(ActionEvent e) {
        clearVBox(params);
        Label nothing = new Label("Aucun paramètre");
        nothing.setStyle("-fx-text-fill: white; -fx-font-size: 32");

        switch(method.getSelectionModel().getSelectedItem()) {
            case "Heuristique":
                params.getChildren().add(nothing);
                break;
            case "Programmation dynamique":
                params.getChildren().add(nothing);
                break;
            case "Algorithme Génétique":
                HBox tuple = new HBox();
                Label iterationLabel = new Label("Itérations:                 ");
                iterationLabel.setStyle("-fx-text-fill: white;");
                tuple.getChildren().add(iterationLabel);
                TextField iterations = new TextField();
                tuple.getChildren().add(iterations);

                HBox tuple2 = new HBox();
                Label popLabel = new Label("Taille population:     ");
                popLabel.setStyle("-fx-text-fill: white;");
                tuple2.getChildren().add(popLabel);
                TextField pop = new TextField();
                tuple2.getChildren().add(pop);

                HBox tuple3 = new HBox();
                Label mutLabel = new Label("Mutation:                 ");
                mutLabel.setStyle("-fx-text-fill: white;");
                tuple3.getChildren().add(mutLabel);
                TextField mut = new TextField();
                tuple3.getChildren().add(mut);

                HBox tuple4 = new HBox();
                Label crossLabel = new Label("Crossover:                ");
                crossLabel.setStyle("-fx-text-fill: white;");
                tuple4.getChildren().add(crossLabel);
                TextField cross = new TextField();
                tuple4.getChildren().add(cross);

                params.getChildren().addAll(tuple, tuple2, tuple3, tuple4);
                break;
            case "Recuit Simulé":
                HBox tuple5 = new HBox();
                Label tl = new Label("T:                    ");
                tl.setStyle("-fx-text-fill: white;");
                tuple5.getChildren().add(tl);
                TextField t = new TextField();
                tuple5.getChildren().add(t);

                HBox tuple6 = new HBox();
                Label epsl = new Label("eps:                ");
                epsl.setStyle("-fx-text-fill: white;");
                tuple6.getChildren().add(epsl);
                TextField eps = new TextField();
                tuple6.getChildren().add(eps);

                HBox tuple7 = new HBox();
                Label kl = new Label("K:                    ");
                kl.setStyle("-fx-text-fill: white;");
                tuple7.getChildren().add(kl);
                TextField k = new TextField();
                tuple7.getChildren().add(k);

                HBox tuple8 = new HBox();
                Label il = new Label("Iterations:       ");
                il.setStyle("-fx-text-fill: white;");
                tuple8.getChildren().add(il);
                TextField i = new TextField();
                tuple8.getChildren().add(i);

                params.getChildren().addAll(tuple5, tuple6, tuple7, tuple8);
                break;
            case "Branch and Bound":
                params.getChildren().add(nothing);
                break;
            default:
                break;
        }
    }

    @FXML
    public void onCalcule(ActionEvent event) {
        switch (method.getSelectionModel().getSelectedItem()) {
            case "Heuristique":
                Heuristic(event);

                break;
            case "Programmation dynamique":
                PD(event);
                break;
            case "Algorithme Génétique":
                int iterations = Integer.parseInt(((TextField) ((HBox) params.getChildren().get(0)).getChildren().get(1)).getText());
                int pop = Integer.parseInt(((TextField) ((HBox) params.getChildren().get(1)).getChildren().get(1)).getText());
                int mut = Integer.parseInt(((TextField) ((HBox) params.getChildren().get(2)).getChildren().get(1)).getText());
                int cross = Integer.parseInt(((TextField) ((HBox) params.getChildren().get(3)).getChildren().get(1)).getText());
                GA(event, iterations, pop, mut, cross);
                break;
            case "Recuit Simulé":
                float T = Float.parseFloat(((TextField) ((HBox) params.getChildren().get(0)).getChildren().get(1)).getText());
                float eps = Float.parseFloat(((TextField) ((HBox) params.getChildren().get(1)).getChildren().get(1)).getText());
                float k = Float.parseFloat(((TextField) ((HBox) params.getChildren().get(2)).getChildren().get(1)).getText());
                int iterations_rs = Integer.parseInt(((TextField) ((HBox) params.getChildren().get(3)).getChildren().get(1)).getText());
                Recuit(event, T, eps, k, iterations_rs);
                break;
            case "Branch and Bound":
                BNB(event);
                break;
        }
    }


    //Branch and Bound
    @FXML
    private void BNB(ActionEvent event) {
        if (!data.isEmpty() && weightMax.getText().length() > 0) {
            Date d1 = new Date();
            object[] lots = new object[data.size()];
            for (int i = 0; i < data.size(); i++) lots[i] = data.get(i);
            int weight = Integer.parseInt(weightMax.getText());
            for (int i = 0; i < lots.length; i++)
                for (int j = i; j < lots.length; j++)
                    if ((lots[i].getGain() / lots[i].getPoid()) < (lots[j].getGain() / lots[j].getPoid()))
                        Permute(i, j, lots);
            for (int i = 0; i < lots.length; i++) lots[i].setNbr(weight / lots[i].getPoid());
            solObjects = new int[lots.length];
            Arrays.fill(solObjects, 0);
            Test(0, lots, weight, 0, new int[lots.length]);
            Date d2 = new Date();
            show(String.valueOf((d2.getTime() - d1.getTime())));
            System.out.println(opt + "\n");
            sol.setText(" " + opt);
            objs.setText("");
            for (int i = 0; i < solObjects.length; i++)
                objs.setText(objs.getText() + lots[i].getPoid() + " " + lots[i].getGain() + " " + solObjects[i] + " \n");
        }
    }


    //Heuristique
    @FXML
    private void Heuristic(ActionEvent event) {
        if (!data.isEmpty() && weightMax.getText().length() > 0) {
            Date d1 = new Date();
            opt = 0;
            object[] lots = new object[data.size()];
            for (int i = 0; i < data.size(); i++) lots[i] = data.get(i);
            int weight = Integer.parseInt(weightMax.getText());
            for (int i = 0; i < lots.length; i++)
                for (int j = i; j < lots.length; j++)
                    if ((lots[i].getGain() / lots[i].getPoid()) < (lots[j].getGain() / lots[j].getPoid()))
                        Permute(i, j, lots);
            for (int i = 0; i < lots.length; i++) lots[i].setNbr(weight / lots[i].getPoid());
            solObjects = new int[lots.length];
            Arrays.fill(solObjects, 0);
            for (int i = 0; i < lots.length; i++) {
                solObjects[i] += weight / lots[i].getPoid();
                opt += solObjects[i] * lots[i].getGain();
                weight -= solObjects[i] * lots[i].getPoid();
            }
            Date d2 = new Date();
            show(String.valueOf((d2.getTime() - d1.getTime())));
            System.out.println(opt + "\n");
            sol.setText(" " + opt);
            for (int i = 0; i < solObjects.length; i++)
                objs.setText(objs.getText() + lots[i].getPoid() + " " + lots[i].getGain() + " " + solObjects[i] + " \n");
        }
    }


    private void Permute(int i, int j, object[] lots) {
        object tmp = new object(0, lots[i].getPoid(), lots[i].getGain());
        lots[i].setPoid(lots[j].getPoid());
        lots[i].setGain(lots[j].getGain());
        lots[j].setPoid(tmp.getPoid());
        lots[j].setGain(tmp.getGain());

    }


    private void Test(int num, object[] lot, int poidsReste, int sol, int[] objects) {
        if (num < lot.length) {
            for (int i = poidsReste / lot[num].getPoid(); i >= 0; i--) {
                //System.out.println(i + "opt " +opt+ "here  "+((float) sol + (((float) poidsReste/lot[num].getPoid()) * lot[num].getGain())));
                if (((float) opt) < (float) sol + (((float) poidsReste / lot[num].getPoid()) * lot[num].getGain())) {
                    objects[num] = i;
                    Test(num + 1, lot, poidsReste - lot[num].getPoid() * i, sol + lot[num].getGain() * i, objects);
                } else {
                    break;
                }
            }
        } else {
            opt = sol;
            for (int i = 0; i < objects.length; i++) solObjects[i] = objects[i];
        }
    }


    //Programmation Dynamique
    @FXML
    private void PD(ActionEvent event) {
        if (!data.isEmpty() && weightMax.getText().length() > 0) {
            Date d1 = new Date();
            object[] lots = new object[data.size()];
            for (int i = 0; i < data.size(); i++) lots[i] = data.get(i);
            int taille = 0;
            for (int i = 0; i < lots.length; i++)
                lots[i].setNbr(Integer.parseInt(weightMax.getText()) / lots[i].getPoid());
            for (int i = 0; i < lots.length; i++) taille += lots[i].getNbr();
            obj[] objects = new obj[taille];
            int count = 0;
            for (int i = 0; i < lots.length; i++)
                for (int j = 0; j < lots[i].getNbr(); j++) {
                    objects[count] = new obj(lots[i].getGain(), lots[i].getPoid());
                    count++;
                }
            int w = Integer.parseInt(weightMax.getText());

            int[][] matrix = new int[objects.length][w + 1];
            matrix[0][objects[0].getPoids()] = objects[0].getGain();
            for (int i = 1; i < objects.length; i++) {
                for (int j = 1; j <= w; j++) {
                    if (j >= objects[i].getPoids()) {

                        matrix[i][j] = Math.max(matrix[i - 1][j], matrix[i - 1][j - objects[i].getPoids()] + objects[i].getGain());
                    } else {
                        matrix[i][j] = matrix[i - 1][j];
                    }
                    if (matrix[i][j - 1] > matrix[i][j]) matrix[i][j] = matrix[i][j - 1];
                }
            }
            sol.setText(" Solution: " + String.valueOf(matrix[objects.length - 1][w]));

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++)
                    System.out.print(matrix[i][j] + " ");
                System.out.println();
            }

            boolean[] isThere = new boolean[objects.length];
            Arrays.fill(isThere, false);
            int i = objects.length - 1;
            int j = w;
            while (matrix[i][j] != 0 && j > 0) {
                if (matrix[i][j] == matrix[i][j - 1]) {
                    j--;
                    continue;
                }
                if (i > 0) if (matrix[i][j] == matrix[i - 1][j]) {
                    i--;
                    continue;
                }
                j = j - objects[i].getPoids();
                isThere[i] = true;
                System.out.println(i);
                if (i > 0) i--;
            }
            objs.setText("objects");
            int[] many = new int[lots.length];
            Arrays.fill(many, 0);
            for (int k = 0; k < objects.length; k++) {
                if (isThere[k]) {
                    for (int ii = 0; ii < lots.length; ii++) {
                        if (objects[k].equals(new obj(lots[ii].getGain(), lots[ii].getPoid())) && many[ii] < lots[ii].getNbr()) {
                            many[ii]++;
                            break;
                        }
                    }
                    //objs.setText(objs.getText() + " " + k);
                }
            }
            Date d2 = new Date();
            show(String.valueOf((d2.getTime() - d1.getTime())));
            for (int k = 0; k < many.length; k++) {
                if (k % 5 == 0) objs.setText(objs.getText() + "\n");
                objs.setText(objs.getText() + " " + k + "(" + many[k] + ")");
            }
        }

    }


    @FXML
    private void onAjouter(ActionEvent event) {
        data = tableItem.getItems();
        if (poidsText.getText().length() > 0 && gainText.getText().length() > 0 && nbrText.getText().length() > 0)
            data.add(
                    new object(Integer.parseInt(nbrText.getText()), Integer.parseInt(poidsText.getText()), Integer.parseInt(gainText.getText()))
            );
        System.out.println(data);
        tableItem.setItems(data);
        System.out.println(tableItem.getItems().toString());
        poidsText.setText("");
        gainText.setText("");
        nbrText.setText("");
    }


    //Genetic Algorithm
    @FXML
    private void GA(ActionEvent event, int iter, int pop, int mut, int cross) {
        Date d1 = new Date();
        if (!data.isEmpty() && weightMax.getText().length() > 0) {
            opt = 0;
            object[] lots = new object[data.size()];
            int taille = lots.length;
            for (int i = 0; i < taille; i++) lots[i] = data.get(i);
            int weight = Integer.parseInt(weightMax.getText());
            for (int i = 0; i < taille; i++) lots[i].setNbr(weight / lots[i].getPoid());
            solObjects = new int[taille];
            Arrays.fill(solObjects, 0);
            int[][] population = new int[pop][taille];
            int pas = lots.length / 20;
            for (int i = 0; i < pop; i++)
                for (int j = 0; j < taille; j += pas) population[i][j] = (int) (Math.round(Math.random()) * 5);
            for (int it = 0; it < iter; it++) {

                //selection
                int[] eval = new int[pop];
                int total = 1;
                for (int i = 0; i < pop; i++) {
                    eval[i] = Evaluation(lots, population[i], weight);
                    total += eval[i];
                }
                //Roullette
                int[] pourcentage = new int[pop];
                pourcentage[0] = (eval[0] / total) * 100;
                int[] position = new int[pop];
                for (int i = 1; i < pop; i++) pourcentage[i] = pourcentage[i - 1] + ((eval[i] / total) * 100);
                for (int i = 0; i < pop; i++) {
                    int rand = (int) (Math.round(Math.random()) * 100);
                    for (int j = 1; j < pop; j++) if (pourcentage[j - 1] < rand) position[i] = j;
                }
                //Crossover
                int[] cromosome1 = population[position[(int) (Math.round(Math.random()) * 3)]];
                int[] cromosome2 = population[position[(int) (Math.round(Math.random()) * 3)]];
                int[] cromosome1prime = new int[taille], cromosome2prime = new int[taille];
                int crosspos = (int) (Math.round(Math.random()) * (taille - 2));
                for (int i = 0; i < taille; i++) {
                    if (i < crosspos) {
                        cromosome1prime[i] = cromosome1[i];
                        cromosome2prime[i] = cromosome2[i];
                    } else {
                        cromosome1prime[i] = cromosome2[i];
                        cromosome2prime[i] = cromosome1[i];
                    }
                }
                int eval1 = Evaluation(lots, cromosome1prime, weight), eval2 = Evaluation(lots, cromosome2prime, weight);

                //mutuelle
                for (int i = 0; i < pop; i++) {
                    int j = (int) (Math.round(Math.random()) * (taille - 1));
                    population[i][j] = (int) (Math.round(Math.random()));
                }
                for (int i = 0; i < pop; i++) {
                    eval[i] = Evaluation(lots, population[i], weight);
                    total += eval[i];
                }


                int min = 0, minEval = eval[0];
                for (int i = 1; i < pop; i++) {
                    if (minEval > eval[i]) {
                        minEval = eval[i];
                        min = i;
                    }
                }
                if (eval1 > minEval) {
                    population[min] = cromosome1prime;
                    eval[min] = eval1;
                }

                min = 0;
                minEval = eval[0];
                for (int i = 1; i < pop; i++) {
                    if (minEval > eval[i]) {
                        minEval = eval[i];
                        min = i;
                    }
                }
                if (eval2 > minEval) {
                    population[min] = cromosome2prime;
                    eval[min] = eval2;
                }


                //solution optimal
                int max = 0, maxEval = eval[0];
                for (int i = 1; i < pop; i++) {
                    if (max < eval[i]) {
                        max = i;
                        maxEval = eval[i];
                    }
                }
                int optLoc = maxEval;
                if (optLoc > opt) {
                    opt = optLoc;
                    for (int i = 0; i < taille; i++) solObjects[i] = population[max][i];
                }
            }

            Date d2 = new Date();
            show(String.valueOf((d2.getTime() - d1.getTime())));
            System.out.println(opt + "\n");
            sol.setText(" " + opt);
            objs.setText("");
            for (int i = 0; i < solObjects.length; i++)
                objs.setText(objs.getText() + lots[i].getPoid() + " " + lots[i].getGain() + " " + solObjects[i] + " \n");
        }
    }


    void show(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time Calculating");
        alert.setHeaderText(text + " Milisecondes");
        alert.showAndWait();
    }


    private int Evaluation(object[] lots, int[] individu, int max) {
        int eval = 0;
        int weight = 0;
        for (int i = 0; i < lots.length; i++) {
            eval += lots[i].getGain() * individu[i];
            weight += lots[i].getPoid() * individu[i];
        }
        if (weight > max) eval = 0;
        return eval;
    }


    //Recuit Simulé
    @FXML
    private void Recuit(ActionEvent event, float T, float eps, float k, int iterations) {
        if (!data.isEmpty() && weightMax.getText().length() > 0) {
            opt = 0;
            Date d1 = new Date();
            object[] lots = new object[data.size()];
            for (int i = 0; i < data.size(); i++) lots[i] = data.get(i);
            int weight = Integer.parseInt(weightMax.getText());
            int taille = lots.length;
            for (int i = 0; i < lots.length; i++) lots[i].setNbr(weight / lots[i].getPoid());
            solObjects = new int[lots.length];
            Arrays.fill(solObjects, 0);

            //Recuit Simulé
            opt = 0;
            int reste = weight;
            for (int i = 0; i < taille; i++) {
                solObjects[i] = (int) ((Math.round(Math.random() * lots[i].getNbr())));
                if (reste > solObjects[i] * lots[i].getPoid()) {
                    reste -= solObjects[i] * lots[i].getPoid();
                    opt += solObjects[i] * lots[i].getGain();
                } else {
                    solObjects[i] = 0;
                }
            }
            int[] solGenerator = new int[taille];
            for (int j = 0; j < taille; j++) solGenerator[j] = solObjects[j];
            for (int i = 0; i < iterations && T > eps; i++) {
                //generate solution
                int position = 0;
                int[] solLocal = new int[taille];
                for (int j = 0; j < taille; j++) solLocal[j] = solGenerator[j];
                for (int j = 0; j < taille; j++) {
                    if (solLocal[j] > 0) if ((int) (Math.round(Math.random())) == 1) position = j;
                }
                int randomize = (int) (Math.round(Math.random() * solLocal[position]));
                solLocal[position] -= randomize;
                randomize = (int) (Math.round(Math.random() * (taille - 1)));
                int resteLoc = weight;
                for (int j = 0; j < taille; j++) resteLoc -= solLocal[j] * lots[j].getPoid();
                solLocal[randomize] += resteLoc / lots[randomize].getPoid();

                //test solution
                Boolean accept = false;
                int eval = Evaluation(lots, solLocal, weight);
                int delta = eval - opt;
                if (delta > 0) {
                    accept = true;
                } else {
                    accept = Math.random() <= Math.exp(delta / T);
                }

                if (accept) {
                    for (int j = 0; j < taille; j++) solGenerator[j] = solLocal[j];
                    int check = Evaluation(lots, solGenerator, weight);
                    if (check > opt) {
                        opt = check;
                        for (int j = 0; j < taille; j++) solObjects[j] = solGenerator[j];
                    }
                }
                T *= k;
            }
            Date d2 = new Date();
            show(String.valueOf((d2.getTime() - d1.getTime())));
            System.out.println(opt + "\n");
            sol.setText(" " + opt);
            objs.setText("");
            for (int i = 0; i < solObjects.length; i++) {
                System.out.println(lots[i].getPoid() + " " + lots[i].getGain() + " " + solObjects[i]);
                objs.setText(objs.getText() + lots[i].getPoid() + " " + lots[i].getGain() + " " + solObjects[i] + " \n");
            }
        }
    }


    @FXML
    public void onImport(ActionEvent event) {
        System.out.println("clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(new Stage());
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        try {
            String line = buf.readLine();
            object[] list = new object[Integer.parseInt(line)];
            weightMax.setText(buf.readLine().trim());
            for (int i = 0; i < list.length; i++) {
                String[] separator = new String[3];
                separator[0] = " ";
                separator[1] = "  ";
                separator[2] = "   ";
                String[] objct = buf.readLine().split("\\s+");
                list[i] = new object(0, Integer.parseInt(objct[0].trim()), Integer.parseInt(objct[1].trim()));
            }
            data.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
