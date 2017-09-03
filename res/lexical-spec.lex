//type			regex

//keywords
IF				"if"
ELSE			"else"
INT				"int"
FLOAT			"float"
FOR				"for"
BOOLEAN			"boolean"
WHILE			"while"

//constants
INT_CONST		"[0-9]+"
FLOAT_CONST		"[0-9]+(\\.[0-9]*)?"
BOOL_CONST		"true|false"
STRING			"\"(\\.|[^\"])*\""

//id
IDENTIFIER		"[a-zA-Z][a-zA-Z0-9]*"

//symbols
(				"\\("
)				"\\)"
{				"\\{"
}				"\\}"
,				","
;				";"

//operators
=				"="
+				"\\+"
-				"-"
*				"\\*"
/				"/"
%				"%"
EQ_OP 			"=="
NEQ_OP			"!="
GE_OP			">="
LE_OP			"<="
AND_OP			"&&"
OR_OP			"\\|\\|"
>				">"
<				"<"
!				"!"