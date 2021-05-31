package com.example.ecomarket.Utilities;

public class Transforms {
    public Transforms(){}

    public double StringToDouble(String string){
        double result = 0;
        int toDot = 0;
        boolean sup = false;

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '.'){
                toDot = i;
                break;
            }
        }

        for (int i = 1; i < string.length(); i++) {
            if(!sup) {
                sup = string.charAt(i) == '.';
                result += (string.charAt(i-1) - 48) * Math.pow(10.0, toDot - i);
            }
            else
                result += (string.charAt(i)-48) * Math.pow(10.0, toDot-i);
        }

        return result;
    }
}
