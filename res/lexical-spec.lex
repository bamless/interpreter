//type          regex

//keywords
IF              "if"
ELSE            "else"
FOR             "for"
WHILE           "while"
PRINT           "print"
RETURN          "return"

//type keywords
INT             "int"
FLOAT           "float"
BOOLEAN         "boolean"
STRING          "string"
VOID            "void"

//constants
INT_CONST       "[0-9]+"
FLOAT_CONST     "[0-9]+(\\.[0-9]*)?"
BOOL_CONST      "true|false"
STRING_CONST    "\"(\\\\.|[^\"])*\""

//identifier
IDENTIFIER      "[_a-zA-Z][_a-zA-Z0-9]*"

//symbols
(               "\\("
)               "\\)"
{               "\\{"
}               "\\}"
,               ","
;               ";"
[               "\\["
]               "\\]"

//assignment operators
=               "="
+=              "\\+="
-=              "-="
*=              "\\*="
/=              "/="
%=              "%="
+               "\\+"

//operators
-               "-"
*               "\\*"
/               "/"
%               "%"
!               "!"
++              "\\+\\+"
--              "--"
EQ_OP           "=="
NEQ_OP          "!="
GE_OP           ">="
LE_OP           "<="
AND_OP          "&&"
OR_OP           "\\|\\|"
>               ">"
<               "<"
