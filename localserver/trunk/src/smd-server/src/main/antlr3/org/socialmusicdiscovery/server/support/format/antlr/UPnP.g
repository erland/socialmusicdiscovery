grammar UPnP;

@header {
package org.socialmusicdiscovery.server.support.format.antlr;

}

@lexer::header {
package org.socialmusicdiscovery.server.support.format.antlr;
}
//options {backtrack=true;}

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) // {$channel=HIDDEN;}
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;


/*
 upnp:class = "object.item.imageItem.photo" and ( dc:date >= "2005-10-01" and dc:date <= "2005-10-31" )
 upnp:class derivedfrom "object.container.playlistContainer" and @refID exists false
 upnp:class = "object.item.imageItem.photo" and dc:date >= "2005-10-01" and dc:date <= "2005-10-31"
 upnp:class derivedfrom "object.item"
 upnp:artist="Sting" and upnp:album="Gold"
 upnp:artist="Sting" or upnp:album="Gold" and upnp:genre exists false
 (upnp:artist="Sting" or upnp:album="Gold") and upnp:genre exists false
 (dc:date <= "1999-12-31" and dc:date >= "1990-01-01")
 (upnp:class = audioItem ) and ( dc:title contains "filter")
 (upnp:class = audioItem ) and ( upnp:artist contains "filter")
 upnp:class derivedfrom "object.item.videoItem.sas" and @refID exists false
 upnp:class derivedfrom "object.container.playlistContainer" and @refID exists false
*/
// cf: http://www.koders.com/cpp/fid3FDA79800371263A429016D094CBB4C75CA5C779.aspx?s=search#L11
// http://gmrender.nongnu.org/  http://chorale.sourceforge.net/
// searchExp : relExp (  WS+ logOp  WS+ relExp )? ;
// searchExp : relExp | searchExp WS+ logOp WS+ searchExp| '(' WS* searchExp WS* ')';
// searchExp : relExp | searchExp2 WS+ logOp WS+ searchExp2| '(' WS* searchExp2 WS* ')';) (WS+ logOp WS+ searchExp)*) (WS+ logOp WS+ searchExp)*
// searchExp2 : relExp | searchExp WS+ logOp WS+ searchExp| '(' WS* searchExp WS* ')';

// searchCrit : searchExp | asterisk ;
// searchExp : relExp 
// 	| searchExp WS+ logOp WS+ searchExp
// 	| '(' WS* searchExp WS* ')';

// searchCrit : searchExp | asterisk ;
// searchExp : (relExp | '(' WS* searchExp WS* ')' ) (WS+ logOp WS+ searchExp)*;
// searchExp : (relExp | '(' WS* searchExp WS* ')' ) 
// 	| relExp  (WS+ logOp WS+ searchExp)*;

// (upnp:class = audioItem ) and ( upnp:artist contains "filter")
// upnp:artist="Sting" and upnp:album="Gold"
// upnp:class = "object.item.imageItem.photo" and ( dc:date >= "2005-10-01" and dc:date <= "2005-10-31" )
// upnp:class derivedfrom "object.container.playlistContainer" and @refID exists false
// upnp:class = "object.item.imageItem.photo" and dc:date >= "2005-10-01" and dc:date <= "2005-10-31"
// upnp:class derivedfrom "object.item"
// upnp:artist="Sting" and upnp:album="Gold"
// upnp:artist="Sting" or upnp:album="Gold" and upnp:genre exists false
// (upnp:artist="Sting" or upnp:album="Gold") and upnp:genre exists false
// (dc:date <= "1999-12-31" and dc:date >= "1990-01-01")
// (upnp:class = audioItem ) and ( dc:title contains "filter")
// (upnp:class = audioItem ) and ( upnp:artist contains "filter")

searchCrit : searchExp (WS+ logOp WS+ searchExp)* | asterisk ;
searchExp 	: relExp
		| '(' WS* relExp (WS+ logOp WS+ searchExp)* WS* ')'
		;

relExp :  prop binOp (quotedVal|objType)
	| prop WS+ existsOp WS+ boolVal;

quotedVal: STRING;
binOp : WS* relOp WS* | WS+ stringOp WS+ ;
relOp : '=' | '!=' | '<' | '<=' | '>' | '>=';
stringOp : 'contains' | 'doesNotContain' | 'derivedfrom';
existsOp : 'exists';
boolVal : 'true' | 'false';
logOp : 'and' | 'or';
dQuote : '"';
asterisk : '*';
prop : 'upnp:class' | 'upnp:genre' | 'upnp:artist' | 'upnp:album' | 'upnp:originalTrackNumber' | 'dc:title' | 'dc:date' | 'dc:creator' | 'res' | 'res@size' | '@id' | '@refID' | '@parentId';
objType : 'audioItem';
omg : 'aze' WS+ 'aze';