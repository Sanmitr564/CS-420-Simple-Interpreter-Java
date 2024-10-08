import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public interface OperatorEvaluation{
    Object evaluate(Stack<Object> stack, int line) throws Exception;
    int precedence();
}

class Addition implements OperatorEvaluation{
    @Override
    public Integer evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to add incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) x + (Integer) y;
    }

    @Override
    public int precedence() {
        return 4;
    }
}

class Subtraction implements OperatorEvaluation{
    @Override
    public Integer evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to subtract incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) y - (Integer) x;
    }

    @Override
    public int precedence() {
        return 4;
    }
}

class Multiplication implements OperatorEvaluation{
    @Override
    public Integer evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to multiply incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) x + (Integer) y;
    }

    @Override
    public int precedence() {
        return 5;
    }
}

class Division implements OperatorEvaluation{
    @Override
    public Integer evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to divide incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) y / (Integer) x;
    }

    @Override
    public int precedence() {
        return 5;
    }
}

class Modulo implements OperatorEvaluation{
    @Override
    public Integer evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to mod incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) y % (Integer) x;
    }

    @Override
    public int precedence() {
        return 5;
    }
}

class Equality implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception{
        Object x = stack.pop();
        Object y = stack.pop();

        return x.equals(y);
    }

    @Override
    public int precedence() {
        return 2;
    }
}

class And implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Boolean) || !(y instanceof Boolean)){
            throw new Exception("Attempted to and incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Boolean) x && (Boolean) y;
    }

    @Override
    public int precedence() {
        return 1;
    }
}

class Or implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Boolean) || !(y instanceof Boolean)){
            throw new Exception("Attempted to or incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Boolean) x || (Boolean) y;
    }

    @Override
    public int precedence() {
        return 0;
    }
}

class NotEqual implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception{
        Object x = stack.pop();
        Object y = stack.pop();

        return !x.equals(y);
    }

    @Override
    public int precedence() {
        return 2;
    }
}

class Not implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        if(!(x instanceof Boolean)){
            throw new Exception("Attempted to and non boolean type " + x.getClass() + " on line " + line);
        }
        return !((Boolean) x);
    }

    @Override
    public int precedence() {
        return 6;
    }
}

class LessThan implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to compare incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) y < (Integer) x;
    }

    @Override
    public int precedence() {
        return 3;
    }
}

class LessOrEquals implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to compare incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) y <= (Integer) x;
    }

    @Override
    public int precedence() {
        return 3;
    }
}

class GreaterThan implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to compare incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) y > (Integer) x;
    }

    @Override
    public int precedence() {
        return 3;
    }
}

class GreaterOrEquals implements OperatorEvaluation{
    @Override
    public Boolean evaluate(Stack<Object> stack, int line) throws Exception {
        Object x = stack.pop();
        Object y = stack.pop();
        if(!(x instanceof Integer) || !(y instanceof Integer)){
            throw new Exception("Attempted to compare incompatible types " + x.getClass() + " and " + y.getClass() + " on line " + line);
        }
        return (Integer) y >= (Integer) x;
    }

    @Override
    public int precedence() {
        return 3;
    }
}

class OperatorEvaluationMap{
    public static final Map<String, OperatorEvaluation> map = new HashMap<>();
    static {
        map.put("+", new Addition());
        map.put("-", new Subtraction());
        map.put("*", new Multiplication());
        map.put("/", new Division());
        map.put("%", new Modulo());
        map.put("==", new Equality());
        map.put("&&", new And());
        map.put("||", new Or());
        map.put("!=", new NotEqual());
        map.put("!", new Not());
        map.put("<", new LessThan());
        map.put("<=", new LessOrEquals());
        map.put(">", new GreaterThan());
        map.put(">=", new GreaterOrEquals());
    }
}