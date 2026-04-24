package skypixeldev.gamescene.parser.exception;

import skypixeldev.gamescene.interfaces.IWildcard;

public class ParsingWildcardException extends ParsingFunctionException{
    public ParsingWildcardException(IWildcard card){
        super("The wildcard " + card.getClass().getName() + " caused an exception");
    }
}
