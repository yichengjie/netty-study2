package com.yicj.iterator.aggregate;

import com.yicj.iterator.entity.Book;
import com.yicj.iterator.iterator.BookShelfIterator;
import com.yicj.iterator.iterator.Iterator;

public class BookShelf implements Aggregate {
    private Book[] books ;
    private int last ;
    public BookShelf(int maxSize){
        this.books = new Book[maxSize] ;
    }
    public Book getBookAt(int index){
        return books[index] ;
    }
    public void appendBook(Book book){
        this.books[last] = book ;
        this.last ++ ;
    }

    public int getLength(){
        return last ;
    }

    @Override
    public Iterator iterator() {
        return new BookShelfIterator(this);
    }
}
