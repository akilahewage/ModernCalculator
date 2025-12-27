package com.inventory;

import java.util.Stack;

public class Evaluator {

    public static double evaluate(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') continue;

            // 1. Handle Numbers (Digits and Decimals)
            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                StringBuilder sbuf = new StringBuilder();
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sbuf.toString()));
                i--;
            }
            // 2. Handle '%' (Hybrid Logic)
            else if (tokens[i] == '%') {
                // Check if '%' is acting as an operator between two numbers (e.g., 2791 % 3)
                boolean isBinary = false;
                int k = i + 1;
                while (k < tokens.length && tokens[k] == ' ') k++; // Skip spaces

                if (k < tokens.length) {
                    char nextChar = tokens[k];
                    // If followed by a number, dot, or bracket, it's likely a Binary Operator
                    if ((nextChar >= '0' && nextChar <= '9') || nextChar == '.' || nextChar == '(') {
                        isBinary = true;
                    }
                }

                if (isBinary) {
                    // Case A: Binary Operator (Calculates "Percent Of")
                    // Example: 2791 % 3 -> 2791 * 0.03 = 83.73
                    while (!ops.empty() && hasPrecedence('%', ops.peek())) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.push('%');
                } else {
                    // Case B: Unary Operator (Calculates "Value / 100")
                    // Example: 100 x 30% -> 100 x 0.3 = 30
                    if (!values.isEmpty()) {
                        double val = values.pop();
                        values.push(val / 100);
                    }
                }
            }
            // 3. Handle Brackets
            else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            }
            else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();
            }
            // 4. Handle Standard Operators (+ - × ÷)
            else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '×' || tokens[i] == '÷') {
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(tokens[i]);
            }
        }

        // Apply remaining operators in the stack
        while (!ops.empty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    // Returns true if 'op1' has higher or equal precedence to 'op2'
    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        // ×, ÷, and % (binary) have higher precedence than + and -
        if ((op1 == '×' || op1 == '÷' || op1 == '%') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private static double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '×': return a * b; // Multiplication
            case '÷':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b; // Division
            case '%':
                // Binary Percent Logic: "b percent of a"
                // 2791 % 3 -> 2791 * (3/100) = 83.73
                return a * (b / 100);
        }
        return 0;
    }
}