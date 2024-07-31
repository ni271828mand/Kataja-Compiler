package com.github.ktj.compiler;

import java.util.Arrays;

final class TokenHandler{

    private final Token[][] token;
    private final String file;
    private final int lineOffset;
    private int line;
    private int i;

    TokenHandler(Token[][] token, String file, int lineOffset){
        this.token = token;
        this.file = file;
        this.lineOffset = lineOffset;
        line = 0;
        i = -1;
    }

    public Token next(){
        if(i + 1 < token[line].length){
            i++;
            return token[line][i];
        }else{
            while(line++ < token.length){
                if(token[line].length > 0){
                    i = 0;
                    return token[line][i];
                }
            }
        }
        throw new ParsingException("Expected Token got nothing", file, getLine());
    }

    public Token current(){
        if(i == -1 || i >= token[line].length) throw new ParsingException("Expected Token got nothing", file, getLine());
        return token[line][i];
    }

    public Token last(){
        if(i > 0){
            i--;
            return token[line][i];
        }else{
            while(--line >= 0){
                if(token[line].length > 0){
                    i = token[line].length - 1;
                    return token[line][i];
                }
            }
        }
        i = -1;
        line = 0;
        return null;
    }

    public Token assertToken(String...strings){
        Token t = next();

        for(String string:strings) if(t.equals(string)) return t;

        throw new ParsingException("Expected one of "+Arrays.toString(strings)+" got "+t.s, file, getLine());
    }

    public Token assertTokenTypes(Token.Type...types){
        Token t = next();

        for(Token.Type type:types) if(t.equals(type)) return t;

        throw new ParsingException("Expected one of "+Arrays.toString(types)+" got "+t.t, file, getLine());
    }

    public Token assertToken(Token.Type type, String...strings){
        Token t = next();

        if(t.equals(type)) return t;

        for(String string:strings) if(t.equals(string)) return t;

        throw new ParsingException("Expected one of "+type+", "+Arrays.toString(strings)+" got "+t.s, file, getLine());
    }

    public Token assertToken(Token.Type type1, Token.Type type2,String...strings){
        Token t = next();

        if(t.equals(type1)) return t;
        if(t.equals(type2)) return t;

        for(String string:strings) if(t.equals(string)) return t;

        throw new ParsingException("Expected one of "+type1+", "+type2+", "+Arrays.toString(strings)+" got "+t.s, file, getLine());
    }

    public void assertEndOfStatement(){
        if(i + 1 != token[line].length) assertToken(";");
    }

    public void assertHasNext(){
        next();
        last();
    }

    public boolean isNext(String string){
        if(!hasNext()) return false;
        if(next().equals(string)) return true;

        last();
        return false;
    }

    public boolean isNext(Token.Type type){
        if(next().equals(type)) return true;

        last();
        return false;
    }

    public boolean isEndOfStatement(){
        if(i + 1 == token[line].length) return true;

        return isNext(";");
    }

    public boolean hasNext(){
        String index = getIndex();
        if(line < token.length && i + 1 < token[line].length){
            setIndex(index);
            return true;
        }else{
            while(++line < token.length){
                if(token[line].length > 0){
                    setIndex(index);
                    return true;
                }
            }
        }
        setIndex(index);
        return false;
    }

    public int getLine(){
        if(line < 0) return lineOffset;
        return line + 1 + lineOffset;
    }

    public String getIndex(){
        return line +":"+i;
    }

    public void setIndex(String index){
        String[] s = index.split(":");
        line = Integer.parseInt(s[0]);
        i = Integer.parseInt(s[1]);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        for(Token[] line:token){
            for(Token token:line) sb.append(token).append(" ");
            sb.append("\n");
        }

        return sb.toString();
    }
}
