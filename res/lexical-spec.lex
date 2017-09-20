//type			regex

//keywords
IF				"if"
ELSE			"else"
FOR				"for"
WHILE			"while"

INT				"int"
FLOAT			"float"
BOOLEAN			"boolean"
STRING			"string"

PRINT			"print"

//constants
INT_CONST		"[0-9]+"
FLOAT_CONST		"[0-9]+(\\.[0-9]*)?"
BOOL_CONST		"true|false"
STRING_CONST	"\"(\\\\.|[^\"])*\""

//id
IDENTIFIER		"[a-zA-Z][a-zA-Z0-9]*"

//symbols
(				"\\("
)				"\\)"
{				"\\{"
}				"\\}"
,				","
;				";"
[				"\\["
]				"\\]"

//operators
=				"="
+=				"\\+="
-=				"-="
*=				"\\*="
/=				"/="
%=				"%="
+				"\\+"
-				"-"
*				"\\*"
/				"/"
%				"%"
++				"\\+\\+"
--				"--"
EQ_OP 			"=="
NEQ_OP			"!="
GE_OP			">="
LE_OP			"<="
AND_OP			"&&"
OR_OP			"\\|\\|"
>				">"
<				"<"
!				"!"