//type          regex

//keywords
IF              "if"       0
ELSE            "else"     0
FOR             "for"      0
WHILE           "while"    0
PRINT           "print"    0
PRINTLN         "println"  0
RETURN          "return"   0
CONTINUE        "continue" 0
BREAK           "break"    0

//type keywords
INT             "int"      0
FLOAT           "float"    0
BOOLEAN         "boolean"  0
STRING          "string"   0
VOID            "void"     0

//constants
INT_CONST       "[0-9]+"                         1
FLOAT_CONST     "([0-9]+([.][0-9]*)?|[.][0-9]+)" 1
BOOL_CONST      "true|false"                     1
STRING_CONST    "\"(\\\\.|[^\"])*\""             1
//identifier
IDENTIFIER      "[_a-zA-Z][_a-zA-Z0-9]*"         1

//symbols
(               "\\("     0
)               "\\)"     0
{               "\\{"     0
}               "\\}"     0
,               ","       0
;               ";"       0
[               "\\["     0
]               "\\]"     0

//assignment operators
=               "="       0
+=              "\\+="    0
-=              "-="      0
*=              "\\*="    0
/=              "/="      0
%=              "%="      0
+               "\\+"     0

//operators
-               "-"       0
*               "\\*"     0
/               "/"       0
%               "%"       0
!               "!"       0
++              "\\+\\+"  0
--              "--"      0
EQ_OP           "=="      0
NEQ_OP          "!="      0
GE_OP           ">="      0
LE_OP           "<="      0
AND_OP          "&&"      0
OR_OP           "\\|\\|"  0
>               ">"       0
<               "<"       0
