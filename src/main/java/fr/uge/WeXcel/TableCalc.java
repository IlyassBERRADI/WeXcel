package fr.uge.WeXcel;

import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class TableCalc {
    public String calculateFormula(String form, String nameTable, int idTable){
        var pattern = Pattern.compile("^=(\\(|\\)|\\+|\\*|-|/|%|\b[A-Z]+\\([^)]*\\)|[A-Z0-9.]+)");
        var matcher = pattern.matcher(form);
        Deque<String> stackOperator = new LinkedList<>();
        Deque<String> stackOperand = new LinkedList<>();

        while(matcher.find()) {
            var group = matcher.group();
            if ("*".equals(group) | "/".equals(group) | "%".equals(group)){
                while ("*".equals(stackOperator.peek()) || "/" .equals(stackOperator.peek())|| "%".equals(stackOperator.peek())){
                    stackOperand.push(stackOperator.pop());
                }
                stackOperator.push(group);

            }
            else if ("+".equals(group) | "-".equals(group)){
                while ("*".equals(stackOperator.peek()) || "/".equals(stackOperator.peek()) || "%".equals(stackOperator.peek()) ||
                        "+".equals(stackOperator.peek()) || "-".equals(stackOperator.peek())){
                    stackOperand.push(stackOperator.pop());
                }
                stackOperator.push(group);
            } else if ("=".equals(group)) {
                /*while (stackOperator.peek()=="*" || stackOperator.peek()=="/" || stackOperator.peek()=="%" ||
                        stackOperator.peek()=="+" || stackOperator.peek()=="-"){
                    stackOperand.push(stackOperator.pop());
                }*/
                stackOperand.push(group);
            } else if (group.indexOf('(') != -1) {
                switch (group.substring(0, group.indexOf('('))){
                    case "COUNT" -> count(group.substring(group.indexOf('(')+1, group.indexOf(')')));
                    case "AVERAGE" -> ;
                    case "SUM" ->;
                    default -> throw new IllegalArgumentException();
                }
            }
        }


        return  null;
    }

    public double average(String str, String nameTable, int idTable){
        String[] array;
        TableResource tr = new TableResource();
        if (str.indexOf(':')!=-1){
            array=str.split(":");
            var ind1 = getIndex(array[0].trim());
            var ind2 = getIndex(array[1].trim());
            double sum=0;
            for (int i = ind1.nbLine(); i <= ind2.nbLine(); i++) {
                var elt= tr.getCell(new Index(i, ind1.nbColumn()), nameTable, idTable);
                if (elt.getType().equals("Chaine")){
                    throw new IllegalArgumentException();
                } else if (elt.getType().equals("Formule")) {
                    var val = elt.getValue();
                    sum+=Double.parseDouble(val.substring(0, val.indexOf('=')).trim());
                }
                else {
                    sum+=Double.parseDouble(elt.getValue().trim());
                }

            }
            return sum/(ind2.nbLine()-ind1.nbLine());
        } else if (str.contains(",")) {
//            var pattern = Pattern.compile("\\d+\\.\\d*|[A-Z]+[0-9]+");
//            var matcher = pattern.matcher(str);
            array = str.split(",");
            double sum2=0;
            for (var elt2 :
                    array) {
                if (Character.isLetter(elt2.charAt(0))) {
                    var index = getIndex(elt2);
                    var cell = tr.getCell(index, nameTable, idTable);
                    
                }
            }

        }
    }

    public boolean contains(String form, String cell){

    }

    public boolean isFormula(){

    }

    public int count(String form){
        String[] array;
        if (form.indexOf(':')!=-1){
            array=form.split(":");
            var ind1 = getIndex(array[0].trim());
            var ind2 = getIndex(array[1].trim());
            return (ind2.nbLine()-ind1.nbLine()+1);
        } else if (form.contains(",")) {
            array = form.split(",");
            return array.length;
        }
        else {
            throw new IllegalArgumentException();
        }

    }

    public Index getIndex(String indexCell){
        int result = 0;
        int i=0;
        for ( i = 0; i < indexCell.length() && Character.isLetter(indexCell.charAt(i)); i++) {
            int value = indexCell.charAt(i) - 'A' + 1;
            result = result * 26 + value;
        }
        if (result==0 || i==indexCell.length()){
            throw new IllegalArgumentException();
        }
        var nbRow = Integer.parseInt(indexCell.substring(i).trim());

        return new Index(nbRow, result);
    }
}
