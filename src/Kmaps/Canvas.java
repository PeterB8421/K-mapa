/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Kmaps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Petr Balok
 */
public class Canvas extends JPanel implements ActionListener, MouseListener {

    private int valueCount; //Počet hodnot pro tabulku
    private ArrayList<ArrayList<Indicator>> indicators = new ArrayList<>(); //Matice indikátorů
    private Timer t;
    private mainWindow window;
    private final int startPos = 30;
    private int x = startPos;
    private int y = startPos;
    private ArrayList<Changeable> outputs = new ArrayList<>();//Pole interaktivních výstupů při růzé kombinaci vstupů
    private JLabel outputLabel;//Výstupní textové pole pro výpis rovnice
//Výstupní textové pole pro výpis rovnice
    private JLabel optimizedEquationOutput;//Výstupní textové pole pro výpis upravené rovnice
    private ArrayList<ArrayList<KMapIndicator>> kMapIndicators;
    private ArrayList<KMapIndicator> trueKMapInds = new ArrayList<>();

    public Canvas(mainWindow window, int values, JLabel outputLabel, JLabel optimizedEq) {
        this.kMapIndicators = new ArrayList<>();
        this.window = window;
        this.valueCount = values;
        this.outputLabel = outputLabel;
        this.optimizedEquationOutput = optimizedEq;
        init(this.valueCount);
    }

    private void init(int values) {
        this.setSize(new Dimension(800, 500));
        this.setBackground(Color.WHITE);
        this.setFocusable(true);
        this.valueCount = values;
        t = new Timer(10, this);
        t.start();
        for (int i = 0; i < this.valueCount; i++) {
            indicators.add(new ArrayList<>());
        }
        for (int i = 0; i < this.valueCount; i++) {
            kMapIndicators.add(new ArrayList<>());
        }
        addMouseListener(this);
        initTableValues();
        initKMapTableValues();
    }

