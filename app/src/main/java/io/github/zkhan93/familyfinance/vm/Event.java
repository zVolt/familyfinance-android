package io.github.zkhan93.familyfinance.vm;

public class Event<T> {
    private T content;
    private boolean handled;
    public Event(T content){
        this.content = content;
        handled = false;
    }

    public T peekContent(){
        return content;
    }

    public T getContentIfNotHandled(){
        if(handled){
            return null;
        }else{
            handled = true;
            return content;
        }
    }
}
