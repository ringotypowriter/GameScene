package skypixeldev.gamescene.parser.exception;

public class ParsingFunctionException extends RuntimeException{
    public ParsingFunctionException(String s){
        super(s);
    }

    public ParsingFunctionException(){
        this("Parsing Exception occurred, please check the syntax.");
    }
}
