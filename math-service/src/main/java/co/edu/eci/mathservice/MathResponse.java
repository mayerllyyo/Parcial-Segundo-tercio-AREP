package co.edu.eci.mathservice;

public class MathResponse {

    private String operation;
    private int input;
    private String output;

    public MathResponse(String operation, int input, String output) {
        this.operation = operation;
        this.input = input;
        this.output = output;
    }

    public String getOperation() {
        return operation;
    }

    public int getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }
}
