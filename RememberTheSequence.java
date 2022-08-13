package GPU.PAWT;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

class Game extends Panel implements Runnable, ActionListener{
    private final Color colorButton;
    private final Color colorSuccess;
    private final Color colorFailure;
    private int countTotalClicks = 0, countSuccessfulClicks = 0, countFailureClicks = 0;
    private final int countCells;
    private ArrayList<Button> listAllButton; // список для всех кнопок
    private ArrayList<Button> listActiveButton; // список для "активных" кнопок
    private ArrayList<Button> listOthersButton; // список для оставшихся кнопок
    Game(int countCells){
        this.countCells = countCells;
        colorButton = new Color(2, 168, 190); // цвет кнопок
        colorSuccess = new Color(0, 204, 102); // цвет успеха
        colorFailure = new Color(203, 72, 19); // цвет ошибки
        /* использование диспетчера сеточной компоновки*/
        setLayout(new GridLayout(countCells, countCells, 1, 1)); // установка сеточной компоновки
        setBackground(new Color(146, 146, 146)); // установка цвета фона
        setInit(); // первичная установка
        new Thread(this).start();// запуск дочернего потока
        /* использование внутреннего класса для обработки события закрытия окна*/
    }
    private void setInit(){
        Button b;
        listAllButton = new ArrayList<>();
        for(int i = 0; i < countCells; i++) {
            for (int j = 0; j < countCells; j++) {
                b = new Button(); // создание кнопки
                b.setBackground(Color.lightGray); // установка первичного цвета кнопки
                listAllButton.add(b); // добавление кнопки в listAllButton
                listAllButton.get(countTotalClicks).setEnabled(false); // кнопки не активны
                add(listAllButton.get(countTotalClicks)); // добавление кнопки в фрейм
                listAllButton.get(countTotalClicks).addActionListener(this); // регистрация слушателя для текущей кнопки
                countTotalClicks++;
            }
        }
        /* можно 0 ошибок*/
        if(countCells > 0 && countCells <= 3){
            countTotalClicks = countSuccessfulClicks = countCells;
            countFailureClicks = 1;
        }
        /* можно 1 ошибку*/
        if(countCells > 3 && countCells <= 6){
            countTotalClicks = countCells+1;
            countSuccessfulClicks = countCells;
            countFailureClicks = 2;
        }
        /* можно 2 ошибки*/
        if(countCells > 6 && countCells <= 9){
            countTotalClicks = countCells+2;
            countSuccessfulClicks = countCells;
            countFailureClicks = 3;
        }
        /* можно 3 ошибки*/
        if(countCells > 9 && countCells <= 15){
            countTotalClicks = countCells+3;
            countSuccessfulClicks = countCells;
            countFailureClicks = 4;
        }
    }
    public void run(){
        setBag();
    }
    private void setBag(){
        listActiveButton = new ArrayList<>();
        listOthersButton = new ArrayList<>();
        try {
            Random random = new Random();
            for (int i = 0; i < listAllButton.size(); i++) {
                int idx1 = random.nextInt(listAllButton.size());
                Button b1 = listAllButton.get(idx1);
                int idx2 = random.nextInt(listAllButton.size());
                listAllButton.set(idx1, listAllButton.get(idx2));
                listAllButton.set(idx2, b1);
            }
            for (int a = 0; a < countCells; a++) {
                Thread.sleep(1000);
                listAllButton.get(a).setBackground(colorButton);
                listActiveButton.add(listAllButton.get(a));
                listOthersButton.add(listAllButton.get(a)); // подсчет остатков в случае ошибки
                Thread.sleep(500);
                listAllButton.get(a).setBackground(Color.lightGray);
            }
        }catch(InterruptedException e){
            System.err.println(e.getMessage());
        }
        for(Button u : listAllButton) u.setEnabled(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean flag = true;
        for (Button q : listActiveButton){
            if (e.getSource().equals(q)){
                q.setBackground(colorButton);
                q.setEnabled(false);
                if(!listOthersButton.isEmpty()) listOthersButton.remove(q); // удаляем угаданную кнопку из остатков
                flag = false;
                countSuccessfulClicks--;
                /* в случае успеха*/
                if(countSuccessfulClicks == 0){
                    for (Button bn : listAllButton){
                        bn.setEnabled(false);
                        setBackground(colorSuccess);
                    }
                }
                countTotalClicks--;
            }
        }
        if(flag) {
            Button t = (Button) e.getSource();
            t.setBackground(new Color(178, 178, 178));
            t.setEnabled(false);
            countFailureClicks--;
            /* в случае ошибки*/
            if(countFailureClicks == 0){
                for (Button bne : listOthersButton){
                    bne.setBackground(new Color(208, 143, 47));
                }
                for (Button bn : listAllButton){
                    bn.setEnabled(false);
                    setBackground(colorFailure);
                }
            }
            countTotalClicks--;
        }
        /* в случае окончания попыток*/
        if(countTotalClicks == 0 && countSuccessfulClicks != 0){
            for (Button bne : listOthersButton){
                bne.setBackground(new Color(208, 143, 47));
            }
            for (Button bn : listAllButton){
                bn.setEnabled(false);
                setBackground(colorFailure);
            }
        }
    }
    /* переопределение метода getInsets(), для того чтобы ввести значения вставок*/
    public Insets getInsets(){
        return new Insets(40, 20, 20, 20);
    }
}

class GameFrame extends Frame{
    private int countCells = 2;
    GameFrame(){
        setLayout(new BorderLayout());
        Button buttonStart = new Button("Заново");
        Button buttonNext = new Button("Следующая");
        Button buttonPrev = new Button("Предыдущая");
        Label label = new Label("Level: "+countCells);
        Panel panel = new Panel();
        FlowLayout fl = new FlowLayout();
        panel.add(label, fl);
        panel.add(buttonStart, fl);
        panel.add(buttonNext, fl);
        panel.add(buttonPrev, fl);
        add(panel, BorderLayout.NORTH);
        CardLayout cardLayout = new CardLayout();
        Panel panel2 = new Panel();
        panel2.setLayout(cardLayout);
        panel2.add(new Game(2), "Panel1");
        add(panel2, BorderLayout.CENTER);
        buttonStart.addActionListener((ae) -> {
            label.setText("Level: "+countCells);
            panel.add(label, FlowLayout.LEFT);
            if(panel2.getComponents().length == 2) panel2.remove(1);
            panel2.add(new Game(countCells), "Panel"+countCells);
            cardLayout.next(panel2);
        });
        buttonNext.addActionListener((ae) -> {
            if(panel2.getComponents().length == 2) panel2.remove(1);
            if(countCells != 15) countCells++;
            label.setText("Level: "+countCells);
            panel2.add(new Game(countCells), "Panel"+countCells);
            cardLayout.next(panel2);
        });
        buttonPrev.addActionListener((ae) -> {
            if(panel2.getComponents().length == 2) panel2.remove(1);
            if(countCells != 2) countCells--;
            label.setText("Level: "+countCells);
            panel2.add(new Game(countCells), "Panel"+countCells);
            cardLayout.previous(panel2);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}

public class RememberTheSequence{
    public static void main(String[] args) {
        /* создание объекта типа MyFrameClass, являющимся окном (каркасом)*/
        GameFrame gameFrame = new GameFrame();
        /* объект типа Dimension, отвечает за размеры создаваемого объекта (каркаса)*/
        gameFrame.setSize(new Dimension(550, 400)); // установка размера
        gameFrame.setTitle("Remember The Sequence"); // установка названия
        gameFrame.setVisible(true); // установка видимости
    }
}
