/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cal;

import java.awt.Color;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
/**
 *
 * @author NguyenThanhSang
 */
public class mainFr extends javax.swing.JFrame {

    private String nullMessage="No history";
    
    private String displayString;
    private String btnLabel;
    
    
    public mainFr() {
        initComponents();
        setMainFrame();
        setTitle("Máy tính xịn xò con bò");
        Icon clrIcon = new ImageIcon("src\\icon\\clear.png");
        clrButton.setIcon(clrIcon);
        
        ImageIcon mainIcon = new ImageIcon("src\\icon\\calcu.jpg");
        setIconImage(mainIcon.getImage());
        
        Icon delIcon = new ImageIcon("src\\icon\\del.png");
        delButton.setIcon(delIcon);
        getContentPane().setBackground(new Color(51,51,51));
    }
    
   private void setMainFrame(){
       display.setText("0");
   }
  
   
   //*******************************************************************************************************//
   
   private int hasNums(String str,String val){
        int nums=0;
        while (str.indexOf(val)!=-1){
            nums+=1;
            str=str.substring(str.indexOf(val)+1);
        }
        return nums;
    }
   
   private String autoAdd(String displayString){
        String symbol=displayString.substring(displayString.length()-2,displayString.length()-1);
        switch (symbol){
            case "+":
            case "-":
                displayString+="0";
                break;
            case "*":
            default:
                displayString+="1";
        }
        return displayString;
    }
   
    //Compare symbol priority
    private boolean compareSymbols(String newSymbol,String existSymbol){
        //Only +, -, *,/
        //false indicates that the last element of the symbol stack needs he symbol to be pushed out if the priority of the former is smaller or equal.
        //true indicates that the former has higher priority, and the symbol directly enters the symbol stack.
        //Encounter brackets
        if(existSymbol.equals("(")) return true;
        //Same priority
        if(newSymbol.equals(existSymbol)) return false;
        else if((newSymbol.equals("*")||newSymbol.equals("/"))&&(existSymbol.equals("*")||existSymbol.equals("/"))) return false;
        else if((newSymbol.equals("+")||newSymbol.equals("-"))&&(existSymbol.equals("+")||existSymbol.equals("-"))) return false;
        //Different priorities
        else if((newSymbol.equals("-") ||newSymbol.equals("+")) &&(existSymbol.equals("*") || existSymbol.equals("/"))) return false;
        // Only the former * or / or the latter + or*
        return true;
    }
   
    private LinkedList<String> getNewExpre(String[] displayString){
        for(int i=0;i<displayString.length;i++){
            System.out.print(displayString[i]+" ");
        }
        System.out.println();
        LinkedList<String> symbols=new LinkedList<>();//Stack for symbols
        LinkedList<String> newExpre=new LinkedList<>();//Stack for new displayStrings
        for (int i=0;i<displayString.length;i++){
            String val=displayString[i];
            if(val.equals("")) continue;
//            System.out.println(symbols);
//            System.out.println(newExpre);
              System.out.println(val);
            switch (val){
                case "(":
                    symbols.add(val);break;
                case ")":
                    boolean isOk=true;
                    while(isOk){
                        String _symbol=symbols.pollLast();
                        if(_symbol.equals("(")) isOk=false;
                        else newExpre.add(_symbol);
                    };
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    if(symbols.size()==0){
                        symbols.add(val);
                    } else if(compareSymbols(val,symbols.get(symbols.size()-1))) symbols.add(val);
                    else{
                        while (symbols.size()>0 && !compareSymbols(val,symbols.get(symbols.size()-1))){
                            newExpre.add(symbols.pollLast());//Need to stack all, until the priority is smaller than their own.
                        }
                        symbols.add(val);
                    }
                    break;
                default:
                    newExpre.add(val);
            }
        }
        while(symbols.size()>0){
            newExpre.add(symbols.pollLast());
        }
        return newExpre;
    }
   
