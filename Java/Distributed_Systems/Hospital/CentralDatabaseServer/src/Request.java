import java.io.Serializable;

public class Request implements Serializable {
    private Operation operation;
    private Object data;

    public Request(Operation operation, Object data) {
        this.operation = operation;
        this.data = data;
    }

    public Operation getOperation() {
        return operation;
    }

    public Object getData() {
        return data;
    }
}