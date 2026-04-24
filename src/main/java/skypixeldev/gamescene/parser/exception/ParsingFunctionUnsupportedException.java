package skypixeldev.gamescene.parser.exception;

public class ParsingFunctionUnsupportedException extends RuntimeException{
    public ParsingFunctionUnsupportedException(String s){
        super(s);
    }

    public ParsingFunctionUnsupportedException(){
        this("Parsing Exception occurred, please check the syntax.");
    }
}