       //Calculation
    private void cal(String str){
        //Get a new displayString
        LinkedList<String> expre=getNewExpre(str.split(" "));
        for(int i=0;i<expre.size();i++){
            System.out.print(expre.get(i)+",");
        }
        System.out.println();
        for(var i=0;i<expre.size();i++){
            var val=expre.get(i);
            switch(val){
                case "-":
                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())-Double.valueOf(expre.get(i-1).toString())));
                    expre.remove(i-1);
                    expre.remove(i-1);
                    i-=2;
                    break;
                case "+":
                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())+Double.valueOf(expre.get(i-1).toString())));
                    expre.remove(i-1);
                    expre.remove(i-1);
                    i-=2;
                    break;
                case "*":
                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())*Double.valueOf(expre.get(i-1).toString())));
                    expre.remove(i-1);
                    expre.remove(i-1);
                    i-=2;
                    break;
                case "/":
                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())/Double.valueOf(expre.get(i-1).toString())));
                    expre.remove(i-1);
                    expre.remove(i-1);
                    i-=2;
                    break;
                default:
                    break;
            }
        }
        String result=expre.get(0);

        display.setText(result);//Zeroing
        //Add history
        String recordStr=record.getText();
        if(recordStr.equals(nullMessage)) recordStr="";
        recordStr+=str+"="+result+"\n\n";
        record.setText(recordStr);
    }

    
     public void Run() {
//                System.out.println("Đây là hàm RUN()");
                final String[] exceptMessages={"NaN","Infinity"};
                //Get the clicked button object
                String value= btnLabel;//Button value
                String displayString=display.getText();//Get the displayString of the display
                int len=displayString.length();//Because displayString length is often used, it is declared in advance here
                switch (value){
                    case "C":
                        display.setText("0");
                        break;
                    case "Back":
                        boolean flag=false;
                        for(int i=0;i<exceptMessages.length;i++){
                            if(!displayString.equals(exceptMessages[i])) continue;
                            flag=true;
                            break;
                        }
                        if(len==1) displayString="0";
                        else if(flag==true){
                            //When infinite, NaN and other data exist
                            displayString="0";
                        } else if(len>0 &&displayString.lastIndexOf(" ")!=len-1 ){
                            displayString=displayString.substring(0,len-1);
                        }else if(len>0 &&displayString.substring(len-4,len-3).matches("[0-9]+")) displayString=displayString.substring(0,len-3);
                        else if(len>0) displayString=displayString.substring(0,len-2);
                        display.setText(displayString);
                        break;
                    case "+":
                    case "-":
                    case "*":
                    case "/":
                        if(displayString.lastIndexOf(" ")==len-1 &&!displayString.substring(len-2,len-1).equals("(")
                                &&!displayString.substring(len-2,len-1).equals(")")){
                            displayString=displayString.substring(0,len-3);
                        }else if(len>1&& displayString.substring(len-2,len-1).equals("(")){
                            displayString+="0";
                        }
                        displayString+=" "+value+" ";
                        display.setText(displayString);
                        break;
                    case "(":
                    case ")":
                        if(value.equals(")") &&hasNums(displayString,"(")<=hasNums(displayString,")")) break;
                        else if(value.equals(")") &&displayString.lastIndexOf(" ")==len-1 &&displayString.lastIndexOf("(")!=len-2){
                            //If the shape is like 2 + (5 +, add a number automatically
                            displayString=autoAdd(displayString);
                            len+=1;
                        }
                        if(displayString.equals("0")) displayString="";
                        else if(value=="(" &&displayString.lastIndexOf(" ")!=len-1) displayString+=" *";//5 * (2 + 3) default to a * sign
                        if(displayString.lastIndexOf(" ")==len-1) displayString=displayString.substring(0,len-1);//Clear preceding spaces
                        if(len>1 &&value.equals("(") && displayString.lastIndexOf(")")==len-2){
                            //Form (a + b) × a d d one in the middle (c + D)*
                            displayString+=" *";
                            len+=2;
                        }
                        displayString+=" "+value+" ";
                        display.setText(displayString);
                        break;
                    case ".":
                        if(displayString.lastIndexOf(")")!=-1 && displayString.lastIndexOf(")")==len-2) displayString+="* 0"+value;//brackets
                        else if(displayString.lastIndexOf(" ")==len-1)displayString+="0"+value;
                        else if(displayString.lastIndexOf(".")!=-1 &&displayString.lastIndexOf(".")>displayString.lastIndexOf(" ")) break;//Enter two in a row.
                        else displayString+=value;
                        display.setText(displayString);
                        break;
                    case "=":
                        if(hasNums(displayString,"(")>hasNums(displayString,")")){
                            //gapNum brackets missing
                            displayString+=displayString.lastIndexOf(" ")==len-1?"+ 0":" + 0";
                            int gapNum=hasNums(displayString,"(")-hasNums(displayString,")");
                            for(int i=0;i<gapNum;i++){
                                displayString+=" ) ";
                            }
                        }
                        else if(displayString.lastIndexOf(" ")==len-1 && displayString.lastIndexOf(")")!=len-2){
                            //When the last one is an operator, add 0 or 1 to the default displayString
                            displayString=autoAdd(displayString);
                        }
                        cal(displayString);
                        break;
                    default:
                        //exceptMessages
                        if(displayString.substring(len-1).matches("[a-zA-Z]+")){
                            displayString="0";
                        }
                        //number
                        if(displayString.equals("0")) displayString="";
                        if(displayString.lastIndexOf(" ")==len-1&&displayString.lastIndexOf(")")==len-2) displayString+="* ";
                        displayString+=value;
                        display.setText(displayString);
                }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        record = new javax.swing.JTextArea();
        clrButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        clButton = new javax.swing.JButton();
        Button8 = new javax.swing.JButton();
        Button5 = new javax.swing.JButton();
        Button2 = new javax.swing.JButton();
        dotButton = new javax.swing.JButton();
        opButton = new javax.swing.JButton();
        Button7 = new javax.swing.JButton();
        Button4 = new javax.swing.JButton();
        Button1 = new javax.swing.JButton();
        Button0 = new javax.swing.JButton();
        Button9 = new javax.swing.JButton();
        Button6 = new javax.swing.JButton();
        Button3 = new javax.swing.JButton();
        eqlButton = new javax.swing.JButton();
        delButton = new javax.swing.JButton();
        divButton = new javax.swing.JButton();
        mulButton = new javax.swing.JButton();
        plusButton = new javax.swing.JButton();
        minButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        display = new javax.swing.JTextField();
        helpLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));

        record.setEditable(false);
        record.setBackground(new java.awt.Color(209, 206, 199));
        record.setColumns(20);
        record.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        record.setForeground(new java.awt.Color(0, 0, 0));
        record.setRows(5);
        record.setText("No history");
        record.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane2.setViewportView(record);

        clrButton.setBackground(new java.awt.Color(153, 153, 153));
        clrButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clrButtonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        clButton.setBackground(new java.awt.Color(153, 153, 153));
        clButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        clButton.setForeground(new java.awt.Color(0, 0, 0));
        clButton.setText(")");
        clButton.setBorderPainted(false);
        clButton.setPreferredSize(new java.awt.Dimension(72, 26));
        clButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clButtonActionPerformed(evt);
            }
        });

        Button8.setBackground(new java.awt.Color(153, 153, 153));
        Button8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button8.setForeground(new java.awt.Color(0, 0, 0));
        Button8.setText("8");
        Button8.setBorderPainted(false);
        Button8.setPreferredSize(new java.awt.Dimension(72, 26));
        Button8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button8ActionPerformed(evt);
            }
        });

        Button5.setBackground(new java.awt.Color(153, 153, 153));
        Button5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button5.setForeground(new java.awt.Color(0, 0, 0));
        Button5.setText("5");
        Button5.setBorderPainted(false);
        Button5.setPreferredSize(new java.awt.Dimension(72, 26));
        Button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button5ActionPerformed(evt);
            }
        });

        Button2.setBackground(new java.awt.Color(153, 153, 153));
        Button2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button2.setForeground(new java.awt.Color(0, 0, 0));
        Button2.setText("2");
        Button2.setBorderPainted(false);
        Button2.setPreferredSize(new java.awt.Dimension(72, 26));
        Button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button2ActionPerformed(evt);
            }
        });

        dotButton.setBackground(new java.awt.Color(153, 153, 153));
        dotButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        dotButton.setForeground(new java.awt.Color(0, 0, 0));
        dotButton.setText(".");
        dotButton.setBorderPainted(false);
        dotButton.setMaximumSize(new java.awt.Dimension(72, 26));
        dotButton.setMinimumSize(new java.awt.Dimension(72, 26));
        dotButton.setPreferredSize(new java.awt.Dimension(72, 26));
        dotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dotButtonActionPerformed(evt);
            }
        });

        opButton.setBackground(new java.awt.Color(153, 153, 153));
        opButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        opButton.setForeground(new java.awt.Color(0, 0, 0));
        opButton.setText("(");
        opButton.setBorderPainted(false);
        opButton.setPreferredSize(new java.awt.Dimension(72, 26));
        opButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opButtonActionPerformed(evt);
            }
        });

        Button7.setBackground(new java.awt.Color(153, 153, 153));
        Button7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button7.setForeground(new java.awt.Color(0, 0, 0));
        Button7.setText("7");
        Button7.setBorderPainted(false);
        Button7.setPreferredSize(new java.awt.Dimension(72, 26));
        Button7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button7ActionPerformed(evt);
            }
        });

        Button4.setBackground(new java.awt.Color(153, 153, 153));
        Button4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button4.setForeground(new java.awt.Color(0, 0, 0));
        Button4.setText("4");
        Button4.setBorderPainted(false);
        Button4.setPreferredSize(new java.awt.Dimension(72, 26));
        Button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button4ActionPerformed(evt);
            }
        });

        Button1.setBackground(new java.awt.Color(153, 153, 153));
        Button1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button1.setForeground(new java.awt.Color(0, 0, 0));
        Button1.setText("1");
        Button1.setBorderPainted(false);
        Button1.setPreferredSize(new java.awt.Dimension(72, 26));
        Button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button1ActionPerformed(evt);
            }
        });

        Button0.setBackground(new java.awt.Color(153, 153, 153));
        Button0.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button0.setForeground(new java.awt.Color(0, 0, 0));
        Button0.setText("0");
        Button0.setBorderPainted(false);
        Button0.setMaximumSize(new java.awt.Dimension(72, 26));
        Button0.setMinimumSize(new java.awt.Dimension(72, 26));
        Button0.setPreferredSize(new java.awt.Dimension(72, 26));
        Button0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button0ActionPerformed(evt);
            }
        });

        Button9.setBackground(new java.awt.Color(153, 153, 153));
        Button9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button9.setForeground(new java.awt.Color(0, 0, 0));
        Button9.setText("9");
        Button9.setBorderPainted(false);
        Button9.setPreferredSize(new java.awt.Dimension(72, 26));
        Button9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button9ActionPerformed(evt);
            }
        });

        Button6.setBackground(new java.awt.Color(153, 153, 153));
        Button6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button6.setForeground(new java.awt.Color(0, 0, 0));
        Button6.setText("6");
        Button6.setBorderPainted(false);
        Button6.setPreferredSize(new java.awt.Dimension(72, 26));
        Button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button6ActionPerformed(evt);
            }
        });

        Button3.setBackground(new java.awt.Color(153, 153, 153));
        Button3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button3.setForeground(new java.awt.Color(0, 0, 0));
        Button3.setText("3");
        Button3.setBorderPainted(false);
        Button3.setPreferredSize(new java.awt.Dimension(72, 26));
        Button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button3ActionPerformed(evt);
            }
        });

        eqlButton.setBackground(new java.awt.Color(153, 153, 153));
        eqlButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        eqlButton.setForeground(new java.awt.Color(0, 0, 0));
        eqlButton.setText("=");
        eqlButton.setBorderPainted(false);
        eqlButton.setPreferredSize(new java.awt.Dimension(72, 26));
        eqlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eqlButtonActionPerformed(evt);
            }
        });

        delButton.setBackground(new java.awt.Color(153, 153, 153));
        delButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        delButton.setForeground(new java.awt.Color(0, 0, 0));
        delButton.setBorderPainted(false);
        delButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delButtonActionPerformed(evt);
            }
        });

        divButton.setBackground(new java.awt.Color(153, 153, 153));
        divButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        divButton.setForeground(new java.awt.Color(0, 0, 0));
        divButton.setText("/");
        divButton.setBorderPainted(false);
        divButton.setPreferredSize(new java.awt.Dimension(72, 26));
        divButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divButtonActionPerformed(evt);
            }
        });

        mulButton.setBackground(new java.awt.Color(153, 153, 153));
        mulButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        mulButton.setForeground(new java.awt.Color(0, 0, 0));
        mulButton.setText("*");
        mulButton.setBorderPainted(false);
        mulButton.setPreferredSize(new java.awt.Dimension(72, 26));
        mulButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mulButtonActionPerformed(evt);
            }
        });

        plusButton.setBackground(new java.awt.Color(153, 153, 153));
        plusButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        plusButton.setForeground(new java.awt.Color(0, 0, 0));
        plusButton.setText("+");
        plusButton.setBorderPainted(false);
        plusButton.setPreferredSize(new java.awt.Dimension(72, 26));
        plusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusButtonActionPerformed(evt);
            }
        });

        minButton.setBackground(new java.awt.Color(153, 153, 153));
        minButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        minButton.setForeground(new java.awt.Color(0, 0, 0));
        minButton.setText("-");
        minButton.setBorderPainted(false);
        minButton.setPreferredSize(new java.awt.Dimension(72, 26));
        minButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minButtonActionPerformed(evt);
            }
        });

        cancelButton.setBackground(new java.awt.Color(153, 153, 153));
        cancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelButton.setForeground(new java.awt.Color(204, 0, 0));
        cancelButton.setText("Clear");
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(opButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(clButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dotButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Button9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eqlButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(delButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(divButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mulButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plusButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(minButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(divButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mulButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(plusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(Button9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Button6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Button3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eqlButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(opButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Button7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Button4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Button1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Button0, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(clButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(delButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Button8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Button5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Button2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(45, 45, 45))))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        display.setEditable(false);
        display.setBackground(new java.awt.Color(209, 206, 199));
        display.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        display.setForeground(new java.awt.Color(0, 0, 0));

        helpLbl.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        helpLbl.setForeground(new java.awt.Color(204, 204, 204));
        helpLbl.setText(" help? ");
        helpLbl.setToolTipText("");
        helpLbl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        helpLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpLblMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(display))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(helpLbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(clrButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(display, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clrButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(helpLbl))
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
   
    
    private void clrButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clrButtonActionPerformed
        record.setText(nullMessage);
    }//GEN-LAST:event_clrButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // cancel
        display.setText("0");
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void minButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minButtonActionPerformed

        btnLabel = "-";
        Run();
    }//GEN-LAST:event_minButtonActionPerformed

    private void plusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusButtonActionPerformed

        btnLabel = "+";
        Run();
    }//GEN-LAST:event_plusButtonActionPerformed

    private void mulButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mulButtonActionPerformed

        btnLabel = "*";
        Run();
    }//GEN-LAST:event_mulButtonActionPerformed

    private void divButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divButtonActionPerformed
        btnLabel = "/";
        Run();
    }//GEN-LAST:event_divButtonActionPerformed

    private void delButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButtonActionPerformed
        // xoa

        String displayString=display.getText();
        int len=displayString.length();
        boolean flag=false;
        for(int i=0;i<exceptMessages.length;i++){
            if(!displayString.equals(exceptMessages[i])) continue;
            flag=true;
            break;
        }
        if(len==1) displayString="0";
        else if(flag==true){
            //When infinite, NaN and other data exist
            displayString="0";
        } else if(len>0 &&displayString.lastIndexOf(" ")!=len-1 ){
            displayString=displayString.substring(0,len-1);
        }else if(len>0 &&displayString.substring(len-4,len-3).matches("[0-9]+")) displayString=displayString.substring(0,len-3);
        else if(len>0) displayString=displayString.substring(0,len-2);
        display.setText(displayString);
    }//GEN-LAST:event_delButtonActionPerformed

    private void eqlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eqlButtonActionPerformed
        btnLabel = "=";
        Run();
    }//GEN-LAST:event_eqlButtonActionPerformed

    private void Button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button3ActionPerformed
        btnLabel = "3";
        Run();
    }//GEN-LAST:event_Button3ActionPerformed

    private void Button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button6ActionPerformed
        btnLabel = "6";
        Run();
    }//GEN-LAST:event_Button6ActionPerformed

    private void Button9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button9ActionPerformed
        btnLabel = "9";
        Run();
    }//GEN-LAST:event_Button9ActionPerformed

    private void Button0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button0ActionPerformed
        btnLabel = "0";
        Run();
    }//GEN-LAST:event_Button0ActionPerformed

    private void Button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button1ActionPerformed
        btnLabel = "1";
        Run();
    }//GEN-LAST:event_Button1ActionPerformed

    private void Button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button4ActionPerformed
        btnLabel = "4";
        Run();
    }//GEN-LAST:event_Button4ActionPerformed

    private void Button7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button7ActionPerformed
        btnLabel = "7";
        Run();
    }//GEN-LAST:event_Button7ActionPerformed

    private void opButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opButtonActionPerformed

        btnLabel = "(";
        Run();
    }//GEN-LAST:event_opButtonActionPerformed

    private void dotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dotButtonActionPerformed
        btnLabel = ".";
        Run();
    }//GEN-LAST:event_dotButtonActionPerformed

    private void Button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button2ActionPerformed
        // TODO add your handling code here:
        btnLabel = "2";
        Run();
    }//GEN-LAST:event_Button2ActionPerformed

    private void Button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button5ActionPerformed
        btnLabel = "5";
        Run();
    }//GEN-LAST:event_Button5ActionPerformed

    private void Button8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button8ActionPerformed
        btnLabel = "8";
        Run();
    }//GEN-LAST:event_Button8ActionPerformed

    private void clButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clButtonActionPerformed
        btnLabel = ")";
        Run();
    }//GEN-LAST:event_clButtonActionPerformed

    private void helpLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpLblMouseClicked
        