    private void initKMapTableValues() {
        ArrayList<Boolean> vars = new ArrayList<>();
        for (int i = 0; i < (this.valueCount == 3 ? 2 : this.valueCount); i++) { //i = číslo řádku
            if (this.valueCount == 3 && i == 2)//Vykreslení mapy pro 3 proměnné je menší vyjímka
            {
                continue;
            }
            kMapIndicators.add(new ArrayList<>());
            for (int k = 0; k < (this.valueCount == 3 ? 4 : this.valueCount); k++) { //k = číslo sloupce
                switch (this.valueCount) {
                    case 2://V případě 2 proměnných
                        for (int j = 0; j < 4; j++) {//Cyklus pro určení všech proměnných a jejich nastavení
                            if (j < this.valueCount) {
                                switch (j) {
                                    case 0: //a
                                        vars.add(i != 0);
                                        break;
                                    case 1://b
                                        vars.add(k != 0);
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                vars.add(false);//c, d
                            }
                        }
                        break;
                    case 3://V případě 3 proměnných
                        for (int j = 0; j < 4; j++) {
                            if (j < this.valueCount) {
                                switch (j) {
                                    case 0://a
                                        vars.add(i != 0);
                                        break;
                                    case 1://b
                                        vars.add(k == 1 || k == 2);
                                        break;
                                    case 2://c
                                        vars.add(k == 2 || k == 3);
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                vars.add(false);//d
                            }
                        }
                        break;
                    case 4://V případě 4 proměnných
                        for (int j = 0; j < 4; j++) {
                            switch (j) {
                                case 0://a
                                    vars.add(i == 2 || i == 3);
                                    break;
                                case 1://b
                                    vars.add(k == 1 || k == 2);
                                    break;
                                case 2://c
                                    vars.add(i == 1 || i == 2);
                                    break;
                                case 3://d
                                    vars.add(k == 2 || k == 3);
                                    break;
                            }
                        }
                }
                kMapIndicators.get(i).add(new KMapIndicator(new Point(indicators.get(i).get(((k == 3 && this.valueCount == 3) ? 2 : k)).getPosition().x + 500
                        + ((k == 3 && this.valueCount == 3) ? indicators.get(i).get(((k == 3 && this.valueCount == 3) ? 2 : k)).getSIZE() : 0),
                        indicators.get(i).get(((k == 3 && this.valueCount == 3) ? 2 : k)).getPosition().y),
                        20, false, new ArrayList<>(vars)));
                vars.clear();
            }
        }
    }

    private void initTableValues() {//Metoda pro vytvoření matice indikátorů, které představují tabulku pravdivostních hodnot
        this.x = startPos;//Nastavení startovací pozice pro indikátory
        this.y = startPos;
        ArrayList<Integer> tBefore = new ArrayList<>(); //trues before - počet prvků true před prvkem
        ArrayList<Integer> fBefore = new ArrayList<>();//falses before - počet prvků false před prvkem
        for (int j = 0; j < 3; j++) {//Inicializace pole tBefore a fBefore
            tBefore.add(0);
            fBefore.add(0);
        }
        /* Proměnné jsou ve sloupcích a|b|c|d||S (S je spotřebič, jestli je při dané kombinaci zapnutý nebo vypnutý) */
        //Algoritmus pro určení hodnoty daného indikátoru
        for (int i = 0; i < Math.pow(2, valueCount); i++) { //i je indikátor řádku
            indicators.add(new ArrayList<>());//přidání řádku do matice indikátorů
            for (int k = 0; k < valueCount; k++) { //k je indikátor sloupce
                boolean val = true;//Inicializace proměnné val, která představuje hodnotu prvku (true/false), je zde inicializována, aby nebyl nullPonterException
                switch (k) {
                    case 0:
                        val = i % 2 == 0;
                        break;
                    case 1:
                        if (tBefore.get(0) == 2 && fBefore.get(0) == 0) {
                            val = false;
                            tBefore.set(0, 0);
                            fBefore.set(0, fBefore.get(0) + 1);
                        } else if (tBefore.get(0) == 0 && fBefore.get(0) == 0) {
                            val = true;
                            tBefore.set(0, tBefore.get(0) + 1);
                        } else if (tBefore.get(0) == 1 && fBefore.get(0) == 0) {
                            val = true;
                            tBefore.set(0, tBefore.get(0) + 1);
                            fBefore.set(0, 0);
                        } else if (tBefore.get(0) == 0 && fBefore.get(0) == 1) {
                            val = false;
                            fBefore.set(0, fBefore.get(0) + 1);
                            tBefore.set(0, 0);
                        } else if (tBefore.get(0) == 0 && fBefore.get(0) == 2) {
                            val = true;
                            tBefore.set(0, tBefore.get(0) + 1);
                            fBefore.set(0, 0);
                        }
                        break;
                    case 2:
                        if (tBefore.get(1) == 4 && fBefore.get(1) == 0) {
                            val = false;
                            tBefore.set(1, 0);
                            fBefore.set(1, fBefore.get(1) + 1);
                        } else if (tBefore.get(1) == 0 && fBefore.get(1) == 0) {
                            val = true;
                            tBefore.set(1, tBefore.get(1) + 1);
                        } else if (tBefore.get(1) > 0 && tBefore.get(1) <= 3 && fBefore.get(1) == 0) {
                            val = true;
                            tBefore.set(1, tBefore.get(1) + 1);
                            fBefore.set(1, 0);
                        } else if (tBefore.get(1) == 0 && fBefore.get(1) > 0 && fBefore.get(1) <= 3) {
                            val = false;
                            fBefore.set(1, fBefore.get(1) + 1);
                            tBefore.set(1, 0);
                        } else if (tBefore.get(1) == 0 && fBefore.get(1) == 4) {
                            val = true;
                            tBefore.set(1, tBefore.get(1) + 1);
                            fBefore.set(1, 0);
                        }
                        break;
                    case 3:
                        if (tBefore.get(2) == 8 && fBefore.get(2) == 0) {
                            val = false;
                            tBefore.set(2, 0);
                            fBefore.set(2, fBefore.get(2) + 1);
                        } else if (tBefore.get(2) > 0 && tBefore.get(2) < 8 && fBefore.get(2) == 0) {
                            val = true;
                            tBefore.set(2, tBefore.get(2) + 1);
                            fBefore.set(2, 0);
                        } else if (tBefore.get(2) == 0 && fBefore.get(2) > 0 && fBefore.get(2) < 8) {
                            val = false;
                            tBefore.set(2, 0);
                            fBefore.set(2, fBefore.get(2) + 1);
                        } else if (tBefore.get(2) == 0 && fBefore.get(2) == 0) {
                            val = true;
                            tBefore.set(2, tBefore.get(2) + 1);
                            fBefore.set(2, 0);
                        }
                        break;
                }
                indicators.get(i).add(new Indicator(new Point(this.x, this.y), val));//Přidání indikátoru s hodnotou a pozicí do matice
                this.x += indicators.get(i).get(k).getSIZE();//Přiřazení x pro další indikátor v matici
            }
            outputs.add(new Changeable(20, false, new Point(this.x + indicators.get(i).get(0).getSIZE(), this.y)));
            this.y += indicators.get(i).get(0).getSIZE();//Přiřazení x pro další indikátor v matici
            this.x = startPos;//Nastavení x na začátek řádku
        }
    }

    public void render(Graphics gr) { // Funkce pro vyrenderování Canvasu
        /* vytvoření pomocných proměnných pro objekty */
        Graphics2D g2d = (Graphics2D) gr;
        Area area;
        BasicStroke stroke;
        //Cyklus pro vykreslení matice indikátorů
        for (int i = 0; i < indicators.size(); i++) {
            for (int k = 0; k < indicators.get(i).size(); k++) {
                g2d.setColor(indicators.get(i).get(k).getBackgroundColor());
                area = new Area(new Rectangle2D.Double(indicators.get(i).get(k).getPosition().x, indicators.get(i).get(k).getPosition().y,
                        indicators.get(i).get(k).getSIZE(), indicators.get(i).get(k).getSIZE()));//Vytvoření objektu area s čtvercem pro vykreslení
                g2d.fill(area);//Vykreslení pozadí
                stroke = new BasicStroke();//Vytvoření objektu pro kreslení obrysu
                g2d.setStroke(stroke);
                g2d.setColor(indicators.get(i).get(k).getStrokeColor());
                g2d.draw(area);//Vykreslení obrysu
                g2d.setColor(indicators.get(i).get(k).getValue() ? Color.BLACK : Color.WHITE);
                g2d.drawString(indicators.get(i).get(k).getValue() ? "1" : "0", indicators.get(i).get(k).getPosition().x + indicators.get(i).get(k).getSIZE() / 2,
                        2 + indicators.get(i).get(k).getPosition().y + indicators.get(i).get(k).getSIZE() / 2);
                /*Vykreslení textu, 1 v případě, že je prvek true, 0 pokud je false. */
            }
        }
        for (int i = 0; i < valueCount; i++) {//Cyklus pro vykreslení názvů proměnných ve sloupcích
            switch (i) {
                case 0:
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("a", indicators.get(0).get(i).getPosition().x + 5,
                            indicators.get(0).get(i).getPosition().y - indicators.get(0).get(i).getSIZE() / 2);
                    break;
                case 1:
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("b", indicators.get(0).get(i).getPosition().x + 5,
                            indicators.get(0).get(i).getPosition().y - indicators.get(0).get(i).getSIZE() / 2);
                    break;
                case 2:
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("c", indicators.get(0).get(i).getPosition().x + 5,
                            indicators.get(0).get(i).getPosition().y - indicators.get(0).get(i).getSIZE() / 2);
                    break;
                case 3:
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("d", indicators.get(0).get(i).getPosition().x + 5,
                            indicators.get(0).get(i).getPosition().y - indicators.get(0).get(i).getSIZE() / 2);
                    break;
            }
        }

        for (Changeable output : outputs) {
            g2d.setColor(output.getBgColor());
            area = new Area(new Rectangle2D.Double(output.getPosition().x, output.getPosition().y, output.getSIZE(), output.getSIZE()));
            g2d.fill(area);
            g2d.setColor(output.getStrokeColor());
            g2d.draw(area);
            g2d.drawString(output.getValue() ? "1" : "0", output.getPosition().x + output.getSIZE() / 2, output.getPosition().y + output.getSIZE() / 2);
        }
        stroke = new BasicStroke(2f);
        g2d.setStroke(stroke);
        g2d.drawString("s", outputs.get(0).getPosition().x + outputs.get(0).getSIZE() / 2, outputs.get(0).getPosition().y - outputs.get(0).getSIZE() / 2);
        for (int i = 0; i < kMapIndicators.size(); i++) {
            for (int k = 0; k < kMapIndicators.get(i).size(); k++) {
                area = new Area(new Rectangle2D.Double(kMapIndicators.get(i).get(k).getPosition().x, kMapIndicators.get(i).get(k).getPosition().y,
                        kMapIndicators.get(i).get(k).getSize(), kMapIndicators.get(i).get(k).getSize()));
                g2d.setColor(kMapIndicators.get(i).get(k).getBackgroundColor());
                g2d.fill(area);
                g2d.setColor(kMapIndicators.get(i).get(k).getStrokeColor());
                g2d.draw(area);
                g2d.setColor(kMapIndicators.get(i).get(k).getValue() ? new Color(0, 230, 230) : Color.RED);
                g2d.drawString(kMapIndicators.get(i).get(k).getValue() ? "1" : "0", kMapIndicators.get(i).get(k).getPosition().x + kMapIndicators.get(i).get(k).getSize() / 2,
                        kMapIndicators.get(i).get(k).getPosition().y + kMapIndicators.get(i).get(k).getSize() / 2);
                switch (this.valueCount) {
                    case 2:
                        g2d.setColor(Color.BLACK);
                        g2d.drawString("a", kMapIndicators.get(1).get(0).getPosition().x - (kMapIndicators.get(1).get(0).getSize() - 10),
                                kMapIndicators.get(1).get(0).getPosition().y + kMapIndicators.get(1).get(0).getSize());
                        g2d.drawString("b", kMapIndicators.get(1).get(1).getPosition().x + kMapIndicators.get(1).get(1).getSize() / 2,
                                kMapIndicators.get(1).get(1).getPosition().y + kMapIndicators.get(1).get(1).getSize() + 10);
                        break;
                    case 3:
                        g2d.setColor(Color.BLACK);
                        g2d.drawString("a", kMapIndicators.get(1).get(0).getPosition().x - kMapIndicators.get(1).get(0).getSize(), kMapIndicators.get(1).get(0).getPosition().y
                                + kMapIndicators.get(1).get(0).getSize());
                        g2d.drawString("b", kMapIndicators.get(0).get(1).getPosition().x + 5, kMapIndicators.get(0).get(1).getPosition().y - kMapIndicators.get(0).get(1).getSize() / 2);
                        g2d.drawString("b", kMapIndicators.get(0).get(2).getPosition().x + 5, kMapIndicators.get(0).get(2).getPosition().y - kMapIndicators.get(0).get(2).getSize() / 2);
                        g2d.drawString("c", kMapIndicators.get(1).get(2).getPosition().x + 5, kMapIndicators.get(1).get(2).getPosition().y + kMapIndicators.get(1).get(2).getSize() + 10);
                        g2d.drawString("c", kMapIndicators.get(1).get(3).getPosition().x + 5, kMapIndicators.get(1).get(3).getPosition().y + kMapIndicators.get(1).get(3).getSize() + 10);
                        break;
                    case 4:
                        g2d.setColor(Color.BLACK);
                        g2d.drawString("a", kMapIndicators.get(2).get(0).getPosition().x - kMapIndicators.get(2).get(0).getSize(), kMapIndicators.get(2).get(0).getPosition().y
                                + kMapIndicators.get(2).get(0).getSize());
                        g2d.drawString("a", kMapIndicators.get(3).get(0).getPosition().x - kMapIndicators.get(3).get(0).getSize(), kMapIndicators.get(3).get(0).getPosition().y
                                + kMapIndicators.get(3).get(0).getSize());
                        g2d.drawString("b", kMapIndicators.get(3).get(1).getPosition().x + 5, kMapIndicators.get(3).get(1).getPosition().y + kMapIndicators.get(3).get(1).getSize() + 10);
                        g2d.drawString("b", kMapIndicators.get(3).get(2).getPosition().x + 5, kMapIndicators.get(3).get(2).getPosition().y + kMapIndicators.get(3).get(2).getSize() + 10);
                        g2d.drawString("c", kMapIndicators.get(1).get(3).getPosition().x + kMapIndicators.get(1).get(3).getSize() + 5, kMapIndicators.get(1).get(3).getPosition().y + 10);
                        g2d.drawString("c", kMapIndicators.get(2).get(3).getPosition().x + kMapIndicators.get(2).get(3).getSize() + 5, kMapIndicators.get(2).get(3).getPosition().y + 10);
                        g2d.drawString("d", kMapIndicators.get(0).get(2).getPosition().x + 5, kMapIndicators.get(0).get(2).getPosition().y - kMapIndicators.get(0).get(2).getSize() / 2);
                        g2d.drawString("d", kMapIndicators.get(0).get(3).getPosition().x + 5, kMapIndicators.get(0).get(3).getPosition().y - kMapIndicators.get(0).get(3).getSize() / 2);
                    default:
                        break;
                }
            }
        }
    }

    private void changeEquationString() {
        String equation = "S=";
        for (int i = 0; i < outputs.size(); i++) {
            if (outputs.get(i).getValue()) {
                if (i != 0) {
                    equation += "+";
                }
                for (int k = 0; k < indicators.get(i).size(); k++) {
                    if (k != 0) {
                        equation += "*";
                    }
                    equation += indicators.get(i).get(k).getValue() ? "" : "!";
                    switch (k) {
                        case 0:
                            equation += "a";
                            break;
                        case 1:
                            equation += "b";
                            break;
                        case 2:
                            equation += "c";
                            break;
                        case 3:
                            equation += "d";
                            break;
                    }
                }
            }
        }
        outputLabel.setText(equation);
    }

    private void optimizeEquationString() {
        int pluses = 0;
        String equation = this.outputLabel.getText();
        String result = "S = ";
        for (int i = 0; i < equation.length(); i++) {
            if (equation.charAt(i) == '+') {
                pluses++;
            }
        }
        List<String> list = Arrays.asList(equation.split("\\+|\\*|\\="));
        ArrayList<String> optimize = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
           /*if(list.get(i).equals(""))
               list.remove(i);
           if(list.get(i).equals("+"))
               list.remove(i);*/
        }
        int lastK = 1;
        for (int i = 0; i < pluses; i++) {
            for (int k = 1; k <= (((list.size()-1)-lastK == this.valueCount)?this.valueCount:this.valueCount*2); k++) {
                /*if(pluses % 2 == 0 && k > this.valueCount)
                    continue;*/
                optimize.add(list.get(lastK));
                lastK++;

            }
            result += this.optimizePair(new ArrayList<>(optimize));
            if (pluses > 1) {
                result += " + ";
            }
        }
        if (pluses == 0) {
            result = this.outputLabel.getText();
        }
        this.optimizedEquationOutput.setText(result);
    }

    private String optimizePair(ArrayList<String> list) {
        String result = "";
        //proměnné a-d
        boolean a = false;
        boolean b = false;
        boolean c = false;
        boolean d = false;
        //jestli byli v cyklu nastaveny
        boolean isA = false;
        boolean isB = false;
        boolean isC = false;
        boolean isD = false;
        //jestli mám proměnné vypisovat
        boolean printA = false;
        boolean printB = false;
        boolean printC = false;
        boolean printD = false;
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i)) {
                case "a":
                    if (!isA) {
                        a = true;
                        isA = true;
                        printA = true;
                    } else {
                        if (!a && printA) {
                            printA = false;
                        } else {
                            printA = true;
                            a = true;
                        }
                    }
                    break;
                case "!a":
                    if (!isA) {
                        a = false;
                        isA = true;
                        printA = true;
                    } else {
                        if (a && printA) {
                            printA = false;
                        } else {
                            printA = true;
                            a = false;
                        }
                    }
                    break;
                case "b":
                    if (!isB) {
                        b = true;
                        isB = true;
                        printB = true;
                    } else {
                        if (!b && printB) {
                            printB = false;
                        } else {
                            printB = true;
                            b = true;
                        }
                    }
                    break;
                case "!b":
                    if (!isB) {
                        b = false;
                        isB = true;
                        printB = true;
                    } else {
                        if (b && printB) {
                            printB = false;
                        } else {
                            printB = true;
                            b = false;
                        }
                    }
                    break;
                case "c":
                    if (!isC) {
                        c = true;
                        isC = true;
                        printC = true;
                    } else {
                        if (!c && printC) {
                            printC = false;
                        } else {
                            printC = true;
                            c = true;
                        }
                    }
                    break;
                case "!c":
                    if (!isC) {
                        c = false;
                        isC = true;
                        printC = true;
                    } else {
                        if (c && printC) {
                            printC = false;
                        } else {
                            printC = true;
                            c = false;
                        }
                    }
                    break;
                case "d":
                    if (!isD) {
                        isD = true;
                        printD = true;
                        d = true;
                    } else {
                        if (!d && printD) {
                            printD = false;
                        } else {
                            d = true;
                            printD = true;
                        }
                    }
                    break;
                case "!d":
                    if (!isD) {
                        printD = true;
                        d = false;
                        isD = true;
                    } else {
                        if (d && printD) {
                            printD = false;
                        } else {
                            d = false;
                            printD = true;
                        }
                    }
            }
        }
        result += (printA ? (a ? "a " : "!a ") : "");
        result += (printA && printB ? "* " : "");
        result += (printB ? (b ? "b " : "!b ") : "");
        result += (printC && printB ? "* " : "");
        result += (printC ? (c ? "c " : "!c ") : "");
        result += (printD && printC ? "* " : "");
        result += (printD ? (d ? "d " : "!d ") : "");
        return result;
    }

    public int getValueCount() {
        return valueCount;
    }

    public void setValueCount(int valueCount) {
        this.valueCount = valueCount;
        indicators.clear();
        outputs.clear();
        kMapIndicators.clear();
        this.initTableValues();
        this.initKMapTableValues();
        this.changeEquationString();
        this.optimizeEquationString();
        this.repaint();
    }

    private void changeKMapValues() {
        ArrayList<Boolean> variables = new ArrayList<>();
        for (int j = 0; j < indicators.size(); j++) {//j = řádek indikátorů
            if (j >= Math.pow(2, this.valueCount)) {
                continue;
            }
            for (int l = 0; l < indicators.get(j).size(); l++) {//l = sloupec indikátorů
                variables.add(indicators.get(j).get(l).getValue());
            }
            if (this.valueCount != 4) {
                for (int l = variables.size(); l < 4; l++) {
                    variables.add(false);
                }
            }
            for (int i = 0; i < kMapIndicators.size(); i++) {//i = řádek K-mapy
                for (int k = 0; k < kMapIndicators.get(i).size(); k++) {//k = sloupec K-mapy
                    if (variables.equals(kMapIndicators.get(i).get(k).getVariables())) {
                        kMapIndicators.get(i).get(k).setValue(outputs.get(j).getValue());
                    }
                }
            }
            variables.clear();
        }

    }

    public ArrayList<ArrayList<Indicator>> getIndicators() {
        return indicators;
    }

    public ArrayList<Changeable> getOutputs() {
        return outputs;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.render(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Myš klikla");
        for (Changeable o : outputs) {
            Area output = new Area(new Rectangle2D.Double(o.getPosition().x, o.getPosition().y, o.getSIZE(), o.getSIZE()));
            if (output.contains(e.getPoint())) {
                o.clicked();
                this.changeEquationString();
                this.optimizeEquationString();
            }
            this.changeKMapValues();//Změna potřebného prvku K-mapy na true
            this.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("Myš stisknuta");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("Tlačítko myši puštěno");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("Myš vstoupila do objektu");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("Myš vystoupila z objektu");
    }

}
