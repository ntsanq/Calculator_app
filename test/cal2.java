


import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;

public class cal2 {
    public static void main(String[] args) { new CalculatorFrame("Calculator"); }
}
//Calculator window
class CalculatorFrame extends Frame{
    private final int height=400;
    private final int width=700;
    private final int offset_x=20;
    private final int offset_y=40;
    private TextArea display,record;//Display and history
    private String nullMessage="No history yet...";
    //Component value
    private final String[] values={"(",")","C","Back","7","8","9","/","4","5","6","*","1","2","3","+","0",".","=","-"};

    //Constructor, this calculator entry
    public CalculatorFrame(String title){
        //this.setTitle(title);
        super(title);//Set calculator name
        setMainFrame();//Window basic settings
        setButton();//Setup button
    }

    private void setMainFrame(){
        this.setLayout(null);//Set window layout to empty
        this.setBounds(400,200,width,height);//Set display position and window size
        this.setResizable(false);//Window resizable
        this.setVisible(true);//Window visibility
        //Close window listening event
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //Setting the display
        display=new TextArea("0",8,52,3);//textarea
        display.setBounds(offset_x,offset_y,(int)(width*0.7),(int)(height*0.3));
        display.setEditable(false);
        display.setBackground(new Color(230, 230, 230));
        display.setFont(new Font("Young circle",Font.BOLD,15));
        this.add(display);
        //Set history
        record=new TextArea(nullMessage,52,52,3);
        int left=offset_x+(int)(width*0.7);
        record.setBounds(left,offset_y,width-left,height-offset_y);
        record.setEditable(false);
        record.setBackground(new Color(230, 230, 230));
        record.setFont(new Font("Young circle",Font.TYPE1_FONT,10));
        //Clear history button
        JButton delBtn=new JButton(new ImageIcon("src/images/del.png"));//Add background image to button
        delBtn.setBounds(width-40,height-40,20,20);//Button size, and positioning
        delBtn.setBackground(new Color(255,255,255));//Set a default background color
        delBtn.addMouseListener(new MouseAdapter(){
            @Override
            //Click to clear history
            public void mousePressed(MouseEvent e){
                record.setText(nullMessage);
            }
        });
        this.add(delBtn);
        this.add(record);
    }
    private void setButton(){
        //Display
        int btn_width=(int)(width*0.7)/4;
        int btn_height=(int)(height*0.7-offset_y)/5;
        for(int i=0;i<values.length;i++){
            addButton(values[i],offset_x+btn_width*(i%4),5+offset_y+(int)(height*0.3)+btn_height*(int)(Math.floor(i/4)));
        }
    }