//        UIManager UI=new UIManager();
//        UI.put("OptionPane.background",new Color(255,0,0));
//        UI.put("Panel.background", Color.RED);
        
        JOptionPane.showMessageDialog(null, "Insert your math method"
                + "\nHit \"=\""
                + "\nThen see your result!"
                + "\nBy NguyenThanhSang.", "How to use?", JOptionPane.PLAIN_MESSAGE);
        
    }//GEN-LAST:event_helpLblMouseClicked

    final String[] exceptMessages={"NaN","Infinity"};
        
 
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mainFr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainFr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainFr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainFr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainFr().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button0;
    private javax.swing.JButton Button1;
    private javax.swing.JButton Button2;
    private javax.swing.JButton Button3;
    private javax.swing.JButton Button4;
    private javax.swing.JButton Button5;
    private javax.swing.JButton Button6;
    private javax.swing.JButton Button7;
    private javax.swing.JButton Button8;
    private javax.swing.JButton Button9;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clButton;
    private javax.swing.JButton clrButton;
    private javax.swing.JButton delButton;
    private javax.swing.JTextField display;
    private javax.swing.JButton divButton;
    private javax.swing.JButton dotButton;
    private javax.swing.JButton eqlButton;
    private javax.swing.JLabel helpLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton minButton;
    private javax.swing.JButton mulButton;
    private javax.swing.JButton opButton;
    private javax.swing.JButton plusButton;
    private javax.swing.JTextArea record;
    // End of variables declaration//GEN-END:variables
}
