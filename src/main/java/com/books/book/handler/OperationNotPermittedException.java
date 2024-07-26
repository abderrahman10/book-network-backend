package com.books.book.handler;

public class OperationNotPermittedException extends  RuntimeException {

    public OperationNotPermittedException(String msg ){
      super(msg);//calling the constructor of the parent class RuntimeException
        //remember : when you have an exception you need to handle it  , and i handle it in the handler
    }
}
