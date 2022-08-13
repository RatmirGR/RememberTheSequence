package Study.Core.GPU.PAWT;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

class MyFrameClassT extends Panel implements Runnable, ActionListener{
    private Color c2, c3, c4;
    private int count = 0, countS = 0, countF = 0;
    private int n;
    private ArrayList<Button> list1; // список для всех кнопок
    private ArrayList<Button> list2; // список для "активных" кнопок
    private ArrayList<Button> list3; // список для оставшихся кнопок
    MyFrameClassT(int n){
        this.n = n;
        Color c1 = new Color(146, 146, 146); // цвет фона
        c2 = new Color(2, 168, 190); // цвет кнопок
        c3 = new Color(0, 204, 102); // цвет успеха
        c4 = new Color(203, 72, 19); // цвет ошибки
        /* использование диспетчера сеточной компоновки*/
        setLayout(new GridLayout(n, n, 1, 1)); // установка сеточной компоновки
        setBackground(c1); // установка цвета фона
        setInit(); // первичная установка
        new Thread(this).start();// запуск дочернего потока
        /* использование внутреннего класса для обработки события закрытия окна*/
    }
    private void setInit(){
        //int countN = 1;
        Button b;
        //int k;
        list1 = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //k = i * n + j;
                //b = new Button(""+(countN++)); // создание кнопки
                b = new Button(); // создание кнопки
                b.setBackground(Color.lightGray); // установка первичного цвета кнопки
                //b.setFont(new Font("Monospace", Font.BOLD, 20));
                list1.add(b); // добавление кнопки в список1
                list1.get(count).setEnabled(false); // кнопки не активны
                add(list1.get(count)); // добавление кнопки в фрейм
                list1.get(count).addActionListener(this); // регистрация слушателя для текущей кнопки
                count++;
            }
        }
        /* можно 0 ошибок*/
        if(n > 0 && n <= 3){
            count = countS = n;
            countF = 1;
        }
        /* можно 1 ошибку*/
        if(n > 3 && n <= 6){
            count = n+1;
            countS = n;
            countF = 2;
        }
        /* можно 2 ошибки*/
        if(n > 6 && n <= 9){
            count = n+2;
            countS = n;
            countF = 3;
        }
        /* можно 3 ошибки*/
        if(n > 9 && n <= 15){
            count = n+3;
            countS = n;
            countF = 4;
        }
    }
    public void run(){
        setBag();
    }
    private void setBag(){
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        try {
            //Collections.shuffle(list1); // тут выходит исключение, написать на рандоме
            Random random = new Random();
            for (int i = 0; i < list1.size(); i++) {
                int idx1 = random.nextInt(list1.size());
                Button b1 = list1.get(idx1);
                int idx2 = random.nextInt(list1.size());
                list1.set(idx1, list1.get(idx2));
                list1.set(idx2, b1);
            }
            for (int a = 0; a < n; a++) {
                Thread.sleep(1000);
                list1.get(a).setBackground(c2);
                list2.add(list1.get(a));
                list3.add(list1.get(a)); // подсчет остатков в случае ошибки
                Thread.sleep(500);
                list1.get(a).setBackground(Color.lightGray);
            }
        }catch(InterruptedException e){
            e.getMessage();
        }
        for(Button u : list1) u.setEnabled(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean flag = true;
        for (Button q : list2){
            if (e.getSource().equals(q)){
                q.setBackground(c2);
                q.setEnabled(false);
                if(!list3.isEmpty()) list3.remove(q); // удаляем угаданную кнопку из остатков
                flag = false;
                countS--;
                /* в случае успеха*/
                if(countS == 0){
                    for (Button bn : list1){
                        bn.setEnabled(false);
                        setBackground(c3);
                    }
                }
                count--;
            }
        }
        if(flag) {
            Button t = (Button) e.getSource();
            t.setBackground(new Color(178, 178, 178));
            t.setEnabled(false);
            countF--;
            /* в случае ошибки*/
            if(countF == 0){
                for (Button bne : list3){
                    bne.setBackground(new Color(208, 143, 47));
                }
                for (Button bn : list1){
                    bn.setEnabled(false);
                    setBackground(c4);
                }
            }
            count--;
        }
        /* в случае окончания попыток*/
        if(count == 0 && countS != 0){
            for (Button bne : list3){
                bne.setBackground(new Color(208, 143, 47));
            }
            for (Button bn : list1){
                bn.setEnabled(false);
                setBackground(c4);
            }
        }
    }
    /* переопределение метода getInsets(), для того чтобы ввести значения вставок*/
    public Insets getInsets(){
        return new Insets(40, 20, 20, 20);
    }
}
class MyFrameClassTS extends Frame{
    private int count = 2;
    MyFrameClassTS(){
        setLayout(new BorderLayout());
        Button b1 = new Button("Заново");
        Button b2 = new Button("Следующая");
        Button b3 = new Button("Предыдущая");
        Label lab = new Label("Level: "+count);
        Panel pn = new Panel();
        FlowLayout fl = new FlowLayout();
        pn.add(lab, fl);
        pn.add(b1, fl);
        pn.add(b2, fl);
        pn.add(b3, fl);
        add(pn, BorderLayout.NORTH);
        CardLayout cl = new CardLayout();
        Panel p = new Panel();
        p.setLayout(cl);
        p.add(new MyFrameClassT(2), "Panel1");
        add(p, BorderLayout.CENTER);
        b1.addActionListener((ae) -> {
            lab.setText("Level: "+count);
            pn.add(lab, FlowLayout.LEFT);
            if(p.getComponents().length == 2) p.remove(1);
            p.add(new MyFrameClassT(count), "Panel"+count);
            cl.next(p);
        });
        b2.addActionListener((ae) -> {
            if(p.getComponents().length == 2) p.remove(1);
            if(count != 15) count++;
            lab.setText("Level: "+count);
            p.add(new MyFrameClassT(count), "Panel"+count);
            cl.next(p);
        });
        b3.addActionListener((ae) -> {
            if(p.getComponents().length == 2) p.remove(1);
            if(count != 2) count--;
            lab.setText("Level: "+count);
            p.add(new MyFrameClassT(count), "Panel"+count);
            cl.previous(p);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
class MyCnClassT{
    void myMeth(){
        System.out.println("Пример 2 - использование сеточной компоновки - игра на память");
        /* создание объекта типа MyFrameClass, являющимся окном (каркасом)*/
        MyFrameClassTS mfc = new MyFrameClassTS();
        /* объект типа Dimension, отвечает за размеры создаваемого объекта (каркаса)*/
        mfc.setSize(new Dimension(550, 400)); // установка размера
        mfc.setTitle("My program"); // установка названия
        mfc.setVisible(true); // установка видимости
    }
}
public class MyMemberGame{
    public static void main(String[] args) {
        new MyCnClassT().myMeth();
    }
}
