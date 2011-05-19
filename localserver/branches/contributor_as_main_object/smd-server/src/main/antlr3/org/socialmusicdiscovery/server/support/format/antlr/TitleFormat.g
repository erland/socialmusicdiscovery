grammar TitleFormat;

@header {
package org.socialmusicdiscovery.server.support.format.antlr;
import org.socialmusicdiscovery.server.support.format.DataProvider;
import java.util.Map;
}
@lexer::header {
package org.socialmusicdiscovery.server.support.format.antlr;
}
@members {
    private DataProvider dataProvider;
    public TitleFormatParser(DataProvider dataProvider, TokenStream input) {
        this(input);
        this.dataProvider = dataProvider;
    }
    public TitleFormatParser(DataProvider dataProvider, TokenStream input, RecognizerSharedState state) {
        this(input, state);
        this.dataProvider = dataProvider;
    }
}

format [Map<String, Object> objects] returns [String value]
@init
{
    dataProvider.init(objects);
}
    :   e=expr {$value = $e.value;} ('||' e=expr {$value+=$e.value;})*;
expr returns [String value]
    :   e=orexpr {$value = $e.value;}
    |   e=ifexpr {$value = $e.value;}
    |   e=value {$value = $e.value;};
orexpr returns [String value]
    :   '(' e=expr {
                if($e.value!=null && $e.value.length()>0) {
                    $value = $e.value;
                }else {
                    $value = "";
                }
            }
        ('|' e=expr {
                if($value.length()==0 && $e.value!=null && $e.value.length()>0) {
                    $value = $e.value;
                }
            })*
        ')';
ifexpr returns [String value]
    :   '[' VARIABLE {
                Object tmp = dataProvider.getValue($VARIABLE.text);
                if(tmp!=null && tmp.toString().length()>0) {
                    $value = tmp.toString();
                }
            }
        ',' e=expr {
                if($value!=null && !$value.equals("")) {
                    $value = $e.value;
                }else {
                    $value = "";
                }
            }
        ']';
value returns [String value]
    :   VARIABLE    {
        Object tmp = dataProvider.getValue($VARIABLE.text);
        if(tmp!=null) {
            $value = tmp.toString();
        }else {
            $value =  "";
        }
    }
    |   TEXT        {$value = $TEXT.text;};

VARIABLE :'%' (DIGIT|UCLETTER|LCLETTER)+ ('.' (DIGIT|UCLETTER|LCLETTER)+)*;
TEXT :(DIGIT|UCLETTER|LCLETTER|WHITESPACE|SEPARATOR)+;
fragment WHITESPACE:    ' ';
fragment SEPARATOR:     ('-'|'_'|'.'|'&'|'#'|':'|'/'|'('|')'|'='|';');
fragment DIGIT:         '0'..'9';
fragment UCLETTER:      'A'..'Z';
fragment LCLETTER:      'a'..'z';