    private void addButton(String val,int x,int y){
        final Button btn=new Button(val);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBounds(x,y,(int)(width*0.7)/4-5,(int)(height*0.7-offset_y)/5-5);
        btn.setFont(new Font("DFKai-SB", Font.BOLD, 15));
        //Add click event
        btn.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                final String[] exceptMessages={"NaN","Infinity"};
                Button Btn=(Button)e.getSource();//Get the clicked button object
                String value=Btn.getLabel();//Button value
                String expression=display.getText();//Get the expression of the display
                int len=expression.length();//Because expression length is often used, it is declared in advance here
                switch (value){
                    case "C":
                        display.setText("0");
                        break;
                    case "Back":
                        boolean flag=false;
                        for(int i=0;i<exceptMessages.length;i++){
                            if(!expression.equals(exceptMessages[i])) continue;
                            flag=true;
                            break;
                        }
                        if(len==1) expression="0";
                        else if(flag==true){
                            //When infinite, NaN and other data exist
                            expression="0";
                        } else if(len>0 &&expression.lastIndexOf(" ")!=len-1 ){
                            expression=expression.substring(0,len-1);
                        }else if(len>0 &&expression.substring(len-4,len-3).matches("[0-9]+")) expression=expression.substring(0,len-3);
                        else if(len>0) expression=expression.substring(0,len-2);
                        display.setText(expression);
                        break;
                    case "+":
                    case "-":
                    case "*":
                    case "/":
                        if(expression.lastIndexOf(" ")==len-1 &&!expression.substring(len-2,len-1).equals("(")
                                &&!expression.substring(len-2,len-1).equals(")")){
                            expression=expression.substring(0,len-3);
                        }else if(len>1&& expression.substring(len-2,len-1).equals("(")){
                            expression+="0";
                        }
                        expression+=" "+value+" ";
                        display.setText(expression);
                        break;
                    case "(":
                    case ")":
                        if(value.equals(")") &&hasNums(expression,"(")<=hasNums(expression,")")) break;
                        else if(value.equals(")") &&expression.lastIndexOf(" ")==len-1 &&expression.lastIndexOf("(")!=len-2){
                            //If the shape is like 2 + (5 +, add a number automatically
                            expression=autoAdd(expression);
                            len+=1;
                        }
                        if(expression.equals("0")) expression="";
                        else if(value=="(" &&expression.lastIndexOf(" ")!=len-1) expression+=" *";//5 * (2 + 3) default to a * sign
                        if(expression.lastIndexOf(" ")==len-1) expression=expression.substring(0,len-1);//Clear preceding spaces
                        if(len>1 &&value.equals("(") && expression.lastIndexOf(")")==len-2){
                            //Form (a + b) × a d d one in the middle (c + D)*
                            expression+=" *";
                            len+=2;
                        }
                        expression+=" "+value+" ";
                        display.setText(expression);
                        break;
                    case ".":
                        if(expression.lastIndexOf(")")!=-1 && expression.lastIndexOf(")")==len-2) expression+="* 0"+value;//brackets
                        else if(expression.lastIndexOf(" ")==len-1)expression+="0"+value;
                        else if(expression.lastIndexOf(".")!=-1 &&expression.lastIndexOf(".")>expression.lastIndexOf(" ")) break;//Enter two in a row.
                        else expression+=value;
                        display.setText(expression);
                        break;
                    case "=":
                        if(hasNums(expression,"(")>hasNums(expression,")")){
                            //gapNum brackets missing
                            expression+=expression.lastIndexOf(" ")==len-1?"+ 0":" + 0";
                            int gapNum=hasNums(expression,"(")-hasNums(expression,")");
                            for(int i=0;i<gapNum;i++){
                                expression+=" ) ";
                            }
                        }
                        else if(expression.lastIndexOf(" ")==len-1 && expression.lastIndexOf(")")!=len-2){
                            //When the last one is an operator, add 0 or 1 to the default expression
                            expression=autoAdd(expression);
                        }
                        cal(expression);
                        break;
                    default:
                        //exceptMessages
                        if(expression.substring(len-1).matches("[a-zA-Z]+")){
                            expression="0";
                        }
                        //number
                        if(expression.equals("0")) expression="";
                        if(expression.lastIndexOf(" ")==len-1&&expression.lastIndexOf(")")==len-2) expression+="* ";
                        expression+=value;
                        display.setText(expression);
                }
            }
        });
        this.add(btn);
    }

    //Calculation
    private void cal(String str){
        //Get a new expression
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

    //0.0+5*(5+0.5)*0.5
    //Get a new expression
    private LinkedList<String> getNewExpre(String[] expression){
        for(int i=0;i<expression.length;i++){
            System.out.print(expression[i]+",");
        }
        System.out.println();
        LinkedList<String> symbols=new LinkedList<>();//Stack for symbols
        LinkedList<String> newExpre=new LinkedList<>();//Stack for new expressions
        for (int i=0;i<expression.length;i++){
            String val=expression[i];
            if(val.equals("")) continue;
            System.out.println(symbols);
            System.out.println(newExpre);
            switch (val){
                case "(":symbols.add(val);break;
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

    //Compare symbol priority
    private boolean compareSymbols(String newSymbol,String existSymbol){
        //Only +, -, *,/
        //false indicates that the last element of the symbol stack needs to be pushed out if the priority of the former is smaller or equal.
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

    //Determine how many val are in str
    private int hasNums(String str,String val){
        int nums=0;
        while (str.indexOf(val)!=-1){
            nums+=1;
            str=str.substring(str.indexOf(val)+1);
        }
        return nums;
    }
    //Common error in expression, add 0 or 1 after it automatically
    private String autoAdd(String expression){
        String symbol=expression.substring(expression.length()-2,expression.length()-1);
        switch (symbol){
            case "+":
            case "-":
                expression+="0";
                break;
            case "*":
            default:
                expression+="1";
        }
        return expression;
    }
}